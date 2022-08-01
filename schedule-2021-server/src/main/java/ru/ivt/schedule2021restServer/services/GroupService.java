package ru.ivt.schedule2021restServer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.ivt.schedule2021restServer.error.NotFoundApiException;
import ru.ivt.schedule2021restServer.models.GROUP;
import ru.ivt.schedule2021restServer.repositories.GROUPRepository;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"groups"})
public class GroupService implements IGroupService {

    @Autowired
    private GROUPRepository groupRepository;

    @Override
    @Cacheable
    public GROUP findOneById(Long groupId) {
        final Optional<GROUP> groupCandidate = groupRepository.findById(groupId);
        if (groupCandidate.isPresent()) {
            return groupCandidate.get();
        } else {
            throw new NotFoundApiException("Нет группы с id " + groupId);
        }
    }

    @Override
    @Cacheable
    public GROUP findByName(String groupName) {
        final Optional<GROUP> groupCandidate = groupRepository.findByName(groupName);

        if (groupCandidate.isPresent()) {
            return groupCandidate.get();
        } else {
            throw new NotFoundApiException("Нет группы с name " + groupName);
        }
    }

    @Override
    public List<GROUP> findGroupsByFacultyID(Long facultyId) {
        return groupRepository.findGroupsByFacultyId(facultyId);
    }
}
