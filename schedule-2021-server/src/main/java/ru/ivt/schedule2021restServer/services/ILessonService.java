package ru.ivt.schedule2021restServer.services;

import org.springframework.data.util.Pair;
import ru.ivt.schedule2021restServer.models.Lesson;
import ru.ivt.schedule2021restServer.transfer.DateOptionsDto;

import java.time.LocalDate;
import java.util.List;

public interface ILessonService {
    Lesson findOne(Long groupId);

    Pair<List<Lesson>, DateOptionsDto> findLessonsOfCurrentWeek(Long groupId);

    Pair<List<Lesson>, DateOptionsDto> findLessonsOfCurrentWeek(Long groupId, LocalDate weekDay);
}
