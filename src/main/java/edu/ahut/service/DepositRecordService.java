package edu.ahut.service;

import edu.ahut.entity.DepositRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DepositRecordService {
    // 创建充值记录
    DepositRecord createDepositRecord(DepositRecord depositRecord);
    
    // 为用户充值并创建充值记录
    DepositRecord deposit(Long userId, Double amount, Integer paymentMethod, Long operatorId, String remark);
    
    // 根据ID获取充值记录
    Optional<DepositRecord> getDepositRecordById(Long id);
    
    // 获取用户的所有充值记录
    List<DepositRecord> getDepositRecordsByUserId(Long userId);
    
    // 分页获取用户的充值记录
    Page<DepositRecord> getDepositRecordsByUserId(Long userId, Pageable pageable);
    
    // 分页获取所有充值记录
    Page<DepositRecord> getAllDepositRecords(Pageable pageable);
    
    // 生成唯一订单号
    String generateOrderNumber();
} 