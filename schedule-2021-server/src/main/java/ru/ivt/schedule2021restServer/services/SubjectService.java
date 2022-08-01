package ru.ivt.schedule2021restServer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ivt.schedule2021restServer.error.NotFoundApiException;
import ru.ivt.schedule2021restServer.models.GROUP;
import ru.ivt.schedule2021restServer.models.Student;
import ru.ivt.schedule2021restServer.models.Subject;
import ru.ivt.schedule2021restServer.repositories.StudentRepository;
import ru.ivt.schedule2021restServer.repositories.SubjectRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService implements ISubjectService {

    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<Subject> getSubjectListOfStudent(Long studentId) {
        Optional<Student> student = studentRepository.findStudentById(studentId);
        if (student.isPresent()) {
            GROUP studentGroup = student.get().getGroup();

            byte level = studentGroup.getLevel();
            level *= 2;
            if (LocalDate.now().getMonthValue() >= 9 || LocalDate.now().getMonthValue() < 2) {
                level--;
            }
            return subjectRepository.findSubjectsOfStudent(studentId, level);
        } else {
            throw new NotFoundApiException("Нет студента с id " + studentId);
        }
    }
}
