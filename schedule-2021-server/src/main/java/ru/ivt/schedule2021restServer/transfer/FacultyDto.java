package ru.ivt.schedule2021restServer.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ivt.schedule2021restServer.models.Faculty;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacultyDto {
    private Long id;
    private String name;
    private String shortName;

    public static FacultyDto from(Faculty faculty) {
        return FacultyDto.builder()
            .id(faculty.getId())
            .name(faculty.getName())
            .shortName(faculty.getShortName())
            .build();
    }

    public static List<FacultyDto> from(List<Faculty> users) {
        return users.stream().map(FacultyDto::from).collect(Collectors.toList());
    }
}
