package com.az.lb.servise;

import com.az.lb.model.Org;
import com.az.lb.model.Team;
import com.az.lb.repository.OrgRepository;
import com.az.lb.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private TeamRepository teamRepository;


    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    @Transactional
    public Team createNewTeam(UUID orgId, String teamName) {
        Team rez = null;
        Optional<Org> org = orgRepository.findById(orgId);
        if (org.isPresent()) {
            Team team = new Team();
            team.setOrg(org.get());
            team.setName(teamName);
            rez = teamRepository.save(team);

        }
        return rez;

    }

    @Transactional
    public Team createNewTeam(String orgId, String teamName) {
        return createNewTeam(UUID.fromString(orgId), teamName);
    }

    @Transactional
    public void deleteTeam(UUID id) {
        teamRepository.deleteById(id);
    }

    @Transactional
    public Team update(Team team) {
        return teamRepository.save(team);
    }


    public Optional<Team> findById(UUID id) {
        return teamRepository.findById(id);
    }
}
