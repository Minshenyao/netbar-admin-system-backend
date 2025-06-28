package edu.ahut.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String identity;
    
    private String phone;

    private double balance;

    private Integer status; // 1-正常，0-禁用
    
    @Column(name = "created_at")
    private Long createdAt;
    
    @Column(name = "updated_at")
    private Long updatedAt;
    
    // 用户权限: 99-系统管理员, 0-普通用户, 1-包月用户, 2-包年用户, 3-VIP用户
    private Integer permission;
    
    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
        status = 1;
        balance = 0.00;
        if (!Objects.equals(username, "admin")) {
            permission = 0; // 默认为普通用户
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
    
    // 判断用户是否为管理员
    @Transient
    public boolean isAdmin() {
        return permission != null && permission == 99;
    }
} 