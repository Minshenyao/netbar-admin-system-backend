package edu.ahut.service.impl;

import edu.ahut.entity.Permission;
import edu.ahut.repository.PermissionRepository;
import edu.ahut.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Permission updatePermission(Long id, Permission permission) {
        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
        
        existingPermission.setName(permission.getName());
        existingPermission.setDescription(permission.getDescription());
        existingPermission.setDiscount(permission.getDiscount());
        // We don't update the code as it's a system identifier
        
        return permissionRepository.save(existingPermission);
    }

    @Override
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }

    @Override
    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    @Override
    public Optional<Permission> getPermissionByCode(Integer code) {
        return permissionRepository.findByCode(code);
    }

    @Override
    public Optional<Permission> getPermissionByName(String name) {
        return permissionRepository.findByName(name);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public boolean existsByCode(Integer code) {
        return permissionRepository.existsByCode(code);
    }

    @Override
    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }

    @Override
    public double getDiscountByCode(Integer code) {
        return permissionRepository.findByCode(code)
                .map(Permission::getDiscount)
                .orElse(1.0); // Default to no discount if permission not found
    }
} 