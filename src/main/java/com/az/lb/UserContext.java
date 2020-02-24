package com.az.lb;

import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.model.Team;
import com.az.lb.repository.OrgRepository;
import com.az.lb.repository.PersonRepository;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;


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

    private Team selectedTeam;

    private LocalDate selectedDate;



    public synchronized Org getOrg() {
        if (org == null) {
            Optional<Org> defaultOrg = orgRepository.findByName("Default");
            if (defaultOrg.isPresent()) {
                return defaultOrg.get();
            }
            Org no = new Org();
            no.setName("Default");
            orgRepository.save(no);
            org = orgRepository.findAll().get(0);
            System.out.println(">>>>>>>>>>>>>>>>" + org.getId());

            service.createNewTeam(
                    org.getId().toString(),
                    "Simple test value");

            service.createNewTeam(
                    org.getId().toString(),
                    "One more team");

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

            person = new Person();
            person.setEmail("Dua.Lipa@org.net");
            person.setFirstName("Dua");
            person.setLastName("Lipa");
            person.setOrg(org);
            personRepository.save(person);
        }
        return org;
    }

    public Team getSelectedTeam() {
        return selectedTeam;
    }

    public void setSelectedTeam(Team selectedTeam) {
        this.selectedTeam = selectedTeam;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    @Override
    public String toString() {
        return "UserContext{" +
                "org=" + org +
                ", selectedTeam=" + selectedTeam +
                ", selectedDate=" + selectedDate +
                '}';
    }
}
