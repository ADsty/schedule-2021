package ru.ivt.schedule2021restServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.ivt.schedule2021restServer.models.GROUP;

import java.util.List;
import java.util.Optional;

public interface GROUPRepository extends JpaRepository<GROUP, Long>, JpaSpecificationExecutor<GROUP> {
    //    GROUP findByName(String name);
    Optional<GROUP> findByName(String name);

    @Query("select grp from GROUP as grp inner join grp.faculty as fac where fac.id = :facultyId")
    List<GROUP> findGroupsByFacultyId(Long facultyId);
}
