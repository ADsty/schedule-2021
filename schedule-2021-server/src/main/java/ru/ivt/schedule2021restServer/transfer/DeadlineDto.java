package ru.ivt.schedule2021restServer.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ivt.schedule2021restServer.models.Deadline;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeadlineDto {
    private Long id;
    private String title;
    private String subjectName;
    private String description;
    private LocalTime notificationTime;
    private LocalDate deadlineDate;
    private String type;
    private LessonDto lesson;
    private String mediaURL;

    public static DeadlineDto from(Deadline deadline) {
        DeadlineDtoBuilder deadlineDtoBuilder = DeadlineDto.builder()
            .id(deadline.getId())
            .title(deadline.getTitle())
            .description(deadline.getDescription())
            .notificationTime(deadline.getNotificationTime())
            .deadlineDate(deadline.getDeadlineDate())
            .type(deadline.getType());

        if (deadline.getLesson() != null) {
            deadlineDtoBuilder.lesson(LessonDto.from(deadline.getLesson()));
        }

        return deadlineDtoBuilder.mediaURL(deadline.getMediaUrl())
            .type(deadline.getType())
            .lesson(LessonDto.from(deadline.getLesson()))
            .mediaURL(deadline.getMediaUrl())
            .subjectName(deadline.getSubject().getName())
            .build();
    }

    public static List<DeadlineDto> from(List<Deadline> users) {
        return users.stream().map(DeadlineDto::from).collect(Collectors.toList());
    }
}
