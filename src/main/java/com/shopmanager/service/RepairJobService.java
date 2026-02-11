package com.shopmanager.service;

import com.shopmanager.dto.repair.RepairJobRequest;
import com.shopmanager.dto.repair.RepairJobResponse;
import org.springframework.data.domain.Page;

public interface RepairJobService {

    RepairJobResponse createRepairJob(RepairJobRequest request);

    RepairJobResponse updateRepairJob(Long id, RepairJobRequest request);

    RepairJobResponse getRepairJob(Long id);

    Page<RepairJobResponse> searchRepairs(String query, int page, int size);

    void deleteRepairJob(Long id);
}