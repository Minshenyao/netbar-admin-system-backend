package edu.ahut.repository;

import edu.ahut.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(Integer code);
    Optional<Permission> findByName(String name);
    boolean existsByCode(Integer code);
    boolean existsByName(String name);
} 