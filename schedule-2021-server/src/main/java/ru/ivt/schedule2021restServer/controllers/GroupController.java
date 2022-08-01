package ru.ivt.schedule2021restServer.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.util.UriEncoder;
import ru.ivt.schedule2021restServer.services.IGroupService;
import ru.ivt.schedule2021restServer.transfer.GroupDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.ivt.schedule2021restServer.transfer.GroupDto.from;

@RestController
public class GroupController {

    private final IGroupService groupService;

    @Autowired
    public GroupController(final IGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/groups/{group-id}")
    public GroupDto getGroup(@PathVariable("group-id") Long groupId) {
        return from(groupService.findOneById(groupId));
    }

    @GetMapping("/groups/**")
    public GroupDto getGroupByName(HttpServletRequest request) {
        String groupName = request.getRequestURI().split(request.getContextPath() + "/groups/")[1];
        return from(groupService.findByName(UriEncoder.decode(groupName)));
    }


    @GetMapping("/faculties/{faculty-id}/groups")
    public List<GroupDto> getGroupsByFacultyID(@PathVariable("faculty-id") Long facultyId) {
        return from(groupService.findGroupsByFacultyID(facultyId));
    }
}
