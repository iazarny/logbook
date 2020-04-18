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

        Org org = orgService.createNewOrganiation("Wo ist dein ausweis", "man@ausweis.com", "Do","Hast");
        Team team = teamService.createNewTeam(org.getId(), "Team");
        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            person.setEmail("wo"+i+"@sdfsdf.com");
            person.setFirstName("Wo  " + i);
            person.setLastName("ist " + i);

            person.setOrg(org);
            personRepository.save(person);

        }

        Person person = personRepository.findByEmail("wo5@sdfsdf.com").get();

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