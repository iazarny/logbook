package com.az.lb.servise;

import com.az.lb.model.Person;
import com.az.lb.model.Team;
import com.az.lb.model.TeamPerson;
import com.az.lb.repository.PersonRepository;
import com.az.lb.repository.TeamPersonRepository;
import com.az.lb.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamPersonService {


    @Autowired
    private TeamPersonRepository teamPersonRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PersonRepository personRepository;

    List<TeamPerson> findByPersonTeam(Person person, Team team) {
        return teamPersonRepository.findAllByPersonAndTeam(person, team);
    }

    @Transactional
    public List<TeamPerson> unassignPerson(UUID teamPersonId) {
        TeamPerson tp = teamPersonRepository.findById(teamPersonId).get();
        UUID tid = tp.getTeam().getId();
        teamPersonRepository.deleteById(teamPersonId);
        return teamPersonRepository.findAllByTeamId(tid);
    }

    @Transactional
    public List<TeamPerson> assignPerson(UUID personId, UUID teamId) {
        teamRepository.findById(teamId).ifPresent(
                team -> {
                    personRepository.findById(personId).ifPresent(
                            person -> {
                                teamPersonRepository.save(
                                        new TeamPerson(person, team)
                                );
                            }

                    );
                }
        );
        return teamPersonRepository.findAllByTeamId(teamId);

    }

}
