package com.az.lb.servise;

import com.az.lb.model.Activity;
import com.az.lb.model.Org;
import com.az.lb.model.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
class ActivityServiceTest {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private TeamService teamService;



    @Test
    void createActivity() {

        Org org = orgService.createNewOrganiation("ActivityServiceTest", "man@ActivityServiceTest.com", "John","Dow");

        Team newTeam = teamService.createNewTeam(org.getId().toString(), "ActivityServiceTest Team");

        Activity activity = activityService.createActivity(newTeam, LocalDate.now());

        assertNotNull(activity);

        Activity oneMoreActivity = activityService.createActivity(newTeam, LocalDate.now());

        assertTrue(activity != oneMoreActivity);
        assertEquals(activity.getId(), oneMoreActivity.getId());

        Team team = teamService.createNewTeam(org.getId().toString(), "ActivityServiceTest Team1");
        Activity activity1 = activityService.createActivity(team, LocalDate.now());
        assertEquals(2, activityService.findAll(LocalDate.now()).size());

    }

    @Test
    void findAllByDate() {
        Org org = orgService.createNewOrganiation("ActivityServiceTest1",
                "man2@ActivityServiceTest.com", "John","Dow");

        Team newTeam = teamService.createNewTeam(org.getId().toString(), "ActivityServiceTest Team0");


        Activity activity0 = activityService.createActivity(newTeam, LocalDate.now());



    }
}