package com.az.lb.repository;

import com.az.lb.model.Activity;
import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.model.PersonPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonPhotoRepository extends JpaRepository<PersonPhoto, UUID> {

    @Query(value = "select pf from  PersonPhoto pf,  TeamPerson tp " +
        "where  tp.team.id =:teamId and pf.person = tp.person ")
    List<PersonPhoto> findAllInTeam(@Param("teamId") UUID teamId);

    Optional<PersonPhoto> findByPerson(Person p);


}
