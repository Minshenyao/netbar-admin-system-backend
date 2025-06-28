package edu.ahut.config;

import edu.ahut.entity.Computer;
import edu.ahut.entity.Permission;
import edu.ahut.entity.User;
import edu.ahut.service.ComputerService;
import edu.ahut.service.PermissionService;
import edu.ahut.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private ComputerService computerService;

    @Override
    public void run(String... args) {
        // 初始化权限数据
        initializePermissions();
        
        // 初始化管理员账号
        initializeAdminUser();
        
        // 初始化计算机
        initializeComputers();
    }
    
    private void initializePermissions() {
        // 系统管理员 - 免费上网
        if (!permissionService.existsByCode(99)) {
            Permission adminPermission = new Permission();
            adminPermission.setCode(99);
            adminPermission.setName("系统管理员");
            adminPermission.setDescription("系统管理员，拥有所有权限，上网免费");
            adminPermission.setDiscount(0.0); // 免费
            permissionService.createPermission(adminPermission);
        }
        
        // 普通用户 - 无折扣
        if (!permissionService.existsByCode(0)) {
            Permission regularPermission = new Permission();
            regularPermission.setCode(0);
            regularPermission.setName("普通用户");
            regularPermission.setDescription("普通用户，无折扣");
            regularPermission.setDiscount(1.0); // 无折扣
            permissionService.createPermission(regularPermission);
        }
        
        // 包月用户 - 9折
        if (!permissionService.existsByCode(1)) {
            Permission monthlyPermission = new Permission();
            monthlyPermission.setCode(1);
            monthlyPermission.setName("包月用户");
            monthlyPermission.setDescription("包月用户，享受9折优惠");
            monthlyPermission.setDiscount(0.9); // 9折
            permissionService.createPermission(monthlyPermission);
        }
        
        // 包年用户 - 7折
        if (!permissionService.existsByCode(2)) {
            Permission yearlyPermission = new Permission();
            yearlyPermission.setCode(2);
            yearlyPermission.setName("包年用户");
            yearlyPermission.setDescription("包年用户，享受7折优惠");
            yearlyPermission.setDiscount(0.7); // 7折
            permissionService.createPermission(yearlyPermission);
        }
        
        // VIP用户 - 5折
        if (!permissionService.existsByCode(3)) {
            Permission vipPermission = new Permission();
            vipPermission.setCode(3);
            vipPermission.setName("VIP用户");
            vipPermission.setDescription("VIP用户，享受5折优惠");
            vipPermission.setDiscount(0.5); // 5折
            permissionService.createPermission(vipPermission);
        }
    }
    
    private void initializeAdminUser() {
        // 检查是否已存在管理员账号
        User adminUser = userService.getUserByUsername("admin");
        if (adminUser == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // 初始密码
            admin.setIdentity("000000000000000000");
            admin.setPermission(99);
            admin.setPhone("13800000000");
            userService.register(admin);
        }
    }

    private void initializeComputers() {
        // 检查是否已有计算机
        if (computerService.countComputers() > 0) {
            return;
        }
        
        // 创建20台计算机，每5台一个区域
        String[] areas = {"A", "B", "C", "D"};
        
        for (int i = 0; i < 20; i++) {
        Computer computer = new Computer();
            String area = areas[i / 5]; // 每5台分配一个区域
            computer.setArea(area);
            computer.setStatus(0); // 初始状态为关机
            computer.setIpAddress("192.168.1." + (100 + i)); // 分配IP地址
            computer.setName("PC-" + area + "-" + (i % 5 + 1)); // 例如：PC-A-1
        computerService.createComputer(computer);
        }
    }
}