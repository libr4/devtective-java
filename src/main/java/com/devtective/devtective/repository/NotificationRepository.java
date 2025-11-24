package com.devtective.devtective.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devtective.devtective.dominio.notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
