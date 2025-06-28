package edu.ahut.service;

import edu.ahut.entity.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    Permission createPermission(Permission permission);
    Permission updatePermission(Long id, Permission permission);
    void deletePermission(Long id);
    Optional<Permission> getPermissionById(Long id);
    Optional<Permission> getPermissionByCode(Integer code);
    Optional<Permission> getPermissionByName(String name);
    List<Permission> getAllPermissions();
    boolean existsByCode(Integer code);
    boolean existsByName(String name);
    // Get discount factor for a specific permission code
    double getDiscountByCode(Integer code);
} 