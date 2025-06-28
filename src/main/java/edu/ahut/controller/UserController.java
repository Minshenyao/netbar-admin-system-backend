package edu.ahut.controller;

import edu.ahut.common.Result;
import edu.ahut.context.UserContext;
import edu.ahut.entity.DepositRecord;
import edu.ahut.entity.Permission;
import edu.ahut.entity.User;
import edu.ahut.service.DepositRecordService;
import edu.ahut.service.PermissionService;
import edu.ahut.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private DepositRecordService depositRecordService;

    // 用户认证相关接口
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            return Result.success(registeredUser);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            if (username == null || password == null) {
                return Result.error("账号或密码为空");
            }
            User user = userService.login(username, password);
            
            // 从ThreadLocal中获取token
            String token = UserContext.getCurrentToken();
            
            // 构建返回结果，包含用户信息和令牌
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("token", token);
            
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<User> logout() {
        userService.logout();
        return Result.success(null);
    }

    // 用户基本信息管理接口
    @GetMapping("/profile")
    public Result<User> getUserProfile(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            User user = userService.getUserById(userId);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 管理员接口，需要从路径中获取用户ID
    @GetMapping("/{id}")
    public Result<User> getUserById(HttpServletRequest request, @PathVariable Long id) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            User user = userService.getUserById(id);
            return Result.success(user);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    // 更新当前用户信息，从JWT中获取用户ID
    @PutMapping("/profile")
    public Result<User> updateUserProfile(HttpServletRequest request, @RequestBody User user) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            user.setId(userId);
            User updatedUser = userService.updateUser(user);
            return Result.success(updatedUser);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 管理员接口，更新指定用户信息
    @PutMapping("/{id}")
    public Result<User> updateUser(HttpServletRequest request, @PathVariable Long id, @RequestBody User user) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return Result.success(updatedUser);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 删除指定用户，管理员接口
    @DeleteMapping("/{id}")
    public Result<User> deleteUser(HttpServletRequest request, @PathVariable Long id) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            userService.deleteUser(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    public Result<Page<User>> getAllUsers(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            Page<User> users = userService.getAllUsers(pageable);
            return Result.success(users);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 管理员接口，启用指定用户
    @PostMapping("/{id}/enable")
    public Result<User> enableUser(HttpServletRequest request, @PathVariable Long id) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            userService.enableUser(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 管理员接口，禁用指定用户
    @PostMapping("/{id}/disable")
    public Result<User> disableUser(HttpServletRequest request, @PathVariable Long id) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            userService.disableUser(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 管理员接口，更新指定用户状态
    @PutMapping("/{id}/status")
    public Result<User> updateUserStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Map<String, Integer> status) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            userService.updateUserStatus(id, status.get("status"));
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 管理员接口，更新指定用户权限
    @PutMapping("/{id}/permission")
    public Result<User> updateUserPermission(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Map<String, Integer> permissionMap) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            Integer permissionCode = permissionMap.get("permissionCode");
            if (permissionCode == null) {
                return Result.error("权限参数不能为空");
            }
            
            // 检查权限编码是否存在
            if (!permissionService.existsByCode(permissionCode)) {
                return Result.error("权限编码不存在");
            }
            
            userService.updateUserPermission(id, permissionCode);
            User updatedUser = userService.getUserById(id);
            return Result.success(updatedUser);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 管理员接口，更新指定用户余额
    @PutMapping("/{id}/balance")
    public Result<User> updateUserBalance(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Map<String, Double> balance) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            userService.updateUserBalance(id, balance.get("balance"));
            DepositRecord depositRecord = new DepositRecord();
            depositRecord.setUser(userService.getUserById(id));
            depositRecord.setAmount(balance.get("balance"));
            depositRecord.setOperatorId(currentUserId);
            depositRecord.setPaymentMethod(1);
            depositRecord.setRemark("管理员重置用户余额");
            depositRecordService.createDepositRecord(depositRecord);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 更新当前用户余额，从JWT中获取用户ID
    @PostMapping("/balance/add")
    public Result<User> addBalanceForCurrentUser(
            HttpServletRequest request,
            @RequestBody Map<String, Object> amount) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Number rawAmount = (Number) amount.get("amount");
            double amountValue = rawAmount.doubleValue();
            userService.addBalance(userId, amountValue);
            DepositRecord depositRecord = new DepositRecord();
            depositRecord.setUser(userService.getUserById(userId));
            depositRecord.setAmount(amountValue);
            depositRecord.setOperatorId(userId);
            depositRecord.setRemark(amount.get("remark").toString());
            depositRecord.setPaymentMethod((int) amount.get("paymentMethod"));
            depositRecordService.createDepositRecord(depositRecord);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 管理员接口，为指定用户添加余额
    @PostMapping("/{id}/balance/add")
    public Result<User> addBalance(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Map<String, Double> amount) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            userService.addBalance(id, amount.get("amount"));
            DepositRecord depositRecord = new DepositRecord();
            depositRecord.setUser(userService.getUserById(id));
            depositRecord.setAmount(amount.get("amount"));
            depositRecord.setOperatorId(currentUserId);
            depositRecord.setPaymentMethod(0);
            depositRecordService.createDepositRecord(depositRecord);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 扣除当前用户余额，从JWT中获取用户ID
    @PostMapping("/balance/deduct")
    public Result<User> deductBalanceForCurrentUser(
            HttpServletRequest request,
            @RequestBody Map<String, Double> amount) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            userService.deductBalance(userId, amount.get("amount"));
            DepositRecord depositRecord = new DepositRecord();
            depositRecord.setUser(userService.getUserById(userId));
            depositRecord.setAmount(amount.get("amount"));
            depositRecord.setOperatorId(userId);
            depositRecord.setRemark(amount.get("remark").toString());
            depositRecord.setPaymentMethod(amount.get("paymentMethod").intValue());
            depositRecordService.createDepositRecord(depositRecord);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 管理员接口，扣除指定用户余额
    @PostMapping("/{id}/balance/deduct")
    public Result<User> deductBalance(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Map<String, Double> amount) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            userService.deductBalance(id, amount.get("amount"));
            DepositRecord depositRecord = new DepositRecord();
            depositRecord.setUser(userService.getUserById(id));
            depositRecord.setAmount(amount.get("amount"));
            depositRecord.setOperatorId(currentUserId);
            depositRecord.setPaymentMethod(0);
            depositRecord.setRemark("管理员代为扣款");
            depositRecordService.createDepositRecord(depositRecord);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 更新当前用户密码，从JWT中获取用户ID
    @PostMapping("/password")
    public Result<User> updateCurrentUserPassword(
            HttpServletRequest request,
            @RequestBody Map<String, String> passwords) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            userService.updatePassword(
                userId,
                passwords.get("oldPassword"),
                passwords.get("newPassword")
            );
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 管理员接口，更新指定用户密码
    @PostMapping("/{id}/password")
    public Result<User> updatePassword(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody Map<String, String> passwords) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            userService.updatePassword(
                id,
                passwords.get("oldPassword"),
                passwords.get("newPassword")
            );
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 获取用户的折扣信息
    @GetMapping("/{id}/discount")
    public Result<Map<String, Object>> getUserDiscountInfo(
            HttpServletRequest request,
            @PathVariable Long id) {
        try {
            // 获取当前用户ID
            Long currentUserId = (Long) request.getAttribute("userId");
            
            // 非管理员只能查询自己的折扣信息
            if (!currentUserId.equals(id) && !userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            User user = userService.getUserById(id);
            Optional<Permission> permissionOpt = permissionService.getPermissionByCode(user.getPermission());
            
            if (permissionOpt.isEmpty()) {
                return Result.error("用户权限信息不存在");
            }
            
            Permission permission = permissionOpt.get();
            
            Map<String, Object> discountInfo = new HashMap<>();
            discountInfo.put("userId", user.getId());
            discountInfo.put("username", user.getUsername());
            discountInfo.put("permissionCode", permission.getCode());
            discountInfo.put("permissionName", permission.getName());
            discountInfo.put("discount", permission.getDiscount());
            discountInfo.put("description", permission.getDescription());
            
            return Result.success(discountInfo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 获取当前用户的折扣信息
    @GetMapping("/current/discount")
    public Result<Map<String, Object>> getCurrentUserDiscountInfo(HttpServletRequest request) {
        try {
            Long currentUserId = (Long) request.getAttribute("userId");
            return getUserDiscountInfo(request, currentUserId);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
} 