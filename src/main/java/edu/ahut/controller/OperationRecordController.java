package edu.ahut.controller;

import edu.ahut.common.Result;
import edu.ahut.entity.OperationRecord;
import edu.ahut.service.OperationRecordService;
import edu.ahut.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/operation-records")
public class OperationRecordController {

    @Autowired
    private OperationRecordService operationRecordService;

    @Autowired
    private UserService userService;

    // 根据ID获取操作记录
    @GetMapping("/{id}")
    public Result<OperationRecord> getOperationRecordById(
            @PathVariable Long id,
            HttpServletRequest request) {

        try {
            Long userId = (Long) request.getAttribute("userId");
            Optional<OperationRecord> recordOpt = operationRecordService.getOperationRecordById(id);

            if (recordOpt.isEmpty()) {
                return Result.error("操作记录不存在");
            }

            OperationRecord record = recordOpt.get();

            // 非管理员只能查看自己的操作记录
            if (!userService.isAdmin(userId) && !record.getUser().getId().equals(userId)) {
                return Result.error("权限不足");
            }

            return Result.success(record);

        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    // 获取当前用户的操作记录（分页）
    @GetMapping("/my")
    public Result<Page<OperationRecord>> getMyOperationRecords(
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
            Page<OperationRecord> records = operationRecordService.getOperationRecordsByUserId(userId, pageable);

            return Result.success(records);

        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    // 获取指定用户的操作记录（分页，仅管理员）
    @GetMapping("/user/{userId}")
    public Result<Page<OperationRecord>> getUserOperationRecords(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request) {

        try {
            Long operatorId = (Long) request.getAttribute("userId");

            // 非管理员只能查看自己的操作记录
            if (!userService.isAdmin(operatorId) && !userId.equals(operatorId)) {
                return Result.error("权限不足");
            }

            Sort sort = direction.equalsIgnoreCase("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<OperationRecord> records = operationRecordService.getOperationRecordsByUserId(userId, pageable);

            return Result.success(records);

        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    // 获取指定计算机的操作记录（分页，仅管理员）
    @GetMapping("/computer/{computerId}")
    public Result<Page<OperationRecord>> getComputerOperationRecords(
            @PathVariable Long computerId,
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
            Page<OperationRecord> records = operationRecordService.getOperationRecordsByComputerId(computerId, pageable);

            return Result.success(records);

        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    // 获取指定用户在指定计算机上的操作记录（分页）
    @GetMapping("/user/{userId}/computer/{computerId}")
    public Result<Page<OperationRecord>> getUserComputerOperationRecords(
            @PathVariable Long userId,
            @PathVariable Long computerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request) {

        try {
            Long operatorId = (Long) request.getAttribute("userId");

            // 非管理员只能查看自己的操作记录
            if (!userService.isAdmin(operatorId) && !userId.equals(operatorId)) {
                return Result.error("权限不足");
            }

            Sort sort = direction.equalsIgnoreCase("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<OperationRecord> records = operationRecordService.getOperationRecordsByUserIdAndComputerId(userId, computerId, pageable);

            return Result.success(records);

        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    // 获取所有操作记录（分页，仅管理员）
    @GetMapping
    public Result<Page<OperationRecord>> getAllOperationRecords(
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
            Page<OperationRecord> records = operationRecordService.getAllOperationRecords(pageable);

            return Result.success(records);

        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}