package com.devtective.devtective.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devtective.devtective.dominio.notification.NotificationStatus;

public interface NotificationStatusRepository extends JpaRepository<NotificationStatus, Long> {
    NotificationStatus findByName(String name);
}
