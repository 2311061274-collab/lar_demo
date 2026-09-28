package com.petcare.contact;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMessageRepository
            contactMessageRepository;


    @Transactional
    public ContactMessage create(
            ContactRequest request
    ) {

        ContactMessage message =
                new ContactMessage();

        message.setFullName(
                request.getFullName().trim()
        );

        message.setPhone(
                request.getPhone().trim()
        );

        message.setEmail(
                normalizeNullable(
                        request.getEmail()
                )
        );

        message.setSubject(
                normalizeNullable(
                        request.getSubject()
                )
        );

        message.setMessage(
                request.getMessage().trim()
        );

        message.setStatus(
                ContactMessageStatus.NEW
        );

        return contactMessageRepository
                .save(message);
    }


    private String normalizeNullable(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}
