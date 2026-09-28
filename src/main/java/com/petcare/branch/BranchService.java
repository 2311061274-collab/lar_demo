package com.petcare.branch;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;


    /*
     * =========================================================
     * LẤY DANH SÁCH CHI NHÁNH ĐANG HOẠT ĐỘNG
     * =========================================================
     */

    @Transactional(readOnly = true)
    public List<Branch> getActiveBranches() {

        return branchRepository
                .findByActiveTrueOrderByIdAsc();
    }


    /*
     * =========================================================
     * LẤY CHI NHÁNH ACTIVE THEO ID
     * =========================================================
     */

    @Transactional(readOnly = true)
    public Branch getActiveById(Long id) {

        Branch branch =
                branchRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Không tìm thấy chi nhánh"
                                )
                        );


        if (!Boolean.TRUE.equals(
                branch.getActive()
        )) {

            throw new IllegalArgumentException(
                    "Chi nhánh hiện không hoạt động"
            );
        }


        return branch;
    }
}
