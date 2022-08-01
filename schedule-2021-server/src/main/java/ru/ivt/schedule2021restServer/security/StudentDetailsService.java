package ru.ivt.schedule2021restServer.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.ivt.schedule2021restServer.error.UnauthorizedApiException;
import ru.ivt.schedule2021restServer.models.Student;
import ru.ivt.schedule2021restServer.repositories.StudentRepository;

import java.util.Optional;

@Service
public class StudentDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public StudentDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<Student> studentCandidate = studentRepository.findStudentByName(username);
        if (studentCandidate.isPresent()) {
            return new StudentDetails(studentCandidate.get());
        } else {
            throw new UnauthorizedApiException("Студента " + username + " нет в базе данных");
        }
    }
}
