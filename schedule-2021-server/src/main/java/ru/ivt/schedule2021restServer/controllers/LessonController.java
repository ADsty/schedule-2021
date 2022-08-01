package ru.ivt.schedule2021restServer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.ivt.schedule2021restServer.models.Lesson;
import ru.ivt.schedule2021restServer.services.ILessonService;
import ru.ivt.schedule2021restServer.transfer.DateOptionsDto;
import ru.ivt.schedule2021restServer.transfer.DateWrapper;
import ru.ivt.schedule2021restServer.transfer.LessonDto;

import java.time.LocalDate;
import java.util.List;

import static ru.ivt.schedule2021restServer.transfer.LessonDto.from;

@RestController
public class LessonController {

    @Autowired
    private ILessonService lessonService;

    @GetMapping("/lessons/{lesson-id}")
    public LessonDto getLesson(@PathVariable("lesson-id") Long lessonId) {
        return from(lessonService.findOne(lessonId));
    }

    @GetMapping("/groups/{group-id}/lessons")
    public DateWrapper getLessonsOfGroupByCurrentWeek(@PathVariable("group-id") Long groupId) {
        final Pair<List<Lesson>, DateOptionsDto> lessonsOfCurrentWeek = lessonService.findLessonsOfCurrentWeek(groupId);
        return new DateWrapper(lessonsOfCurrentWeek.getSecond(), from(lessonsOfCurrentWeek.getFirst()));
    }

    @GetMapping("/groups/{group-id}/lessons/{date}")
    public DateWrapper getLessonsOfGroupByWeek(@PathVariable("group-id") Long groupId,
                                               @PathVariable("date") String date) {
        final LocalDate localDate = LocalDate.parse(date);
        final Pair<List<Lesson>, DateOptionsDto> lessonsOfCurrentWeek = lessonService.findLessonsOfCurrentWeek(groupId, localDate);
        final List<LessonDto> from = from(lessonsOfCurrentWeek.getFirst());
        return new DateWrapper(lessonsOfCurrentWeek.getSecond(), from);
    }
}
