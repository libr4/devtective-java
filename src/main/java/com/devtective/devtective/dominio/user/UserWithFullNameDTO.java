package com.devtective.devtective.dominio.user;

import java.util.UUID;

public interface UserWithFullNameDTO {
    UUID getPublicId();
    String getUsername();
    String getDisplayName();
}
