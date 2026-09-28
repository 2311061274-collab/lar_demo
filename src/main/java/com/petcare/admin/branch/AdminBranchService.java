package com.petcare.admin.branch;

import com.petcare.branch.Branch;
import com.petcare.branch.BranchRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminBranchService {

    private final BranchRepository branchRepository;


    @Transactional(readOnly = true)
    public List<Branch> findAll() {

        return branchRepository
                .findAllByOrderByIdAsc();
    }


    @Transactional(readOnly = true)
    public Branch findById(Long id) {

        return branchRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Không tìm thấy chi nhánh có id = " + id
                        )
                );
    }


    @Transactional
    public Branch create(
            AdminBranchRequest request
    ) {

        Branch branch = new Branch();

        applyRequest(
                branch,
                request
        );

        return branchRepository
                .save(branch);
    }


    @Transactional
    public Branch update(
            Long id,
            AdminBranchRequest request
    ) {

        Branch branch =
                findById(id);

        applyRequest(
                branch,
                request
        );

        return branchRepository
                .save(branch);
    }


    @Transactional
    public void toggleActive(Long id) {

        Branch branch = branchRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Không tìm thấy chi nhánh có id = " + id
                        )
                );

        boolean currentActive =
                Boolean.TRUE.equals(
                        branch.getActive()
                );

        branch.setActive(
                !currentActive
        );

        branchRepository
                .saveAndFlush(branch);
    }


    public AdminBranchRequest toRequest(
            Branch branch
    ) {

        AdminBranchRequest request =
                new AdminBranchRequest();

        request.setName(
                branch.getName()
        );

        request.setAddress(
                branch.getAddress()
        );

        request.setPhone(
                branch.getPhone()
        );

        request.setActive(
                branch.getActive()
        );

        return request;
    }


    private void applyRequest(
            Branch branch,
            AdminBranchRequest request
    ) {

        branch.setName(
                request.getName().trim()
        );

        branch.setAddress(
                request.getAddress().trim()
        );

        branch.setPhone(
                normalizeNullable(
                        request.getPhone()
                )
        );

        branch.setActive(
                Boolean.TRUE.equals(
                        request.getActive()
                )
        );
    }


    private String normalizeNullable(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}
