package edu.ahut.service;

import edu.ahut.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    // 基础用户管理
    User register(User user);
    User login(String username, String password);
    User getCurrentUser(Long userId);
    void logout();
    User getUserById(Long id);
    User getUserByUsername(String username);
    User updateUser(User user);
    void deleteUser(Long id);
    Page<User> getAllUsers(Pageable pageable);
    
    // 用户状态管理
    void enableUser(Long id);
    void disableUser(Long id);
    void updateUserStatus(Long id, Integer status);
    
    // 用户余额管理
    void updateUserBalance(Long id, double amount);
    void addBalance(Long id, double amount);
    void deductBalance(Long id, double amount);
    
    // 用户权限管理
    void updateUserPermission(Long id, Integer permission);
    boolean isAdmin(Long userId);
    double getUserDiscount(Long userId);
    
    // 用户查询
    void updatePassword(Long id, String oldPassword, String newPassword);
} 