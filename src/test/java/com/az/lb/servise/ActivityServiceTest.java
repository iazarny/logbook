package com.az.lb.servise;

import com.az.lb.model.Activity;
import com.az.lb.model.Org;
import com.az.lb.model.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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

    }
}