package ru.ivt.schedule2021restServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.ivt.schedule2021restServer.models.LessonType;

public interface LessonTypeRepository extends JpaRepository<LessonType, Long>, JpaSpecificationExecutor<LessonType> {

}
