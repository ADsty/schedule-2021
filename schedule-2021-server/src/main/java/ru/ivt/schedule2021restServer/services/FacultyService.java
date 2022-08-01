package ru.ivt.schedule2021restServer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.ivt.schedule2021restServer.models.Faculty;
import ru.ivt.schedule2021restServer.repositories.FacultyRepository;

import java.util.List;

@Service
@CacheConfig(cacheNames = {"faculties"})
public class FacultyService implements IFacultyService {

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    @Cacheable
    public List<Faculty> findAll() {
        return facultyRepository.findAllByOrderByIdAsc();
    }

    @Override
    @Cacheable
    public Faculty findOne(Long facultyId) {
        return facultyRepository.findById(facultyId).orElseThrow(IllegalAccessError::new);
    }
}
