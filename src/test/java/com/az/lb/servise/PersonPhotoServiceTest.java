package com.az.lb.servise;

import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.model.PersonPhoto;
import com.az.lb.model.Team;
import com.az.lb.repository.PersonPhotoRepository;
import com.az.lb.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersonPhotoServiceTest {

    @Autowired
    private OrgService orgService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonPhotoService personPhotoService ;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonPhotoRepository personPhotoRepository;

    @Autowired
    private TeamPersonService teamPersonService;


    @Test
    void addPhoto() throws Exception {
        String content = "Whatever, Lorem ipsum";
        Org org = orgService.createNewOrganiation("Raima", "man@asd.com", "Dron","Baton");
        Team team = teamService.createNewTeam(org.getId(), "Team 00");
        Person person = new Person();
        person.setEmail("do@asd.com");
        person.setFirstName("Dark  ");
        person.setLastName("Ozzy ");
        person.setOrg(org);
        personRepository.save(person);


        personPhotoService.addPhoto(person.getId().toString(), "app/noname",
                0L + content.getBytes().length, new ByteArrayInputStream(content.getBytes()));

        PersonPhoto pf = personPhotoRepository.findByPerson(person).get();
        long len = pf.getImage().length();
        byte arr[] = new byte[(int)len];
        pf.getImage().getBinaryStream().read(arr,0, (int)len);
        String contentBack = new String(arr);
        assertEquals(content, contentBack);
    }

    @Test
    void getTeamsPhoto() {
        Org org = orgService.createNewOrganiation("Funny", "man@funny.com", "Derek","White");
        Team team = teamService.createNewTeam(org.getId(), "Team");
        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            person.setEmail("do"+i+"@funny.com");
            person.setFirstName("Dark  " + i);
            person.setLastName("Ozzy " + i);
            person.setOrg(org);
            personRepository.save(person);

            teamPersonService.assignPerson(
                    person.getId(),
                    team.getId()
            );
        }

        assertTrue(personPhotoService.getTeamsPhoto(team.getId().toString()).isEmpty());

        for (int i = 0; i < 10; i++) {
            final String email = "do"+i+"@funny.com";
            personRepository.findByEmail(email).ifPresent(
                    person -> {
                        personPhotoService.addPhoto(person.getId().toString(), "plaint/text",
                                0L + email.getBytes().length, new ByteArrayInputStream(email.getBytes()));
                    }
            );
        }

        assertEquals(10, personPhotoService.getTeamsPhoto(team.getId().toString()).size());
        System.out.println();

    }
}