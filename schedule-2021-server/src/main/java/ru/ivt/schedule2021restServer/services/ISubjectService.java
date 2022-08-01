package ru.ivt.schedule2021restServer.services;

import ru.ivt.schedule2021restServer.models.Subject;

import java.util.List;

public interface ISubjectService {

    List<Subject> getSubjectListOfStudent(Long studentId);
}
