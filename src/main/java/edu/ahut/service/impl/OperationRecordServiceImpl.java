package edu.ahut.service.impl;

import edu.ahut.entity.Computer;
import edu.ahut.entity.OperationRecord;
import edu.ahut.entity.User;
import edu.ahut.repository.OperationRecordRepository;
import edu.ahut.service.OperationRecordService;
import edu.ahut.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class OperationRecordServiceImpl implements OperationRecordService {

    @Autowired
    private OperationRecordRepository operationRecordRepository;
    
    @Autowired
    private UserService userService;

    @Override
    public OperationRecord createOperationRecord(OperationRecord operationRecord) {
        return operationRecordRepository.save(operationRecord);
    }
    
    @Override
    public Optional<OperationRecord> getOperationRecordById(Long id) {
        return operationRecordRepository.findById(id);
    }
    
    @Override
    public List<OperationRecord> getOperationRecordsByUserId(Long userId) {
        return operationRecordRepository.findByUserId(userId);
    }
    
    @Override
    public Page<OperationRecord> getOperationRecordsByUserId(Long userId, Pageable pageable) {
        return operationRecordRepository.findByUserId(userId, pageable);
    }
    
    @Override
    public List<OperationRecord> getOperationRecordsByComputerId(Long computerId) {
        return operationRecordRepository.findByComputerId(computerId);
    }
    
    @Override
    public Page<OperationRecord> getOperationRecordsByComputerId(Long computerId, Pageable pageable) {
        return operationRecordRepository.findByComputerId(computerId, pageable);
    }
    
    @Override
    public List<OperationRecord> getOperationRecordsByUserIdAndComputerId(Long userId, Long computerId) {
        return operationRecordRepository.findByUserIdAndComputerId(userId, computerId);
    }
    
    @Override
    public Page<OperationRecord> getOperationRecordsByUserIdAndComputerId(Long userId, Long computerId, Pageable pageable) {
        return operationRecordRepository.findByUserIdAndComputerId(userId, computerId, pageable);
    }
    
    @Override
    public Page<OperationRecord> getAllOperationRecords(Pageable pageable) {
        return operationRecordRepository.findAll(pageable);
    }
    
    @Override
    @Transactional
    public OperationRecord createPowerOnRecord(Long userId, Computer computer) {
        User user = userService.getUserById(userId);
        OperationRecord record = new OperationRecord();
        record.setUser(user);
        record.setComputer(computer);
        record.setOperationType(1); // 开机
        record.setStartTime(System.currentTimeMillis());
        return operationRecordRepository.save(record);
    }
    
    @Override
    @Transactional
    public OperationRecord createPowerOffRecord(Long userId, Computer computer) {
        User user = userService.getUserById(userId);

        OperationRecord record = new OperationRecord();
        record.setUser(user);
        record.setComputer(computer);
        record.setOperationType(2); // 关机
        record.setStartTime(System.currentTimeMillis());
        
        return operationRecordRepository.save(record);
    }
    
    @Override
    @Transactional
    public OperationRecord createStartUsingRecord(Long userId, Computer computer) {
        User user = userService.getUserById(userId);
        OperationRecord record = new OperationRecord();
        record.setUser(user);
        record.setComputer(computer);
        record.setOperationType(3); // 开始使用
        record.setStartTime(computer.getStartTime());
        record.setEndTime(computer.getEndTime());
        
        return operationRecordRepository.save(record);
    }
    
    @Override
    @Transactional
    public OperationRecord createStopUsingRecord(Long userId, Computer computer, Long startTime, Double cost) {
        User user = userService.getUserById(userId);

        OperationRecord record = new OperationRecord();
        record.setUser(user);
        record.setComputer(computer);
        record.setOperationType(4); // 结束使用
        record.setStartTime(startTime);
        record.setEndTime(System.currentTimeMillis());
        
        // 计算使用时长（分钟）
        long durationMillis = record.getEndTime() - startTime;
        int durationMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        record.setDuration(durationMinutes);
        
        // 设置消费金额
        record.setCost(cost);

        return operationRecordRepository.save(record);
    }
    
    @Override
    @Transactional
    public OperationRecord createMaintenanceRecord(Long userId, Computer computer) {
        User user = userService.getUserById(userId);

        OperationRecord record = new OperationRecord();
        record.setUser(user);
        record.setComputer(computer);
        record.setOperationType(5); // 设为维修
        record.setStartTime(System.currentTimeMillis());

        return operationRecordRepository.save(record);
    }
    
    @Override
    @Transactional
    public OperationRecord createCancelMaintenanceRecord(Long userId, Computer computer) {
        User user = userService.getUserById(userId);

        OperationRecord record = new OperationRecord();
        record.setUser(user);
        record.setComputer(computer);
        record.setOperationType(6); // 取消维修
        record.setStartTime(System.currentTimeMillis());

        return operationRecordRepository.save(record);
    }
} 