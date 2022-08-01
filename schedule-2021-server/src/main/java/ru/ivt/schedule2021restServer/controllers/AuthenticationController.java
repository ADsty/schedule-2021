package ru.ivt.schedule2021restServer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.ivt.schedule2021restServer.forms.StudentLoginForm;
import ru.ivt.schedule2021restServer.forms.StudentRegistrationForm;
import ru.ivt.schedule2021restServer.models.TokenResponse;
import ru.ivt.schedule2021restServer.services.StudentService;

@RestController
public class AuthenticationController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public TokenResponse registration(StudentRegistrationForm studentRegistrationForm) {
        String jwtToken = studentService.register(studentRegistrationForm);
        return new TokenResponse(jwtToken);
    }

    @PostMapping("/login")
    public TokenResponse login(StudentLoginForm studentForm) {
        String jwtToken = studentService.login(studentForm);
        return new TokenResponse(jwtToken);
    }

    @GetMapping("/validate")
    public TokenResponse validate(@RequestHeader("Authorization") String jwtToken) {
        String validatedJwtToken = studentService.validateToken(jwtToken);
        return new TokenResponse(validatedJwtToken);
    }
}
