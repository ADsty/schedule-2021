package ru.ivt.schedule2021restServer.services;

import ru.ivt.schedule2021restServer.models.LessonType;

import java.util.List;

public interface ILessonTypeService {
    List<LessonType> findAll();

    LessonType findOne(Long lessonTypeId);
}
