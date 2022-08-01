package ru.ivt.schedule2021restServer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.ivt.schedule2021restServer.services.ILessonTypeService;
import ru.ivt.schedule2021restServer.transfer.LessonTypeDto;

import java.util.List;

import static ru.ivt.schedule2021restServer.transfer.LessonTypeDto.from;

@RestController
public class LessonTypeController {

    private final ILessonTypeService lessonTypeService;

    @Autowired
    public LessonTypeController(final ILessonTypeService lessonTypeService) {
        this.lessonTypeService = lessonTypeService;
    }

    @GetMapping("/lesson_types")
    public List<LessonTypeDto> getLessonTypes() {
        return from(lessonTypeService.findAll());
    }

    @GetMapping("/lesson_types/{lesson-type-id}")
    public LessonTypeDto getLessonType(@PathVariable("lesson-type-id") Long lessonTypeId) {
        return from(lessonTypeService.findOne(lessonTypeId));
    }
}

