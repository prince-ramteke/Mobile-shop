package com.shopmanager.repository;

import com.shopmanager.entity.RepairPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepairPaymentRepository extends JpaRepository<RepairPayment, Long> {
    List<RepairPayment> findByRepairJobIdOrderByPaidAtDesc(Long repairJobId);
    List<RepairPayment> findByRepairJobIdOrderByPaidAtAsc(Long repairJobId);

}