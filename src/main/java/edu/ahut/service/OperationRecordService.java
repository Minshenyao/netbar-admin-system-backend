package edu.ahut.service;

import edu.ahut.entity.Computer;
import edu.ahut.entity.OperationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OperationRecordService {
    // 创建操作记录
    OperationRecord createOperationRecord(OperationRecord operationRecord);
    
    // 根据ID获取操作记录
    Optional<OperationRecord> getOperationRecordById(Long id);
    
    // 获取用户的所有操作记录
    List<OperationRecord> getOperationRecordsByUserId(Long userId);
    
    // 分页获取用户的操作记录
    Page<OperationRecord> getOperationRecordsByUserId(Long userId, Pageable pageable);
    
    // 获取指定计算机的所有操作记录
    List<OperationRecord> getOperationRecordsByComputerId(Long computerId);
    
    // 分页获取指定计算机的操作记录
    Page<OperationRecord> getOperationRecordsByComputerId(Long computerId, Pageable pageable);
    
    // 获取用户在指定计算机上的所有操作记录
    List<OperationRecord> getOperationRecordsByUserIdAndComputerId(Long userId, Long computerId);
    
    // 分页获取用户在指定计算机上的操作记录
    Page<OperationRecord> getOperationRecordsByUserIdAndComputerId(Long userId, Long computerId, Pageable pageable);
    
    // 分页获取所有操作记录
    Page<OperationRecord> getAllOperationRecords(Pageable pageable);
    
    // 创建开机记录
    OperationRecord createPowerOnRecord(Long userId, Computer computer);
    
    // 创建关机记录
    OperationRecord createPowerOffRecord(Long userId, Computer computer);
    
    // 创建开始使用记录
    OperationRecord createStartUsingRecord(Long userId, Computer computer);
    
    // 创建结束使用记录
    OperationRecord createStopUsingRecord(Long userId, Computer computer, Long startTime, Double cost);
    
    // 创建设为维修记录
    OperationRecord createMaintenanceRecord(Long userId, Computer computer);
    
    // 创建取消维修记录
    OperationRecord createCancelMaintenanceRecord(Long userId, Computer computer);
} 