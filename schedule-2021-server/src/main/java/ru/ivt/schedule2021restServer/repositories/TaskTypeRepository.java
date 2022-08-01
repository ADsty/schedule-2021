package ru.ivt.schedule2021restServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.ivt.schedule2021restServer.models.TaskType;

public interface TaskTypeRepository extends JpaRepository<TaskType, Long>, JpaSpecificationExecutor<TaskType> {

}
