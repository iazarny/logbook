package com.az.lb.servise;

import com.az.lb.model.Person;
import com.az.lb.model.Team;
import com.az.lb.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PersonService {

    @Autowired
    private PersonRepository repository;

    public List<Person> findAll() {
        return repository.findAll();
    }

    public List<Person> findAllInTeam(UUID teamId) {
        return repository.findAllInTeam(teamId);
    }

    public List<Person> findAllOutOfTeam(UUID teamId, UUID orgId) {
        return repository.findAllOutOfTeam(teamId, orgId);
    }
}
