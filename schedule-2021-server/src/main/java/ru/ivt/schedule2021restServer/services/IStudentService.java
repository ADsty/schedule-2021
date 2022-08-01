package ru.ivt.schedule2021restServer.services;

import ru.ivt.schedule2021restServer.forms.StudentRegistrationForm;

public interface IStudentService {
    String register(StudentRegistrationForm studentRegistrationForm);
}
