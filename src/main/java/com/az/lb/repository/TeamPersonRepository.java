package com.az.lb.repository;

import com.az.lb.model.Person;
import com.az.lb.model.Team;
import com.az.lb.model.TeamPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamPersonRepository extends JpaRepository<TeamPerson, UUID> {

    List<TeamPerson> findAllByTeamId(UUID teamId);
    List<TeamPerson> findAllByTeam(Team team);
    List<TeamPerson> findAllByPersonAndTeam(Person person, Team team);


}
