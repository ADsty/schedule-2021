package ru.ivt.schedule2021restServer.services;

import ru.ivt.schedule2021restServer.models.GROUP;

import java.util.List;

public interface IGroupService {
    GROUP findOneById(Long groupId);

    GROUP findByName(String name);

    List<GROUP> findGroupsByFacultyID(Long facultyId);
}
