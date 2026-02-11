package com.shopmanager.service.impl;

import com.shopmanager.audit.service.AuditService;

import com.shopmanager.dto.repair.RepairJobRequest;
import com.shopmanager.dto.repair.RepairJobResponse;
import com.shopmanager.entity.*;
import com.shopmanager.entity.enums.*;
import com.shopmanager.exception.ResourceNotFoundException;
import com.shopmanager.mapper.RepairJobMapper;
import com.shopmanager.repository.*;
import com.shopmanager.service.*;
import com.shopmanager.message.dto.MessagePayload;
import com.shopmanager.message.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RepairJobServiceImpl implements RepairJobService {

    private final RepairJobRepository repairJobRepository;
    private final CustomerRepository customerRepository;
    private final MessageLogRepository messageLogRepository;
    private final RepairJobMapper repairJobMapper;
    private final PdfService pdfService;
    private final NotificationService notificationService;

    private final AuditService auditService;

    // ⭐ ADDED
    private final DueService dueService;

    @Override
    public RepairJobResponse createRepairJob(RepairJobRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        RepairJob job = repairJobMapper.toEntity(request);
        job.setCustomer(customer);

        if (job.getFinalCost() != null && job.getAdvancePaid() != null) {
            job.setPendingAmount(job.getFinalCost().subtract(job.getAdvancePaid()));
        }

        RepairJob savedJob = repairJobRepository.save(job);
        auditService.log(
                "CREATE_REPAIR",
                "REPAIR",
                savedJob.getId(),
                "Job " + savedJob.getJobNumber()
        );

        // ⭐ ADDED — CREATE DUE ENTRY
        dueService.createDue(
                customer.getId(),
                DueReferenceType.REPAIR,
                savedJob.getId(),
                savedJob.getFinalCost(),
                savedJob.getAdvancePaid()
        );

        // =====================================================
        // 🔥 AUTO WHATSAPP REPAIR RECEIPT SEND
        // =====================================================
        try {
            byte[] pdf = pdfService.generateRepairReceiptPdf(savedJob.getId());

            MessageLog log = MessageLog.builder()
                    .customer(customer)
                    .type(MessageType.BILL)
                    .channel(MessageChannel.WHATSAPP)
                    .status(MessageStatus.PENDING)
                    .messageContent("Repair receipt for Job " + savedJob.getJobNumber())
                    .build();

            messageLogRepository.save(log);

            MessagePayload payload = MessagePayload.builder()
                    .recipient(customer.getWhatsappNumber())
                    .message(
                            "Repair Job Created\n" +
                                    "Job No: " + savedJob.getJobNumber() + "\n" +
                                    "Pending: ₹" + savedJob.getPendingAmount()
                    )
                    .pdf(pdf)
                    .fileName("Repair-" + savedJob.getJobNumber() + ".pdf")
                    .build();

            notificationService.send(log, payload);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return repairJobMapper.toResponse(savedJob);
    }

    // ❌ NO OTHER CHANGES BELOW

    @Override
    public RepairJobResponse updateRepairJob(Long id, RepairJobRequest request) {
        RepairJob existing = repairJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair job not found"));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        existing.setCustomer(customer);
        existing.setDeviceBrand(request.getDeviceBrand());
        existing.setDeviceModel(request.getDeviceModel());
        existing.setImei(request.getImei());
        existing.setIssueDescription(request.getIssueDescription());
        existing.setEstimatedCost(request.getEstimatedCost());
        existing.setFinalCost(request.getFinalCost());
        existing.setAdvancePaid(request.getAdvancePaid());
        existing.setStatus(request.getStatus());

        if (existing.getFinalCost() != null && existing.getAdvancePaid() != null) {
            existing.setPendingAmount(existing.getFinalCost().subtract(existing.getAdvancePaid()));
        }

        return repairJobMapper.toResponse(repairJobRepository.save(existing));
    }

    @Override
    public RepairJobResponse getRepairJob(Long id) {
        RepairJob job = repairJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair job not found"));
        return repairJobMapper.toResponse(job);
    }

    @Override
    public Page<RepairJobResponse> searchRepairs(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repairJobRepository.search(query, pageable)
                .map(repairJobMapper::toResponse);
    }

    @Override
    public void deleteRepairJob(Long id) {
        RepairJob job = repairJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair job not found"));
        repairJobRepository.delete(job);
    }
}