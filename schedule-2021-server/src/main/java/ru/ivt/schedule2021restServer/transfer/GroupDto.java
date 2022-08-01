package ru.ivt.schedule2021restServer.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ivt.schedule2021restServer.models.GROUP;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDto {
    private Long id;
    private String name;
    private byte level;
    private String type;
    private byte origin;

    public static GroupDto from(GROUP group) {
        return GroupDto.builder()
            .id(group.getId())
            .name(group.getName())
            .level(group.getLevel())
            .type(group.getType())
            .origin(group.getOrigin())
            .build();
    }

    public static List<GroupDto> from(List<GROUP> users) {
        return users.stream().map(GroupDto::from).collect(Collectors.toList());
    }
}
