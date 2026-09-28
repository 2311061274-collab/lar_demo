package com.petcare.admin.service;

import com.petcare.appointment.AppointmentRepository;
import com.petcare.service.VetService;
import com.petcare.service.VetServiceRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceService {

    private final VetServiceRepository vetServiceRepository;

    private final AppointmentRepository appointmentRepository;


    @Transactional(readOnly = true)
    public List<VetService> findAll() {

        return vetServiceRepository
                .findAllByOrderByIdAsc();
    }


    @Transactional(readOnly = true)
    public VetService findById(Long id) {

        return vetServiceRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Không tìm thấy dịch vụ"
                        )
                );
    }


    @Transactional
    public VetService create(
            AdminServiceRequest request
    ) {

        String slug =
                normalizeSlug(
                        request.getSlug()
                );

        if (vetServiceRepository.existsBySlug(slug)) {

            throw new IllegalArgumentException(
                    "Slug đã tồn tại."
            );
        }

        validatePrice(request);

        VetService service =
                new VetService();

        applyRequest(
                service,
                request,
                slug
        );

        return vetServiceRepository.save(service);
    }


    @Transactional
    public VetService update(
            Long id,
            AdminServiceRequest request
    ) {

        VetService service =
                findById(id);

        String slug =
                normalizeSlug(
                        request.getSlug()
                );

        if (vetServiceRepository
                .existsBySlugAndIdNot(
                        slug,
                        id
                )) {

            throw new IllegalArgumentException(
                    "Slug đã được sử dụng bởi dịch vụ khác."
            );
        }

        validatePrice(request);

        applyRequest(
                service,
                request,
                slug
        );

        return vetServiceRepository.save(service);
    }


    @Transactional
    public void toggleActive(Long id) {

        VetService service =
                findById(id);

        service.setActive(
                !Boolean.TRUE.equals(
                        service.getActive()
                )
        );

        vetServiceRepository.save(service);
    }


    @Transactional
    public void delete(Long id) {

        VetService service = findById(id);

        if (appointmentRepository.existsByService_Id(id)) {
            throw new IllegalArgumentException("Không thể xóa dịch vụ đã có lịch khám. Vui lòng sử dụng tính năng Ẩn dịch vụ.");
        }

        vetServiceRepository.delete(service);
    }


    public AdminServiceRequest toRequest(
            VetService service
    ) {

        AdminServiceRequest request =
                new AdminServiceRequest();

        request.setName(service.getName());
        request.setSlug(service.getSlug());
        request.setSummary(service.getSummary());
        request.setDescription(service.getDescription());
        request.setPriceFrom(service.getPriceFrom());
        request.setPriceTo(service.getPriceTo());
        request.setDurationMinutes(
                service.getDurationMinutes()
        );
        request.setImageUrl(service.getImageUrl());
        request.setActive(service.getActive());

        return request;
    }


    private void applyRequest(
            VetService service,
            AdminServiceRequest request,
            String slug
    ) {

        service.setName(
                request.getName().trim()
        );

        service.setSlug(slug);

        service.setSummary(
                normalizeNullable(
                        request.getSummary()
                )
        );

        service.setDescription(
                normalizeNullable(
                        request.getDescription()
                )
        );

        service.setPriceFrom(
                request.getPriceFrom()
        );

        service.setPriceTo(
                request.getPriceTo()
        );

        service.setDurationMinutes(
                request.getDurationMinutes()
        );

        service.setImageUrl(
                normalizeNullable(
                        request.getImageUrl()
                )
        );

        service.setActive(
                Boolean.TRUE.equals(
                        request.getActive()
                )
        );
    }


    private void validatePrice(
            AdminServiceRequest request
    ) {

        if (
                request.getPriceFrom() != null
                        &&
                        request.getPriceTo() != null
                        &&
                        request.getPriceTo()
                                .compareTo(
                                        request.getPriceFrom()
                                ) < 0
        ) {

            throw new IllegalArgumentException(
                    "Giá đến không được nhỏ hơn giá từ."
            );
        }
    }


    private String normalizeSlug(
            String slug
    ) {

        return slug
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", "-");
    }


    private String normalizeNullable(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String result =
                value.trim();

        return result.isEmpty()
                ? null
                : result;
    }
}
