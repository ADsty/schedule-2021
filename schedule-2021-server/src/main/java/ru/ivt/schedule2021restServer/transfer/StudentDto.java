package ru.ivt.schedule2021restServer.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDto {
    private String id;
}

//public class GroupDto {
//    private Long id;
//    private String name;
//    private Long apiId;
//    private Long level;
//    private String type;
//    private Long origin;
//    private Long year;
//    private Long departmentSpecializationId;
//
//    public static GroupDto from(GROUP group) {
//        return GroupDto.builder()
//            .id(group.getId())
//            .name(group.getName())
//            .apiId(group.getApiId())
//            .level(group.getLevel())
//            .type(group.getType())
//            .origin(group.getOrigin())
//            .year(group.getYear())
//            .departmentSpecializationId(group.getDepartmentSpecializationId())
//            .build();
//    }
//
//    public static List<GroupDto> from(List<GROUP> users) {
//        return users.stream().map(GroupDto::from).collect(Collectors.toList());
//    }
//}
