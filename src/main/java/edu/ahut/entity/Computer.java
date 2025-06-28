package edu.ahut.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "computers")
public class Computer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;  // 计算机名称
    
    @Column(nullable = false, unique = true)
    private String macAddress;  // MAC地址
    
    @Column(nullable = false, unique = true)
    private String ipAddress;  // IP地址
    
    @Column(nullable = false)
    private String area;  // 区域信息，如"A区"、"B区"等
    
    private Integer status;  // 计算机状态：0-关机，1-开机空闲，2-使用中，3-维修中
    
    private Long userId;  // 当前使用的用户ID，如果没有则为null
    
    private Long startTime;  // 当前用户开始使用的时间

    private Long endTime;   // 当前用户结束使用的时间
    
    @Column(name = "created_at")
    private Long createdAt;
    
    @Column(name = "updated_at")
    private Long updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
        status = 0;  // 初始状态为关机
        
        // 如果没有提供MAC地址，则自动生成
        if (macAddress == null || macAddress.isEmpty()) {
            macAddress = generateMacAddress();
        }
        
        // 如果没有提供计算机名，则自动生成
        if (name == null || name.isEmpty()) {
            name = "PC-" + area + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
    
    // 生成随机MAC地址
    private String generateMacAddress() {
        UUID uuid = UUID.randomUUID();
        String hexString = uuid.toString().replace("-", "").substring(0, 12);
        
        StringBuilder macAddress = new StringBuilder();
        for (int i = 0; i < hexString.length(); i += 2) {
            macAddress.append(hexString.substring(i, i + 2));
            if (i < hexString.length() - 2) {
                macAddress.append(":");
            }
        }
        
        return macAddress.toString().toUpperCase();
    }
    
    // 判断计算机是否空闲
    @Transient
    public boolean isIdle() {
        return status != null && status == 1;
    }
    
    // 判断计算机是否使用中
    @Transient
    public boolean isInUse() {
        return status != null && status == 2;
    }
    
    // 判断计算机是否关机
    @Transient
    public boolean isPowerOff() {
        return status != null && status == 0;
    }
    
    // 判断计算机是否在维修中
    @Transient
    public boolean isUnderMaintenance() {
        return status != null && status == 3;
    }
} 