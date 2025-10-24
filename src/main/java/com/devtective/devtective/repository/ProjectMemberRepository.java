package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.project.ProjectMember;
import com.devtective.devtective.dominio.project.ProjectMemberRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    boolean existsByProjectPublicIdAndWorkerId(UUID projectPublicId, Long workerId);
    List<ProjectMember> findAllByWorkerId(Long workerId);

    @Query("""
      select
        u.publicId  as userPublicId,
        w.firstName as firstName,
        w.lastName  as lastName,
        u.username  as username,
        u.email     as email,
        pos.name    as positionName
      from ProjectMember pm
        join pm.worker w
        join w.userId u
        left join w.positionId pos
      where pm.project.publicId = :projectPublicId
      """)
    List<ProjectMemberRow> findMemberRowsByProjectPublicId(@Param("projectPublicId") UUID projectPublicId);
}