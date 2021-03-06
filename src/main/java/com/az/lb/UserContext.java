package com.az.lb;

import com.az.lb.model.*;
import com.az.lb.repository.OrgRepository;
import com.az.lb.repository.PersonRepository;
import com.az.lb.servise.*;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;


@Component
@VaadinSessionScope
public class UserContext {

    private UUID userId = null;

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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            personRepository.findByEmail(user.getUsername()).ifPresent(
                    u -> {
                        org = u.getOrg();
                        userId = u.getId();
                    }
            );
            /*createTestData();*/
        }
        return org;
    }

    void createTestData() {
        Org no = new Org();
        no.setName("Default");
        orgRepository.save(no);
        org = orgRepository.save(no);

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



        person = new Person("j.pink@org.net", "Joane", "Pink", org);
        personRepository.save(person);

        person = new Person("v.cepesh@org.net","Vlad","Cepech",org);
        personRepository.save(person);

        person = new Person("p.dolgih@org.net","Piotr","Dolgih",org);
        personRepository.save(person);
        teamPersonService.assignPerson(person.getId(), team.getId());

        person = new Person("s.macdac@org.net","Scroogde","Macdac",org);
        personRepository.save(person);

        LocalDate back = LocalDate.now().minusDays(30000);
        while(back.isBefore(LocalDate.now().plusDays(1))) {
            Activity activity = personActivityService.createPersonsActivitySheet(team, back);


            personActivityService.findAllByActivity(activity).stream()
                    .forEach(

                            personActivity -> {
                                int j = (int) (Math.random() * 12);
                                for (int i = 0; i < j; i++) {
                                    PersonActivityDetail pad = new PersonActivityDetail();
                                    pad.setActivity(personActivity);
                                    pad.setTask("ASD-" + task); task ++;
                                    pad.setDetail("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." + i);
                                    pad.setSpend("7h33m2s");
                                    pad.setDone( Math.random() > 0.5);
                                    personActivityDetailService.save(pad);
                                }
                                task = task - (int) (Math.random() * 6);

                            }

                    );

            back = back.plusDays(1);

        }



    }

    int task = (int) (Math.random() * 10000);

    public Team getSelectedTeam() {
        return selectedTeam;
    }

    public void setSelectedTeam(Team selectedTeam) {
        this.selectedTeam = selectedTeam;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public UUID getUserId() {
        return userId;
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
