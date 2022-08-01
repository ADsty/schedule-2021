package ru.ivt.schedule2021restServer.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ivt.schedule2021restServer.models.Lesson;
import ru.ivt.schedule2021restServer.models.Teacher;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonDto {

    private Long id;
    private LocalDate lessonDate;
    private String place;
    private String type;
    private String subjectName;
    private String timeStart;
    private String timeEnd;
    private List<Teacher> teacherList;

    public static LessonDto from(Lesson lesson) {
        return LessonDto.builder()
            .id(lesson.getId() == null ? null : lesson.getId())
            .lessonDate(lesson.getLessonDate())
            .place(lesson.getPlace())
            .type(lesson.getType().getName())
            .subjectName(lesson.getSubject().getName())
            .timeStart(lesson.getTimeStart())
            .timeEnd(lesson.getTimeEnd())
            .teacherList(lesson.getTeacherList())
            .build();
    }

    public static List<LessonDto> from(List<Lesson> users) {
        return users.stream().map(LessonDto::from).collect(Collectors.toList());
    }

}
