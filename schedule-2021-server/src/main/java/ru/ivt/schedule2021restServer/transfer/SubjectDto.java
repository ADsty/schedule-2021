package ru.ivt.schedule2021restServer.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ivt.schedule2021restServer.models.Subject;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectDto {

    private Long id;
    private String name;

    public static SubjectDto from(Subject subject) {
        return SubjectDto.builder()
            .id(subject.getId())
            .name(subject.getName())
            .build();
    }

    public static List<SubjectDto> from(List<Subject> users) {
        return users.stream().map(SubjectDto::from).collect(Collectors.toList());
    }

}
