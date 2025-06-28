package edu.ahut.service.impl;

import edu.ahut.entity.Computer;
import edu.ahut.entity.User;
import edu.ahut.repository.ComputerRepository;
import edu.ahut.service.ComputerService;
import edu.ahut.service.OperationRecordService;
import edu.ahut.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ComputerServiceImpl implements ComputerService {

    @Autowired
    private ComputerRepository computerRepository;

    @Autowired
    private OperationRecordService operationRecordService;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public Computer createComputer(Computer computer) {
        // 生成IP地址
        if (computer.getIpAddress() == null || computer.getIpAddress().isEmpty()) {
            computer.setIpAddress(generateIpAddress(computer.getArea()));
        } else if (computerRepository.existsByIpAddress(computer.getIpAddress())) {
            throw new RuntimeException("IP地址已存在");
        }
        
        // 检查MAC地址是否存在
        if (computer.getMacAddress() != null && !computer.getMacAddress().isEmpty() && 
                computerRepository.existsByMacAddress(computer.getMacAddress())) {
            throw new RuntimeException("MAC地址已存在");
        }
        
        return computerRepository.save(computer);
    }

    @Override
    public Computer getComputerById(Long id) {
        return computerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("计算机不存在"));
    }

    @Override
    @Transactional
    public Computer updateComputer(Computer computer) {
        Computer existingComputer = computerRepository.findById(computer.getId())
                .orElseThrow(() -> new RuntimeException("计算机不存在"));
        
        // 检查MAC地址是否存在
        if (computer.getMacAddress() != null && !computer.getMacAddress().isEmpty() && 
                !existingComputer.getMacAddress().equals(computer.getMacAddress()) && 
                computerRepository.existsByMacAddress(computer.getMacAddress())) {
            throw new RuntimeException("MAC地址已存在");
        }
        
        // 检查IP地址是否存在
        if (computer.getIpAddress() != null && !computer.getIpAddress().isEmpty() && 
                !existingComputer.getIpAddress().equals(computer.getIpAddress()) && 
                computerRepository.existsByIpAddress(computer.getIpAddress())) {
            throw new RuntimeException("IP地址已存在");
        }
        
        // 更新字段
        if (computer.getName() != null) {
            existingComputer.setName(computer.getName());
        }
        
        if (computer.getMacAddress() != null) {
            existingComputer.setMacAddress(computer.getMacAddress());
        }
        
        if (computer.getIpAddress() != null) {
            existingComputer.setIpAddress(computer.getIpAddress());
        }
        
        if (computer.getArea() != null) {
            existingComputer.setArea(computer.getArea());
        }
        
        if (computer.getStatus() != null) {
            existingComputer.setStatus(computer.getStatus());
        }
        
        return computerRepository.save(existingComputer);
    }

    @Override
    @Transactional
    public void deleteComputer(Long id) {
        Computer computer = computerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("计算机不存在"));
        
        // 检查计算机是否正在使用中
        if (computer.isInUse()) {
            throw new RuntimeException("计算机正在使用中，无法删除");
        }
        
        computerRepository.delete(computer);
    }

    @Override
    public Page<Computer> getAllComputers(Pageable pageable) {
        return computerRepository.findAll(pageable);
    }

    @Override
    public List<Computer> getComputersByArea(String area) {
        return computerRepository.findByArea(area);
    }

    @Override
    public Page<Computer> getComputersByArea(String area, Pageable pageable) {
        return computerRepository.findByArea(area, pageable);
    }

    @Override
    public Map<String, Long> countComputersByArea() {
        List<Object[]> results = computerRepository.countComputersByArea();
        Map<String, Long> countMap = new HashMap<>();
        
        for (Object[] result : results) {
            String area = (String) result[0];
            Long count = (Long) result[1];
            countMap.put(area, count);
        }
        
        return countMap;
    }

    @Override
    public List<Computer> getComputersByStatus(Integer status) {
        return computerRepository.findByStatus(status);
    }

    @Override
    public List<Computer> getIdleComputers() {
        return computerRepository.findByStatus(1);
    }

    @Override
    public List<Computer> getInUseComputers() {
        return computerRepository.findByStatus(2);
    }

    @Override
    public List<Computer> getUnderMaintenanceComputers() {
        return computerRepository.findByStatus(3);
    }

    @Override
    public List<Computer> getPowerOffComputers() {
        return computerRepository.findByStatus(0);
    }

    @Override
    @Transactional
    public Computer powerOn(Long id, Long userId) {
        Computer computer = computerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("计算机不存在"));
        
        if (!computer.isPowerOff()) {
            throw new RuntimeException("计算机不处于关机状态");
        }
        
        computer.setStatus(1); // 设置为开机状态
        operationRecordService.createPowerOnRecord(userId, computer);
        return computerRepository.save(computer);

    }

    @Override
    @Transactional
    public Computer powerOff(Long id, Long userId) {
        Computer computer = computerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("计算机不存在"));
        
        if (computer.isInUse()) {
            throw new RuntimeException("计算机正在使用中，无法关机");
        }
        
        computer.setStatus(0); // 设置为关机状态
        operationRecordService.createPowerOffRecord(userId, computer);
        return computerRepository.save(computer);
    }

    @Override
    @Transactional
    public Computer startUsing(Long id, Long userId) {
        Computer computer = computerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("计算机不存在"));
        
        if (!computer.isIdle()) {
            throw new RuntimeException("计算机不处于空闲状态");
        }
        
        // 检查用户是否已经在使用其他计算机
        computerRepository.findByUserId(userId).ifPresent(c -> {
            throw new RuntimeException("用户已经在使用其他计算机");
        });

        // 判断用户余额还能使用多长时间计算机
        // 获取用户余额
        User user = userService.getUserById(userId);
        double balance = user.getBalance();

        // 每分钟基础费用
        double baseRate = 0.1;

        // 获取用户折扣（如：0.9 表示打九折）
        double discount = userService.getUserDiscount(userId);

        // 实际每分钟费用（折扣后）
        double actualRate = baseRate * discount;

        // 计算最多可使用的分钟数（向下取整）
        int durationMinutes = (int) (balance / actualRate);

        // 如果可使用时间不足 1 分钟，可以选择直接报错或默认 1 分钟起步
        if (durationMinutes <= 0) {
            throw new RuntimeException("用户余额不足，无法开始使用计算机");
        }
        // 计算最终费用
