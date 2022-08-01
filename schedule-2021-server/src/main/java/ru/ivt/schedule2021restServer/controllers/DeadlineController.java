package ru.ivt.schedule2021restServer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ivt.schedule2021restServer.forms.DeadlineCreationForm;
import ru.ivt.schedule2021restServer.models.Deadline;
import ru.ivt.schedule2021restServer.services.IDeadlineService;
import ru.ivt.schedule2021restServer.transfer.DateWrapper;

import java.time.LocalDate;
import java.util.List;

import static ru.ivt.schedule2021restServer.transfer.DeadlineDto.from;

@SuppressWarnings("unchecked")
@RestController
public class DeadlineController {
    @Autowired
    private IDeadlineService deadlineService;

    @GetMapping("/students/{student-id}/deadlines")
    public DateWrapper getDeadlinesOfCurrentWeekByStudentId(@PathVariable("student-id") Long studentId) {
        final DateWrapper deadlinesOfCurrentWeekByStudent = deadlineService.getDeadlinesOfCurrentWeekByStudent(studentId);
        return new DateWrapper(
            deadlinesOfCurrentWeekByStudent.getDates(),
            from((List<Deadline>) deadlinesOfCurrentWeekByStudent.getDataList())
        );
    }

    @GetMapping("/students/{student-id}/deadlines/{date}")
    public DateWrapper getDeadlinesOfWeekByStudentId(@PathVariable("student-id") Long studentId,
                                                     @PathVariable("date") String date) {
        final DateWrapper deadlinesOfWeekByStudent = deadlineService.getDeadlinesOfWeekByStudent(studentId, LocalDate.parse(date));
        return new DateWrapper(
            deadlinesOfWeekByStudent.getDates(),
            from((List<Deadline>) deadlinesOfWeekByStudent.getDataList())
        );
    }

    @GetMapping("/groups/{group-id}/deadlines")
    public DateWrapper getDeadlinesOfCurrentWeekByGroupId(@PathVariable("group-id") Long groupId) {
        final DateWrapper deadlinesOfCurrentWeekByGroup = deadlineService.getDeadlinesOfCurrentWeekByGroup(groupId);
        return new DateWrapper(
            deadlinesOfCurrentWeekByGroup.getDates(),
            from((List<Deadline>) deadlinesOfCurrentWeekByGroup.getDataList())
        );
    }

    @GetMapping("/groups/{group-id}/deadlines/{date}")
    public DateWrapper getDeadlinesOfWeekByGroupId(
        @PathVariable("group-id") Long groupId,
        @PathVariable("date") String date) {

        final DateWrapper deadlinesOfWeekByGroup = deadlineService.getDeadlinesOfWeekByGroup(groupId, LocalDate.parse(date));
        return new DateWrapper(
            deadlinesOfWeekByGroup.getDates(),
            from((List<Deadline>) deadlinesOfWeekByGroup.getDataList())
        );
    }

    @PostMapping("/deadlines/create")
    public void createDeadline(DeadlineCreationForm deadlineCreationForm) {
        deadlineService.createNewDeadline(deadlineCreationForm);
    }

    @PostMapping("/deadlines/delete")
    public void deleteDeadline(Long deadlineId) {
        deadlineService.deleteDeadlineById(deadlineId);
    }
//    TODO VITALIY Добавить инсерт дедлайна для конкретного студента


//    TODO VITALIY Добавить удаление дедлайна для конкретного студента
}
