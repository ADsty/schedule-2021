package ru.ivt.schedule2021restServer.services;

import ru.ivt.schedule2021restServer.models.Faculty;

import java.util.List;

public interface IFacultyService {
    List<Faculty> findAll();

    Faculty findOne(Long facultyId);
}
