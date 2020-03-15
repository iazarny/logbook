package com.az.lb.servise;

import com.az.lb.model.Activity;
import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.model.Team;
import com.az.lb.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class PersonService {

    @Autowired
    private PersonRepository repository;

    public List<Person> findAll() {
        return repository.findAll();
    }

    public List<Person> findAll(Org org) {
        return repository.findAllByOrg(org);
    }

    public List<Person> findAllInTeam(UUID teamId) {
        return repository.findAllInTeam(teamId);
    }

    public List<Person> findAllOutOfTeam(UUID teamId, UUID orgId) {
        return repository.findAllOutOfTeam(teamId, orgId);
    }

    public List<Person> findAllWithoutActivity(Org org, Activity act) {
        return repository.findAllWithoutActivity(org, act);
    }

    @Transactional
    public Person save(Person person) {
        return repository.save(person);
    }
}
