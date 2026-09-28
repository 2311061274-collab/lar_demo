package com.petcare.contact;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Getter
@Setter
@NoArgsConstructor
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
            name = "full_name",
            nullable = false
    )
    private String fullName;


    @Column(
            nullable = false,
            length = 50
    )
    private String phone;


    private String email;


    private String subject;


    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String message;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactMessageStatus status =
            ContactMessageStatus.NEW;


    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;


    @Column(
            name = "reply_message",
            columnDefinition = "TEXT"
    )
    private String replyMessage;


    @Column(
            name = "replied_at"
    )
    private LocalDateTime repliedAt;


    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (status == null) {
            status = ContactMessageStatus.NEW;
        }
    }
}
