package ru.ivt.schedule2021restServer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.ivt.schedule2021restServer.services.ISubjectService;
import ru.ivt.schedule2021restServer.transfer.SubjectDto;

import java.util.List;

import static ru.ivt.schedule2021restServer.transfer.SubjectDto.from;

@RestController
public class SubjectController {

    @Autowired
    private ISubjectService subjectService;

    @GetMapping("/students/{student-id}/subjects")
    public List<SubjectDto> getCurrentSubjectsOfStudent(@PathVariable("student-id") Long studentId) {
        return from(subjectService.getSubjectListOfStudent(studentId));
    }
}
