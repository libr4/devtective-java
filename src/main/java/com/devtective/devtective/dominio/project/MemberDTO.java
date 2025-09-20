package com.devtective.devtective.dominio.project;

public record MemberDTO(
        String userPublicId,
        String displayName,
        String username,
        String email,
        String avatarUrl,
        // e.g., "Developer", "QA", "Product Owner"
        String position,
        InvitationStatus invitationStatus
) {
    public enum InvitationStatus {
        INVITED, PENDING, ACCEPTED, DECLINED, REMOVED
    }
}