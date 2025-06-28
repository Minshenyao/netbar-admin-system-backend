package edu.ahut.service.impl;

import edu.ahut.entity.DepositRecord;
import edu.ahut.entity.User;
import edu.ahut.repository.DepositRecordRepository;
import edu.ahut.service.DepositRecordService;
import edu.ahut.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class DepositRecordServiceImpl implements DepositRecordService {

    @Autowired
    private DepositRecordRepository depositRecordRepository;
    
    @Autowired
    private UserService userService;
    
    @Override
    public DepositRecord createDepositRecord(DepositRecord depositRecord) {
        // 如果没有提供订单号，生成一个
        if (depositRecord.getOrderNumber() == null || depositRecord.getOrderNumber().isEmpty()) {
            depositRecord.setOrderNumber(generateOrderNumber());
        }
        // 如果没有提供支付方式，默认为先现金支付
        if (depositRecord.getPaymentMethod() == 0) {
            depositRecord.setPaymentMethod(1);
        }
        if (depositRecord.getRemark() == null || depositRecord.getRemark().isEmpty()) {
            depositRecord.setRemark("管理员代为充值");
        }
        
        return depositRecordRepository.save(depositRecord);
    }
    
    @Override
    @Transactional
    public DepositRecord deposit(Long userId, Double amount, Integer paymentMethod, Long operatorId, String remark) {
        // 获取用户
        User user = userService.getUserById(userId);
        
        // 为用户增加余额
        userService.addBalance(userId, amount);
        
        // 创建充值记录
        DepositRecord depositRecord = new DepositRecord();
        depositRecord.setUser(user);
        depositRecord.setAmount(amount);
        depositRecord.setPaymentMethod(paymentMethod);
        depositRecord.setOperatorId(operatorId);
        depositRecord.setRemark(remark);
        depositRecord.setOrderNumber(generateOrderNumber());

        return depositRecordRepository.save(depositRecord);
    }
    
    @Override
    public Optional<DepositRecord> getDepositRecordById(Long id) {
        return depositRecordRepository.findById(id);
    }
    
    @Override
    public List<DepositRecord> getDepositRecordsByUserId(Long userId) {
        return depositRecordRepository.findByUserId(userId);
    }
    
    @Override
    public Page<DepositRecord> getDepositRecordsByUserId(Long userId, Pageable pageable) {
        return depositRecordRepository.findByUserId(userId, pageable);
    }
    
    @Override
    public Page<DepositRecord> getAllDepositRecords(Pageable pageable) {
        return depositRecordRepository.findAll(pageable);
    }
    
    @Override
    public String generateOrderNumber() {
        // 生成格式：年月日时分秒+4位随机数
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        
        // 生成4位随机数
        Random random = new Random();
        int randomNum = random.nextInt(10000);
        String randomStr = String.format("%04d", randomNum);
        
        return "D" + timestamp + randomStr;
    }
} 