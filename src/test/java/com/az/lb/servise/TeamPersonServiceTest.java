package com.az.lb.servise;

import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.model.Team;
import com.az.lb.model.TeamPerson;
import com.az.lb.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TeamPersonServiceTest {

    @Autowired
    private OrgService orgService;

    @Autowired
    private TeamPersonService teamPersonService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonRepository personRepository;

    @Test
    void unassignPerson() {
    }

    @Test
    void assignPerson() {

        Org org = orgService.createNewOrganiation("TeamPersonServiceTest", "man@ефыва.com", "John","Da");
        Team team = teamService.createNewTeam(org.getId(), "Team");
        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            person.setEmail("any"+i+"@sdfsdf.com");
            person.setFirstName("John  " + i);
            person.setLastName("Dow " + i);

            person.setOrg(org);
            personRepository.save(person);

        }

        Person person = personRepository.findByEmail("any5@sdfsdf.com").get();

        List<TeamPerson> assigned = teamPersonService.assignPerson(
                person.getId(),
                team.getId()
        );
        assertEquals(1, assigned.size());

        assigned = teamPersonService.unassignPerson(
                person.getId(),
                team.getId()
        );

        assertEquals(0, assigned.size());



    }
}