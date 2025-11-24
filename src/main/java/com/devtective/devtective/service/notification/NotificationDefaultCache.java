package com.devtective.devtective.service.notification;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.devtective.devtective.dominio.notification.NotificationMedia;
import com.devtective.devtective.dominio.notification.NotificationStatus;
import com.devtective.devtective.dominio.notification.NotificationType;
import com.devtective.devtective.repository.NotificationMediaRepository;
import com.devtective.devtective.repository.NotificationStatusRepository;
import com.devtective.devtective.repository.NotificationTypeRepository;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Getter
public class NotificationDefaultCache {
    private final NotificationStatusRepository statusRepository;
    private final NotificationMediaRepository mediaRepository;
    private final NotificationTypeRepository typeRepository;

    private Map<String, NotificationType> types;
    private Map<String, NotificationStatus> statuses;
    private Map<String, NotificationMedia> medias;

    @PostConstruct
    public void load() {
        types = typeRepository.findAll().stream()
                .collect(Collectors.toMap(NotificationType::getName, t -> t));

        statuses = statusRepository.findAll().stream()
                .collect(Collectors.toMap(NotificationStatus::getName, s -> s));

        medias = mediaRepository.findAll().stream()
                .collect(Collectors.toMap(NotificationMedia::getName, m -> m));
    }

    public NotificationType type(String name) {
        return types.get(name);
    }

    public NotificationStatus status(String name) {
        return statuses.get(name);
    }

    public NotificationMedia media(String name) {
        return medias.get(name);
    }
}
