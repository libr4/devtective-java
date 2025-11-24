package com.devtective.devtective.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devtective.devtective.dominio.notification.NotificationMedia;

public interface NotificationMediaRepository extends JpaRepository<NotificationMedia, Long> {
}
