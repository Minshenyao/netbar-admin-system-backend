package edu.ahut.controller;

import edu.ahut.common.Result;
import edu.ahut.context.UserContext;
import edu.ahut.entity.Permission;
import edu.ahut.entity.User;
import edu.ahut.service.PermissionService;
import edu.ahut.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private UserService userService;

    // Only admin can create permissions
    @PostMapping
    public Result<Permission> createPermission(@RequestBody Permission permission, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        
        User userOpt = userService.getUserById(userId);
        if (userOpt == null || !userService.isAdmin(userId)) {
            return Result.error("无权限");
        }
        
        if (permissionService.existsByCode(permission.getCode())) {
            return Result.error("权限编码已存在");
        }
        
        if (permissionService.existsByName(permission.getName())) {
            return Result.error("权限名称已存在");
        }
        
        Permission createdPermission = permissionService.createPermission(permission);
        return Result.success(createdPermission);
    }

    // Only admin can update permissions
    @PutMapping("/{id}")
    public Result<Permission> updatePermission(@PathVariable Long id, @RequestBody Permission permission, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        
        User userOpt = userService.getUserById(userId);
        if (userOpt == null || !userService.isAdmin(userId)) {
            return Result.error("无权限");
        }
        
        try {
            Permission updatedPermission = permissionService.updatePermission(id, permission);
            return Result.success(updatedPermission);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // Only admin can delete permissions
    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        
        User userOpt = userService.getUserById(userId);
        if (userOpt == null || !userService.isAdmin(userId)) {
            return Result.error("无权限");
        }
        
        try {
            permissionService.deletePermission(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // Get permission by ID
    @GetMapping("/{id}")
    public Result<Permission> getPermissionById(@PathVariable Long id) {
        Optional<Permission> permissionOpt = permissionService.getPermissionById(id);
        return permissionOpt.map(Result::success).orElseGet(() -> Result.error("权限不存在"));
    }

    // Get permission by code
    @GetMapping("/code/{code}")
    public Result<Permission> getPermissionByCode(@PathVariable Integer code) {
        Optional<Permission> permissionOpt = permissionService.getPermissionByCode(code);
        return permissionOpt.map(Result::success).orElseGet(() -> Result.error("权限不存在"));
    }

    // Get all permissions
    @GetMapping
    public Result<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return Result.success(permissions);
    }
} 