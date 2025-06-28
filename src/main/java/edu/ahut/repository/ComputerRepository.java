package edu.ahut.repository;

import edu.ahut.entity.Computer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long> {
    
    // 根据区域查询计算机
    List<Computer> findByArea(String area);
    
    // 根据区域和状态查询计算机
    List<Computer> findByAreaAndStatus(String area, Integer status);
    
    // 根据状态查询计算机
    List<Computer> findByStatus(Integer status);
    
    // 根据MAC地址查询计算机
    Optional<Computer> findByMacAddress(String macAddress);
    
    // 根据IP地址查询计算机
    Optional<Computer> findByIpAddress(String ipAddress);
    
    // 查询当前用户正在使用的计算机
    Optional<Computer> findByUserId(Long userId);
    
    // 查询各区域计算机数量
    @Query("SELECT c.area, COUNT(c) FROM Computer c GROUP BY c.area")
    List<Object[]> countComputersByArea();
    
    // 分页查询计算机
    Page<Computer> findAll(Pageable pageable);
    
    // 根据区域分页查询计算机
    Page<Computer> findByArea(String area, Pageable pageable);
    
    // 检查MAC地址是否存在
    boolean existsByMacAddress(String macAddress);
    
    // 检查IP地址是否存在
    boolean existsByIpAddress(String ipAddress);
} 