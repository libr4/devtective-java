
package com.devtective.devtective.service.notification;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.devtective.devtective.dominio.notification.Notification;
import com.devtective.devtective.dominio.notification.NotificationMedia;
import com.devtective.devtective.dominio.notification.NotificationMediaEnum;
import com.devtective.devtective.dominio.notification.NotificationStatus;
import com.devtective.devtective.dominio.notification.NotificationStatusEnum;
import com.devtective.devtective.dominio.notification.NotificationType;
import com.devtective.devtective.dominio.notification.NotificationTypeEnum;
import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.repository.NotificationRepository;
import com.devtective.devtective.repository.ProjectRepository;
import com.devtective.devtective.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationDefaultCache notificationDefaultCache;
    private final NotificationRepository notificationRepository;

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public void sendProjectInvite(String username, UUID projectPublicId) {

        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));    
        NotificationMedia media = notificationDefaultCache.media(NotificationMediaEnum.IN_APP.name());
        NotificationStatus status = notificationDefaultCache.status(NotificationStatusEnum.PENDING.name());
        NotificationType type = notificationDefaultCache.type(NotificationTypeEnum.PROJECT_INVITE.name());
        Project project = projectRepository.findByPublicId(projectPublicId);

        Notification notification = Notification.create(user, 
            "You have been invited to join the project: " + project.getName(),
            media,
            status,
            type);
        
        notificationRepository.save(notification);
    }
    
}
