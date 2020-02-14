package com.az.lb.servise;

import com.az.lb.model.Person;
import com.az.lb.model.Team;
import com.az.lb.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    @Autowired
    private PersonRepository repository;

    public List<Person> findAll() {
        return repository.findAll();
    }
}
