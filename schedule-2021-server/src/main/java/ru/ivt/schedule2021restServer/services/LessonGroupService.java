package ru.ivt.schedule2021restServer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ivt.schedule2021restServer.models.LessonGroup;
import ru.ivt.schedule2021restServer.repositories.LessonGroupRepository;

import java.util.List;

@Service
public class LessonGroupService implements ILessonGroupService {

    private final LessonGroupRepository lessonGroupRepository;

    @Autowired
    public LessonGroupService(final LessonGroupRepository lessonGroupRepository) {
        this.lessonGroupRepository = lessonGroupRepository;
    }

    @Override
    public List<LessonGroup> findAll() {
        return lessonGroupRepository.findAll();
    }

}
