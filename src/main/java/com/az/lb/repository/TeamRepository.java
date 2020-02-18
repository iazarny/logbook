package com.az.lb.repository;

import com.az.lb.model.Org;
import com.az.lb.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    List<Team> findAllByOrg(Org org);

}
