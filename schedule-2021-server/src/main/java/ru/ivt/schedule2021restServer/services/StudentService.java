package ru.ivt.schedule2021restServer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ivt.schedule2021restServer.error.ForbiddenApiException;
import ru.ivt.schedule2021restServer.error.NotFoundApiException;
import ru.ivt.schedule2021restServer.forms.StudentLoginForm;
import ru.ivt.schedule2021restServer.forms.StudentRegistrationForm;
import ru.ivt.schedule2021restServer.models.GROUP;
import ru.ivt.schedule2021restServer.models.Student;
import ru.ivt.schedule2021restServer.models.StudentRole;
import ru.ivt.schedule2021restServer.repositories.StudentRepository;
import ru.ivt.schedule2021restServer.security.JwtUtil;
import ru.ivt.schedule2021restServer.security.StudentDetails;
import ru.ivt.schedule2021restServer.security.StudentDetailsService;

import java.util.Optional;

@Service
public class StudentService implements IStudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentDetailsService studentDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private GroupService groupService;

    @Override
    public String register(StudentRegistrationForm studentRegistrationForm) {
        final String studentName = studentRegistrationForm.getName();
        final String groupName = studentRegistrationForm.getGroupName();
        final String password = studentRegistrationForm.getPassword();
        final StudentRole role = StudentRole.valueOf(studentRegistrationForm.getRole());

        if (studentName == null || groupName == null || password == null) {
            throw new NotFoundApiException("Неправильные ключи студента при регистрации");
        }

        final Optional<Student> studentCandidate = studentRepository.findStudentByName(studentName);

        if (studentCandidate.isPresent()) {
            throw new NotFoundApiException("Студент " + studentName + " уже зарегистрирован");
        }

        String hashPassword = passwordEncoder.encode(password);

        GROUP group = groupService
            .findByName(groupName);

        Student student = Student
            .builder()
            .group(group)
            .name(studentName)
            .hashPassword(hashPassword)
            .role(role)
            .build();

        studentRepository.save(student);

        final StudentDetails studentDetails = studentDetailsService.loadUserByUsername(studentRegistrationForm.getName());

        return jwtUtil.generateToken(studentDetails);
    }

    public String login(StudentLoginForm studentLoginForm) {
        final String studentName = studentLoginForm.getName();
        final String password = studentLoginForm.getPassword();

        final Optional<Student> studentCandidate = studentRepository.findStudentByName(studentName);

        if (studentCandidate.isEmpty()) {
            throw new NotFoundApiException("Студент " + studentName + " не зарегистрирован");
        }

        Student student = studentCandidate.get();

        boolean isPasswordMatches = passwordEncoder.matches(password, student.getHashPassword());

        if (!isPasswordMatches) {
            throw new NotFoundApiException("Неправильный пароль");
        }

        final StudentDetails studentDetails = studentDetailsService.loadUserByUsername(studentName);

        return jwtUtil.generateToken(studentDetails);
    }

    public String validateToken(String jwtTokenHeader) {
        String jwtToken;
        if (jwtTokenHeader.startsWith("Bearer ")) {
            jwtToken = jwtTokenHeader.substring(7);
        } else {
            throw new ForbiddenApiException("Некорректный префикс токена");
        }

        final String studentName = jwtUtil.extractUserName(jwtToken);
        final StudentDetails studentDetails = studentDetailsService.loadUserByUsername(studentName);

        jwtUtil.validateToken(jwtToken, studentDetails);

        return jwtUtil.generateToken(studentDetails);
    }
}
