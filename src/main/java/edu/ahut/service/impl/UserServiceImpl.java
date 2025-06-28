package edu.ahut.service.impl;

import edu.ahut.context.UserContext;
import edu.ahut.entity.User;
import edu.ahut.repository.UserRepository;
import edu.ahut.service.PermissionService;
import edu.ahut.service.UserService;
import edu.ahut.util.JwtUtil;
import edu.ahut.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PermissionService permissionService;

//    @Autowired
//    private DepositRecordService depositRecordService;


    @Override
    @Transactional
    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(MD5Util.encrypt(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !MD5Util.verify(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(user);
        
        UserContext.setCurrentToken(token);
        return user;
    }

    @Override
    public User getCurrentUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public void logout() {
        // 获取当前令牌
        String token = UserContext.getCurrentToken();
        
        // 如果令牌存在，将其添加到黑名单
        if (token != null && !token.isEmpty()) {
            jwtUtil.addToBlacklist(token);
        }
        
        // 清除本地线程变量
        UserContext.clear();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 如果修改了用户名，需要检查新用户名是否已存在
        if (!existingUser.getUsername().equals(user.getUsername()) 
                && userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 更新基本信息
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getIdentity() != null) {
            existingUser.setIdentity(user.getIdentity());
        }
        if (user.getPhone() != null) {
            existingUser.setPhone(user.getPhone());
        }

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查用户是否有未完成的订单
//        if (user.getOperationRecords() != null && !user.getOperationRecords().isEmpty()) {
//            boolean hasActiveOperation = user.getOperationRecords().stream()
//                    .anyMatch(record -> record.getStatus() == 1);
//            if (hasActiveOperation) {
//                throw new RuntimeException("用户有未完成的上机记录，无法删除");
//            }
//        }
        
        userRepository.delete(user);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void enableUser(Long id) {
        updateUserStatus(id, 1);
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        updateUserStatus(id, 0);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUserBalance(Long id, double amount) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setBalance(amount);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void addBalance(Long id, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setBalance(user.getBalance() + (amount));
//        DepositRecord depositRecord = depositRecordService.createDepositRecord(new DepositRecord());
//        depositRecordService.deposit(id, amount, 1, depositRecord.getOperatorId(), "系统管理员充值");
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deductBalance(Long id, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("扣款金额必须大于0");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 根据用户权限获取折扣系数
        double discount = permissionService.getDiscountByCode(user.getPermission());
        double discountedAmount = amount * discount;
        
        // 如果是管理员，免费
        if (user.isAdmin()) {
            discountedAmount = 0;
        }
        
        if (user.getBalance() - discountedAmount < 0) {
            throw new RuntimeException("余额不足");
        }
        
        user.setBalance(user.getBalance() - discountedAmount);
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void updateUserPermission(Long id, Integer permission) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查权限编码是否存在
        if (!permissionService.existsByCode(permission)) {
            throw new RuntimeException("权限编码不存在");
        }
        
        user.setPermission(permission);
        userRepository.save(user);
    }
    
    @Override
    public boolean isAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElse(null);
        return user != null && user.isAdmin();
    }

    @Override
    @Transactional
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!MD5Util.verify(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(MD5Util.encrypt(newPassword));
        userRepository.save(user);
    }
    
    /**
     * 获取用户上网费用折扣系数
     * @param userId 用户ID
     * @return 折扣系数 (0~1.0)
     */
    public double getUserDiscount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 如果是管理员，免费
        if (user.isAdmin()) {
            return 0.0;
        }
        
        // 根据权限编码获取折扣系数
        return permissionService.getDiscountByCode(user.getPermission());
    }
} 