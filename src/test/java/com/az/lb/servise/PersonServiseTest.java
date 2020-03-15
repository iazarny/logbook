package com.az.lb.servise;

import com.az.lb.model.*;
import com.az.lb.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
public class PersonServiseTest {

    @Autowired
    private OrgService orgService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonActivityService personActivityService;

    @Autowired
    private PersonService personServise;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private TeamPersonService teamPersonService;

    @Test
    public void findAllWithoutActivity() {

        Org org = orgService.createNewOrganiation("TeamPersonServiceTest", "man@ефыва.com", "John","Da");
        Team team = teamService.createNewTeam(org.getId(), "Team");
        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            person.setEmail("any"+i+"@sdfsdf.com");
            person.setFirstName("John  " + i);
            person.setLastName("Dow " + i);

            person.setOrg(org);
            personRepository.save(person);

            List<TeamPerson> assigned = teamPersonService.assignPerson(
                    person.getId(),
                    team.getId()
            );
        }


        Activity todayActivity = personActivityService.createPersonsActivitySheet(
                team,
                LocalDate.now()
        );

        Activity yesterdayActivity = personActivityService.createPersonsActivitySheet(
                team,
                LocalDate.now().minusDays(1)
        );


        cleanUpPersonActivities(todayActivity, 3); // 4 without activities
        cleanUpPersonActivities(yesterdayActivity, 8); // 9


        assertEquals(4 , personServise.findAllWithoutActivity(org, todayActivity).size());
        assertEquals(9 , personServise.findAllWithoutActivity(org, yesterdayActivity).size());



    }

    private void cleanUpPersonActivities(Activity act, int suf) {
        personActivityService.findAllByActivity(act).stream().filter(
                pa -> {
                    String lastName = pa.getPerson().getLastName();
                    int  idx = Integer.valueOf(
                            lastName.substring(
                                    lastName.length() -1,
                                    lastName.length()
                            )
                    );
                    return idx < suf; // 4 persons
                }
        ).forEach(
                pa->{
                    personActivityService.delete(pa);
                }
        );
    }

}