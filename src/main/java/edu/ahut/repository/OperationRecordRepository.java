package edu.ahut.repository;

import edu.ahut.entity.OperationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRecordRepository extends JpaRepository<OperationRecord, Long> {
    List<OperationRecord> findByUserId(Long userId);
    Page<OperationRecord> findByUserId(Long userId, Pageable pageable);
    
    List<OperationRecord> findByComputerId(Long computerId);
    Page<OperationRecord> findByComputerId(Long computerId, Pageable pageable);
    
    List<OperationRecord> findByOperationType(Integer operationType);
    
    List<OperationRecord> findByUserIdAndComputerId(Long userId, Long computerId);
    Page<OperationRecord> findByUserIdAndComputerId(Long userId, Long computerId, Pageable pageable);
} 