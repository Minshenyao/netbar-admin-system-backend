package edu.ahut.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "operation_records")
public class OperationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "computer_id", nullable = false)
    private Computer computer;
    
    // 操作类型: 1-开机, 2-关机, 3-开始使用, 4-结束使用, 5-设为维修, 6-取消维修
    @Column(nullable = false)
    private Integer operationType;
    
    // 开始时间
    @Column(name = "start_time")
    private Long startTime;
    
    // 结束时间
    @Column(name = "end_time")
    private Long endTime;
    
    // 使用时长（分钟）
    private Integer duration;
    
    // 消费金额
    private Double cost;
    
    // 备注
    private String remark;
    
    @Column(name = "created_at")
    private Long createdAt;
    
    @Column(name = "updated_at")
    private Long updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
} 