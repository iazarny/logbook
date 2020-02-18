package com.az.lb.repository;

import com.az.lb.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {

    Optional<Person> findByEmail(String email);

    @Query(value = "select p from Person p, TeamPerson t " +
            "where p.id = t.person.id and t.team.id =:teamId")
    List<Person> findAllInTeam(@Param("team") UUID teamId);

    @Query(value = "select p from Person p, TeamPerson t " +
            "where p.org.id =:orgId" +
            " and p.id <> t.person.id and t.team.id =:teamId")
    List<Person> findAllOutOfTeam(@Param("team") UUID teamId,
                                  @Param("orgId") UUID orgId);

}
