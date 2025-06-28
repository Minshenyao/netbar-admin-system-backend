package edu.ahut.controller;

import edu.ahut.common.Result;
import edu.ahut.entity.DepositRecord;
import edu.ahut.service.DepositRecordService;
import edu.ahut.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/deposit-records")
public class DepositRecordController {

    @Autowired
    private DepositRecordService depositRecordService;
    
    @Autowired
    private UserService userService;

    // 创建充值记录并为用户充值 (仅管理员)
    @PostMapping
    public Result<DepositRecord> createDepositRecord(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {
        try {
            // 获取当前操作用户ID
            Long operatorId = (Long) request.getAttribute("userId");
            
            // 仅管理员可操作
            if (!userService.isAdmin(operatorId)) {
                return Result.error("权限不足");
            }
            
            Long userId = Long.parseLong(requestBody.get("userId").toString());
            Double amount = Double.parseDouble(requestBody.get("amount").toString());
            Integer paymentMethod = Integer.parseInt(requestBody.get("paymentMethod").toString());
            String remark = (String) requestBody.get("remark");
            
            // 验证金额
            if (amount <= 0) {
                return Result.error("充值金额必须大于0");
            }
            // 执行充值并创建记录
            DepositRecord depositRecord = depositRecordService.deposit(userId, amount, paymentMethod, operatorId, remark);
            return Result.success(depositRecord);
            
        } catch (Exception e) {
            return Result.error("充值失败: " + e.getMessage());
        }
    }
    
    // 获取指定ID的充值记录
    @GetMapping("/{id}")
    public Result<DepositRecord> getDepositRecordById(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        try {
            Long userId = (Long) request.getAttribute("userId");
            Optional<DepositRecord> recordOpt = depositRecordService.getDepositRecordById(id);
            
            if (recordOpt.isEmpty()) {
                return Result.error("充值记录不存在");
            }
            
            DepositRecord record = recordOpt.get();
            
            // 非管理员只能查看自己的充值记录
            if (!userService.isAdmin(userId) && !record.getUser().getId().equals(userId)) {
                return Result.error("权限不足");
            }
            
            return Result.success(record);
            
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    // 获取当前用户的充值记录（分页）
    @GetMapping("/my")
    public Result<Page<DepositRecord>> getMyDepositRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request) {
        
        try {
            Long userId = (Long) request.getAttribute("userId");
            
            Sort sort = direction.equalsIgnoreCase("asc") ? 
                    Sort.by(sortBy).ascending() : 
                    Sort.by(sortBy).descending();
                    
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<DepositRecord> records = depositRecordService.getDepositRecordsByUserId(userId, pageable);
            
            return Result.success(records);
            
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    // 获取指定用户的充值记录（分页，仅管理员）
    @GetMapping("/user/{userId}")
    public Result<Page<DepositRecord>> getUserDepositRecords(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request) {
        
        try {
            Long operatorId = (Long) request.getAttribute("userId");
            
            // 非管理员只能查看自己的充值记录
            if (!userService.isAdmin(operatorId) && !userId.equals(operatorId)) {
                return Result.error("权限不足");
            }
            
            Sort sort = direction.equalsIgnoreCase("asc") ? 
                    Sort.by(sortBy).ascending() : 
                    Sort.by(sortBy).descending();
                    
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<DepositRecord> records = depositRecordService.getDepositRecordsByUserId(userId, pageable);
            
            return Result.success(records);
            
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    // 获取所有充值记录（分页，仅管理员）
    @GetMapping
    public Result<Page<DepositRecord>> getAllDepositRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request) {
        
        try {
            Long userId = (Long) request.getAttribute("userId");
            
            // 仅管理员可访问
            if (!userService.isAdmin(userId)) {
                return Result.error("权限不足");
            }
            
            Sort sort = direction.equalsIgnoreCase("asc") ? 
                    Sort.by(sortBy).ascending() : 
                    Sort.by(sortBy).descending();
                    
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<DepositRecord> records = depositRecordService.getAllDepositRecords(pageable);
            
            return Result.success(records);
            
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
} 