package ru.ivt.schedule2021restServer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.ivt.schedule2021restServer.services.IFacultyService;
import ru.ivt.schedule2021restServer.transfer.FacultyDto;

import java.util.List;

import static ru.ivt.schedule2021restServer.transfer.FacultyDto.from;

@RestController
public class FacultyController {

    private final IFacultyService facultyService;

    @Autowired
    public FacultyController(final IFacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/faculties")
    public List<FacultyDto> getFaculties() {
        return from(facultyService.findAll());
    }

    @GetMapping("/faculties/{faculty-id}")
    public FacultyDto getFaculty(@PathVariable("faculty-id") Long facultyId) {
        return from(facultyService.findOne(facultyId));
    }
}
