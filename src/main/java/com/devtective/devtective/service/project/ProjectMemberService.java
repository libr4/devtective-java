package com.devtective.devtective.service.project;

import com.devtective.devtective.dominio.project.MemberDTO;
import com.devtective.devtective.dominio.project.ProjectMemberRow;
import com.devtective.devtective.repository.ProjectMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository repo;

    public ProjectMemberService(ProjectMemberRepository repo) {
        this.repo = repo;
    }

    public List<MemberDTO> listMembers(UUID projectPublicId) {
        var rows = repo.findMemberRowsByProjectPublicId(projectPublicId);
        return rows.stream()
                .map(r -> new MemberDTO(
                        r.getUserPublicId().toString(),
                        resolveDisplayName(r),
                        r.getUsername(),
                        r.getEmail(),
                        r.getAvatarUrl(),
                        r.getPositionName() != null ? r.getPositionName() : "Member",
                        mockStatus(r.getUserPublicId().toString()) // <- only this field is mocked for now
                ))
                .toList();
    }

    private static String resolveDisplayName(ProjectMemberRow r) {
        String first = safe(r.getFirstName());
        String last  = safe(r.getLastName());
        String full  = (first + " " + last).trim();
        return full.isEmpty() ? r.getUsername() : full;
    }

    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private static MemberDTO.InvitationStatus mockStatus(String userPublicId) {
        int n = Math.abs(userPublicId.hashCode()) % 5;
        return switch (n) {
            case 0 -> MemberDTO.InvitationStatus.ACCEPTED;
            case 1 -> MemberDTO.InvitationStatus.PENDING;
            case 2 -> MemberDTO.InvitationStatus.INVITED;
            case 3 -> MemberDTO.InvitationStatus.DECLINED;
            default -> MemberDTO.InvitationStatus.REMOVED;
        };
    }
}
