package edu.ahut.controller;

import edu.ahut.common.Result;
import edu.ahut.entity.Computer;
import edu.ahut.service.ComputerService;
import edu.ahut.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/computers")
public class ComputerController {

    @Autowired
    private ComputerService computerService;
    
    @Autowired
    private UserService userService;
    
    // 基础管理接口
    
    @PostMapping
    public Result<Computer> createComputer(HttpServletRequest request, @RequestBody Computer computer) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            Computer createdComputer = computerService.createComputer(computer);
            return Result.success(createdComputer);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<Computer> getComputerById(@PathVariable Long id) {
        try {
            Computer computer = computerService.getComputerById(id);
            return Result.success(computer);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Computer> updateComputer(HttpServletRequest request, @PathVariable Long id, @RequestBody Computer computer) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            computer.setId(id);
            Computer updatedComputer = computerService.updateComputer(computer);
            return Result.success(updatedComputer);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteComputer(HttpServletRequest request, @PathVariable Long id) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            computerService.deleteComputer(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping
    public Result<Page<Computer>> getAllComputers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            Page<Computer> computers = computerService.getAllComputers(pageable);
            return Result.success(computers);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 区域查询接口
    
    @GetMapping("/area/{area}")
    public Result<List<Computer>> getComputersByArea(@PathVariable String area) {
        try {
            List<Computer> computers = computerService.getComputersByArea(area);
            return Result.success(computers);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/area/{area}/page")
    public Result<Page<Computer>> getComputersByAreaPaged(
            @PathVariable String area,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            Page<Computer> computers = computerService.getComputersByArea(area, pageable);
            return Result.success(computers);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/area/count")
    public Result<Map<String, Long>> countComputersByArea() {
        try {
            Map<String, Long> countMap = computerService.countComputersByArea();
            return Result.success(countMap);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 状态查询接口
    
    @GetMapping("/status/{status}")
    public Result<List<Computer>> getComputersByStatus(@PathVariable Integer status) {
        try {
            List<Computer> computers = computerService.getComputersByStatus(status);
            return Result.success(computers);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/idle")
    public Result<List<Computer>> getIdleComputers() {
        try {
            List<Computer> computers = computerService.getIdleComputers();
            return Result.success(computers);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/in-use")
    public Result<List<Computer>> getInUseComputers() {
        try {
            List<Computer> computers = computerService.getInUseComputers();
            return Result.success(computers);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/maintenance")
    public Result<List<Computer>> getUnderMaintenanceComputers() {
        try {
            List<Computer> computers = computerService.getUnderMaintenanceComputers();
            return Result.success(computers);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/power-off")
    public Result<List<Computer>> getPowerOffComputers() {
        try {
            List<Computer> computers = computerService.getPowerOffComputers();
            return Result.success(computers);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 操作接口
    
    @PostMapping("/{id}/power-on")
    public Result<Computer> powerOn(HttpServletRequest request, @PathVariable Long id) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            Computer computer = computerService.powerOn(id, currentUserId);
            return Result.success(computer);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/power-off")
    public Result<Computer> powerOff(HttpServletRequest request, @PathVariable Long id) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            Computer computer = computerService.powerOff(id, currentUserId);
            return Result.success(computer);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/start-using")
    public Result<Computer> startUsing(HttpServletRequest request, @PathVariable Long id) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Computer computer = computerService.startUsing(id, userId);
            return Result.success(computer);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/stop-using")
    public Result<Computer> stopUsing(HttpServletRequest request, @PathVariable Long id) {
        try {
            // 获取当前用户ID
            Long currentUserId = (Long) request.getAttribute("userId");
            
            // 获取计算机信息
            Computer computer = computerService.getComputerById(id);
            
            // 检查是否是当前用户在使用或者是管理员
            if (!currentUserId.equals(computer.getUserId()) && !userService.isAdmin(currentUserId)) {
                return Result.error("您没有权限结束该计算机的使用");
            }
            
            Computer stoppedComputer = computerService.stopUsing(id, currentUserId);
            return Result.success(stoppedComputer);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/maintenance")
    public Result<Computer> setMaintenance(HttpServletRequest request, @PathVariable Long id) {
        try {
            // 检查当前用户是否为管理员
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId)) {
                return Result.error("权限不足");
            }
            
            Computer computer = computerService.setMaintenance(id, currentUserId);
            return Result.success(computer);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 特殊查询接口
    
    @GetMapping("/user/current")
    public Result<Computer> getCurrentUserComputer(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Computer computer = computerService.getComputerByUserId(userId);
            return Result.success(computer);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/user/{userId}")
    public Result<Computer> getUserComputer(HttpServletRequest request, @PathVariable Long userId) {
        try {
            // 检查当前用户是否为管理员或者是查询自己
            Long currentUserId = (Long) request.getAttribute("userId");
            if (!userService.isAdmin(currentUserId) && !currentUserId.equals(userId)) {
                return Result.error("权限不足");
            }
            
            Computer computer = computerService.getComputerByUserId(userId);
            return Result.success(computer);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
} 