package com.shopmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "repair_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private LocalDateTime paidAt;

    private String note; // optional (cash / upi / partial etc)


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_job_id")
    @JsonIgnore   // ⭐ ADD THIS LINE
    private RepairJob repairJob;

}