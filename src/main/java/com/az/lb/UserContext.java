package com.az.lb;

import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.repository.OrgRepository;
import com.az.lb.repository.PersonRepository;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@VaadinSessionScope
public class UserContext {

    private Org org = null;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private TeamService service;

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonRepository personRepository;

    public synchronized Org getOrg() {
        if (org == null) {
            Org no = new Org();
            no.setName("Default");
            orgRepository.save(no);
            org = orgRepository.findAll().get(0);
            System.out.println(">>>>>>>>>>>>>>>>" + org.getId());

            service.createNewTeam(
                    org.getId().toString(),
                    "Simple test value");

            Person person = new Person();
            person.setEmail("Ivan.Puzan@org.net");
            person.setFirstName("Ivan");
            person.setLastName("Puzan");
            person.setOrg(org);
            personRepository.save(person);

            person = new Person();
            person.setEmail("Jopa.Lubich@org.net");
            person.setFirstName("Jopa");
            person.setLastName("Lubich");
            person.setOrg(org);
            personRepository.save(person);
        }
        return org;
    }
}
