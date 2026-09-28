package com.petcare.admin.contact;

import com.petcare.contact.ContactMessage;
import com.petcare.contact.ContactMessageRepository;
import com.petcare.contact.ContactMessageStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminContactService {

    private final ContactMessageRepository
            contactMessageRepository;


    /*
     * =========================================================
     * LIST
     * =========================================================
     */

    @Transactional(readOnly = true)
    public List<ContactMessage> findAll() {

        return contactMessageRepository
                .findAllByOrderByCreatedAtDesc();
    }


    /*
     * =========================================================
     * DETAIL
     * =========================================================
     */

    @Transactional
    public ContactMessage findById(Long id) {

        ContactMessage contact =
                contactMessageRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Không tìm thấy liên hệ có id = " + id
                                )
                        );

        /*
         * Khi Admin mở một message NEW,
         * tự động chuyển thành READ.
         */
        if (contact.getStatus()
                == ContactMessageStatus.NEW) {

            contact.setStatus(
                    ContactMessageStatus.READ
            );
        }

        return contact;
    }


    /*
     * =========================================================
     * MARK REPLIED
     * =========================================================
     */

    @Transactional
    public void markReplied(Long id) {

        ContactMessage contact =
                contactMessageRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Không tìm thấy liên hệ có id = " + id
                                )
                        );

        contact.setStatus(
                ContactMessageStatus.REPLIED
        );
    }


    /*
     * =========================================================
     * REPLY MESSAGE
     * =========================================================
     */

    @Transactional
    public void reply(Long id, String replyMessage) {

        ContactMessage contact =
                contactMessageRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Không tìm thấy liên hệ có id = " + id
                                )
                        );

        contact.setReplyMessage(replyMessage != null ? replyMessage.trim() : "");
        contact.setRepliedAt(java.time.LocalDateTime.now());
        contact.setStatus(ContactMessageStatus.REPLIED);
    }
}
