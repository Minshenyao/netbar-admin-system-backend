package edu.ahut.service;

import edu.ahut.entity.Computer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ComputerService {
    
    // 基础管理
    Computer createComputer(Computer computer);
    Computer getComputerById(Long id);
    Computer updateComputer(Computer computer);
    void deleteComputer(Long id);
    Page<Computer> getAllComputers(Pageable pageable);
    
    // 区域查询
    List<Computer> getComputersByArea(String area);
    Page<Computer> getComputersByArea(String area, Pageable pageable);
    Map<String, Long> countComputersByArea();
    
    // 状态管理
    List<Computer> getComputersByStatus(Integer status);
    List<Computer> getIdleComputers();
    List<Computer> getInUseComputers();
    List<Computer> getUnderMaintenanceComputers();
    List<Computer> getPowerOffComputers();
    
    // 操作
    Computer powerOn(Long id, Long userId);
    Computer powerOff(Long id, Long userId);
    Computer startUsing(Long id, Long userId);
    Computer stopUsing(Long id, Long userId);
    Computer setMaintenance(Long id, Long userId);
    
    // 特殊查询
    Computer getComputerByUserId(Long userId);
    
    // IP分配
    String generateIpAddress(String area);
    
    // 获取计算机总数
    long countComputers();
} 