package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.worker.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
        Optional<Worker> findByUserId(AppUser user);
        Worker findByUserId_PublicId(UUID publicId);

        @Query("""
               SELECT w from Worker w
               JOIN w.userId u
               WHERE u.publicId IN :userPublicIds
               """)
        List<Worker> findWorkersByUserPublicIdIn(Collection<UUID> userPublicIds);

        //Worker findById(Long id);
}
