package ru.ivt.schedule2021restServer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.ivt.schedule2021restServer.models.Lesson;
import ru.ivt.schedule2021restServer.repositories.LessonRepository;
import ru.ivt.schedule2021restServer.transfer.DateOptionsDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class LessonService implements ILessonService {

    private final LessonRepository lessonRepository;

    @Autowired
    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public Lesson findOne(Long lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(IllegalAccessError::new);
    }

    @Override
    public Pair<List<Lesson>, DateOptionsDto> findLessonsOfCurrentWeek(Long groupId) {
        return findLessonsOfCurrentWeek(groupId, LocalDate.now());
    }

    @Override
    public Pair<List<Lesson>, DateOptionsDto> findLessonsOfCurrentWeek(Long groupId, LocalDate weekDay) {
        LocalDate monday = weekDay;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }
        LocalDate sunday = weekDay;
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }

        final DateOptionsDto dateOptionsDto =
            DateOptionsDto.builder()
                .currentStartWeek(monday)
                .currentEndWeek(sunday)
                .nextWeek(monday.plusWeeks(1))
                .previousWeek(monday.minusWeeks(1))
                .build();

        final List<Lesson> currentWeekLessonsOfGroup = lessonRepository.findCurrentWeekLessonsOfGroup(groupId, monday, sunday);

        return Pair.of(currentWeekLessonsOfGroup, dateOptionsDto);
    }
}
