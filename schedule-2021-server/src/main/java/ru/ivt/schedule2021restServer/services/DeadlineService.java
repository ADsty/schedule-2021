package ru.ivt.schedule2021restServer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ivt.schedule2021restServer.error.NotFoundApiException;
import ru.ivt.schedule2021restServer.forms.DeadlineCreationForm;
import ru.ivt.schedule2021restServer.models.Deadline;
import ru.ivt.schedule2021restServer.models.GROUP;
import ru.ivt.schedule2021restServer.models.Lesson;
import ru.ivt.schedule2021restServer.models.Student;
import ru.ivt.schedule2021restServer.models.Subject;
import ru.ivt.schedule2021restServer.repositories.DeadlineRepository;
import ru.ivt.schedule2021restServer.repositories.LessonRepository;
import ru.ivt.schedule2021restServer.repositories.StudentRepository;
import ru.ivt.schedule2021restServer.repositories.SubjectRepository;
import ru.ivt.schedule2021restServer.transfer.DateOptionsDto;
import ru.ivt.schedule2021restServer.transfer.DateWrapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class DeadlineService implements IDeadlineService {


    @Autowired
    private DeadlineRepository deadlineRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public DateWrapper getDeadlinesOfCurrentWeekByStudent(Long studentId) {
        return getDeadlinesOfWeekByStudent(studentId, LocalDate.now());
    }

    public void createNewDeadline(DeadlineCreationForm deadlineCreationForm) {
        final String title = deadlineCreationForm.getTitle();
        final String description = deadlineCreationForm.getDescription();
        final LocalTime notificationTime = LocalTime.parse(deadlineCreationForm.getNotificationTime());
        final LocalDate deadlineDate = LocalDate.parse(deadlineCreationForm.getDeadlineDate());
        final String type = "Deadline";
        Optional<Student> studentCandidate = studentRepository.findStudentById(deadlineCreationForm.getStudentId());

        if (title == null || description == null || notificationTime == null || deadlineDate == null || studentCandidate.isEmpty()) {
            throw new NotFoundApiException("Неправильные ключи для создания нового дедлайна");
        }
        final Lesson lesson = lessonRepository.findLessonById(1L);
        final Subject subject = subjectRepository.findSubjectByName(deadlineCreationForm.getSubjectName());
        final Student student = studentCandidate.get();
        final GROUP group = student.getGroup();

        Deadline deadline = Deadline
            .builder()
            .title(title)
            .description(description)
            .notificationTime(notificationTime)
            .deadlineDate(deadlineDate)
            .type(type)
            .subject(subject)
            .lesson(lesson)
            .mediaUrl("404 Error")
            .student(student)
            .group(group)
            .build();

        deadlineRepository.save(deadline);
    }

    @Override
    public DateWrapper getDeadlinesOfCurrentWeekByGroup(Long groupId) {
        return getDeadlinesOfWeekByGroup(groupId, LocalDate.now());
    }

    @Override
    public DateWrapper getDeadlinesOfWeekByStudent(Long studentId, LocalDate weekDay) {
        LocalDate monday = weekDay;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }
        LocalDate sunday = weekDay;
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }
        final DateOptionsDto dateOptionsDto = DateOptionsDto
            .builder()
            .currentStartWeek(monday)
            .currentEndWeek(sunday)
            .nextWeek(monday.plusWeeks(1))
            .previousWeek(monday.minusWeeks(1))
            .build();

        return new DateWrapper(dateOptionsDto, deadlineRepository.findDeadlinesOfCurrentWeekByStudent(studentId, monday, sunday));
    }

    @Override
    public DateWrapper getDeadlinesOfWeekByGroup(Long groupId, LocalDate weekDay) {
        LocalDate monday = weekDay;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }
        LocalDate sunday = weekDay;
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }

        final DateOptionsDto dateOptionsDto = DateOptionsDto
            .builder()
            .currentStartWeek(monday)
            .currentEndWeek(sunday)
            .nextWeek(monday.plusWeeks(1))
            .previousWeek(monday.minusWeeks(1))
            .build();

        return new DateWrapper(dateOptionsDto, deadlineRepository.findDeadlinesOfCurrentWeekByGroup(groupId, monday, sunday));
    }

    @Override
    public void deleteDeadlineById(Long deadlineId) {
        deadlineRepository.deleteById(deadlineId);
    }
}
