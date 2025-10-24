package com.devtective.devtective.dominio.project;

import java.util.UUID;

public interface ProjectMemberRow {
    UUID   getUserPublicId();
    String getFirstName();
    String getLastName();
    String getUsername();
    String getEmail();
    String getAvatarUrl();
    String getPositionName();
}

