package ru.ivt.schedule2021restServer.services;

import ru.ivt.schedule2021restServer.models.LessonGroup;

import java.util.List;

public interface ILessonGroupService {

    List<LessonGroup> findAll();


}
