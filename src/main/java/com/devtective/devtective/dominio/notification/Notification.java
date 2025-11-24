package com.devtective.devtective.dominio.notification;

import com.devtective.devtective.dominio.user.AppUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification")
@RequiredArgsConstructor
@Getter
@Setter
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private AppUser recipient;

    @Column(name = "messagem", nullable = false)
    private String messagem;

    @ManyToOne
    @JoinColumn(name = "media_id", nullable = false)
    private NotificationMedia media;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private NotificationStatus status;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private NotificationType type;

    public static Notification create(
            AppUser recipient, 
            String messagem, 
            NotificationMedia media,
            NotificationStatus status, 
            NotificationType type) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessagem(messagem);
        notification.setMedia(media);
        notification.setStatus(status);
        notification.setType(type);
        return notification;
    }

}
