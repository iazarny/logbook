package com.az.lb.servise;

import com.az.lb.model.*;
import com.az.lb.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersonActivityServiceTest {

    @Autowired
    private OrgService orgService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonActivityService personActivityService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TeamPersonService teamPersonService;


    @Test
    void createPersonsActivitySheet() {

        Org org = orgService.createNewOrganiation(
                "PersonActivityServiceTest", "man@PersonActivityServiceTest.com",
                "John","Dow");

        Team team = teamService.createNewTeam(org.getId().toString(), "PersonActivityServiceTest Team");



        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            person.setEmail("UncleBentch"+i+"@PersonActivityServiceTest.com");
            person.setFirstName("Uncle  " + i);
            person.setLastName("Bentch " + i);

            person.setOrg(org);
            Person persistedPerson = personRepository.save(person);

            teamPersonService.assignPerson(
                    persistedPerson.getId(),
                    team.getId()
            );

        }

        Activity oneMoreActivity = personActivityService.createPersonsActivitySheet(
                team,
                LocalDate.now()
        );

        assertNotNull(oneMoreActivity);

        List<PersonActivity> pa = personActivityService.findAllByTeamDate(team,
                LocalDate.now());

        assertEquals(10, pa.size());

    }
}