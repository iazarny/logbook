package com.az.lb.servise;

import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.model.Team;
import com.az.lb.repository.OrgRepository;
import com.az.lb.repository.PersonRepository;
import com.az.lb.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@SpringBootTest
public class TeamServiceTest {

    @Autowired
    private OrgService orgService;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamService teamService;

    @Test
    public void createNewTeam() {

        Org org = orgService.createNewOrganiation("TeamServiceTestOrg", UUID.randomUUID().toString() + "@jungle.com",
                "John","Dow");

        Team newTeam = teamService.createNewTeam(org.getId().toString(), "Team 1");

        assertNotNull(newTeam);

        Optional<Team> team = teamRepository.findById(newTeam.getId());
        assertTrue(team.isPresent());
        team.ifPresent(
                t -> {
                    assertEquals(org.getId(), t.getOrg().getId());
                    assertEquals("Team 1", t.getName());
                }
        );

    }

}