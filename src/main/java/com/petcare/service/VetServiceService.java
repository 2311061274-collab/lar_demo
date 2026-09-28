package com.petcare.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VetServiceService {

    private final VetServiceRepository
            vetServiceRepository;


    @Transactional(readOnly = true)
    public List<VetService> getActiveServices() {

        return vetServiceRepository
                .findByActiveTrueOrderByIdAsc();
    }


    @Transactional(readOnly = true)
    public VetService getBySlug(
            String slug
    ) {

        return vetServiceRepository
                .findBySlugAndActiveTrue(slug)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Không tìm thấy dịch vụ: "
                                        + slug
                        )
                );
    }


    @Transactional(readOnly = true)
    public VetService getActiveById(
            Long id
    ) {

        VetService service =
                vetServiceRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Không tìm thấy dịch vụ"
                                )
                        );

        if (!Boolean.TRUE.equals(
                service.getActive()
        )) {

            throw new IllegalArgumentException(
                    "Dịch vụ hiện không hoạt động"
            );
        }

        return service;
    }
}
