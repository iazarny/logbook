package com.az.lb;

import com.az.lb.model.*;
import com.az.lb.repository.OrgRepository;
import com.az.lb.repository.PersonRepository;
import com.az.lb.servise.*;
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

    @Autowired
    private PersonActivityService personActivityService;

    @Autowired
    private PersonActivityDetailService personActivityDetailService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private TeamPersonService teamPersonService;

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

            Team team = service.createNewTeam(
                    org.getId().toString(),
                    "Simple test value");

            service.createNewTeam(
                    org.getId().toString(),
                    "One more team");

            Person person = new Person();
            person.setEmail("john.dou@org.net");
            person.setFirstName("John");
            person.setLastName("Dou");
            person.setOrg(org);
            personRepository.save(person);
            teamPersonService.assignPerson(person.getId(), team.getId());

            person = new Person();
            person.setEmail("michael.drunk@org.net");
            person.setFirstName("Michael");
            person.setLastName("Drunk");
            person.setOrg(org);
            personRepository.save(person);
            teamPersonService.assignPerson(person.getId(), team.getId());

            person = new Person();
            person.setEmail("scott.scanotti@org.net");
            person.setFirstName("Scott");
            person.setLastName("Scanotti");
            person.setOrg(org);
            person.setOrgManager(true);
            personRepository.save(person);
            teamPersonService.assignPerson(person.getId(), team.getId());

            Activity activity = personActivityService.createPersonsActivitySheet(team, LocalDate.now());


            personActivityService.findAllByActivity(activity).stream()
                    .forEach(

                            personActivity -> {
                                int j = (int) (Math.random() * 20);
                                for (int i = 0; i < j; i++) {
                                    PersonActivityDetail pad = new PersonActivityDetail();
                                    pad.setActivity(personActivity);
                                    pad.setTask("ASD-" + task); task ++;
                                    pad.setDetail("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum." + i);
                                    pad.setSpend("1d18h33m2s");
                                    pad.setDone( Math.random() > 0.5);
                                    personActivityDetailService.save(pad);
                                }

                            }

                    );





        }
        return org;
    }

    int task = (int) (Math.random() * 1000);

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
