package edu.ahut.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "deposit_records")
public class DepositRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Double amount;
    
    // 支付方式: 1-现金, 2-支付宝, 3-微信, 4-银行卡
    private Integer paymentMethod;
    
    // 操作人ID（谁进行了充值操作，一般是管理员）
    @Column(name = "operator_id")
    private Long operatorId;
    
    // 订单号
    @Column(unique = true)
    private String orderNumber;
    
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