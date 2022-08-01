package ru.ivt.schedule2021restServer.services;

import ru.ivt.schedule2021restServer.forms.DeadlineCreationForm;
import ru.ivt.schedule2021restServer.transfer.DateWrapper;

import java.time.LocalDate;

public interface IDeadlineService {

    DateWrapper getDeadlinesOfCurrentWeekByStudent(Long studentId);

    DateWrapper getDeadlinesOfCurrentWeekByGroup(Long groupId);

    DateWrapper getDeadlinesOfWeekByStudent(Long studentId, LocalDate weekDay);

    DateWrapper getDeadlinesOfWeekByGroup(Long groupId, LocalDate weekDay);

    void createNewDeadline(DeadlineCreationForm deadlineCreationForm);

    void deleteDeadlineById(Long deadlineId);
}
