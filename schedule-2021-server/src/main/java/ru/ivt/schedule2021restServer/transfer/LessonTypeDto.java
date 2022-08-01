package ru.ivt.schedule2021restServer.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ivt.schedule2021restServer.models.LessonType;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonTypeDto {
    private Long id;
    private String name;

    public static LessonTypeDto from(LessonType lessonType) {
        return LessonTypeDto.builder()
            .id(lessonType.getId())
            .name(lessonType.getName())
            .build();
    }

    public static List<LessonTypeDto> from(List<LessonType> users) {
        return users.stream().map(LessonTypeDto::from).collect(Collectors.toList());
    }
}
