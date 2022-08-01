package ru.ivt.schedule2021restServer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ivt.schedule2021restServer.models.LessonType;
import ru.ivt.schedule2021restServer.repositories.LessonTypeRepository;

import java.util.List;

@Service
public class LessonTypeService implements ILessonTypeService {

    private final LessonTypeRepository lessonTypeRepository;

    @Autowired
    public LessonTypeService(LessonTypeRepository lessonTypeRepository) {
        this.lessonTypeRepository = lessonTypeRepository;
    }

    @Override
    public List<LessonType> findAll() {
        return lessonTypeRepository.findAll();
    }

    @Override
    public LessonType findOne(Long lessonTypeId) {
        return lessonTypeRepository.findById(lessonTypeId).orElseThrow(IllegalAccessError::new);
    }
}