//        double finalCost = durationMinutes * actualRate;

        // 扣除用户余额
//        userService.deductBalance(userId, finalCost);

        computer.setStatus(2); // 设置为使用中状态
        computer.setUserId(userId);
        computer.setStartTime(System.currentTimeMillis());
        computer.setEndTime(System.currentTimeMillis() + durationMinutes * 60 * 1000L);
        operationRecordService.createStartUsingRecord(userId, computer);
        return computerRepository.save(computer);
    }

    @Override
    @Transactional
    public Computer stopUsing(Long id, Long userId) {
        Computer computer = computerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("计算机不存在"));
        
        if (!computer.isInUse()) {
            throw new RuntimeException("计算机不处于使用中状态");
        }
        
        // 获取开始使用时间
        Long startTime = computer.getStartTime();
        Long endTime = computer.getEndTime();

        long currentTime = System.currentTimeMillis();
        if (currentTime > endTime) {
            throw new RuntimeException("余额不足，无进行无法扣费，请先充值");
        }
        // 计算使用时长（分钟）
        long durationMillis = currentTime - startTime;
        int durationMinutes = (int) (durationMillis / (1000 * 60));
        
        // 计算费用 - 假设每分钟0.1元
        double baseRate = 0.1;
        double baseCost = durationMinutes * baseRate;
        
        // 获取用户折扣
        double discount = userService.getUserDiscount(userId);
        
        // 计算折扣后费用
        double finalCost = baseCost * discount;
        
        // 从用户余额中扣除费用
        if (finalCost > 0) {
            userService.deductBalance(userId, finalCost);
        }
        
        // 更新计算机状态
        computer.setStatus(1); // 设置为空闲状态
        computer.setUserId(null); // 清除使用用户
        computer.setStartTime(null); // 清除开始时间
        
        // 创建操作记录
        operationRecordService.createStopUsingRecord(userId, computer, startTime, finalCost);
        
        return computerRepository.save(computer);
    }

    @Override
    @Transactional
    public Computer setMaintenance(Long id, Long userId) {
        Computer computer = computerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("计算机不存在"));
        
        if (computer.isInUse()) {
            throw new RuntimeException("计算机正在使用中，无法设置为维修状态");
        }
        
        computer.setStatus(3); // 设置为维修中状态
        operationRecordService.createMaintenanceRecord(userId, computer);
        return computerRepository.save(computer);
    }

    @Override
    public Computer getComputerByUserId(Long userId) {
        return computerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("未找到用户正在使用的计算机"));
    }

    @Override
    public String generateIpAddress(String area) {
        // 根据区域生成IP地址
        // 假设A区使用192.168.1.x，B区使用192.168.2.x，以此类推
        String prefix = "192.168.";
        int areaCode;
        
        // 将区域转换为区域代码
        switch (area.toUpperCase()) {
            case "A区":
                areaCode = 1;
                break;
            case "B区":
                areaCode = 2;
                break;
            case "C区":
                areaCode = 3;
                break;
            case "D区":
                areaCode = 4;
                break;
            case "VIP区":
                areaCode = 10;
                break;
            default:
                areaCode = new Random().nextInt(20) + 1; // 随机分配1-20
        }
        
        // 生成最后一段IP
        int maxAttempts = 100; // 最大尝试次数
        for (int i = 0; i < maxAttempts; i++) {
            int lastSegment = new Random().nextInt(253) + 2; // 2-254，避开0, 1, 255
            String ipAddress = prefix + areaCode + "." + lastSegment;
            
            // 检查IP是否已存在
            if (!computerRepository.existsByIpAddress(ipAddress)) {
                return ipAddress;
            }
        }
        
        throw new RuntimeException("无法分配IP地址，请尝试其他区域");
    }

    @Override
    public long countComputers() {
        return computerRepository.count();
    }
} 