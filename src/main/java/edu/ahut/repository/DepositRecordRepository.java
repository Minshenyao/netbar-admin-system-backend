package edu.ahut.repository;

import edu.ahut.entity.DepositRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRecordRepository extends JpaRepository<DepositRecord, Long> {
    List<DepositRecord> findByUserId(Long userId);
    Page<DepositRecord> findByUserId(Long userId, Pageable pageable);
}