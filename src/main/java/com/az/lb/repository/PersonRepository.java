package com.az.lb.repository;

import com.az.lb.model.Activity;
import com.az.lb.model.Org;
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

    @Query(value = "select t.person from  TeamPerson t " +
        "where  t.team.id =:teamId")
    List<Person> findAllInTeam(@Param("teamId") UUID teamId);

    @Query("select p from Person p where p.org.id = :orgId " +
            "and p.id not in ( select tp.person.id from TeamPerson tp where tp.team.id = :teamId)" )
    List<Person> findAllOutOfTeam(@Param("teamId") UUID teamId,
                                  @Param("orgId") UUID orgId);

    List<Person> findAllByOrg(Org org);

    @Query("select p from Person p where p.org = :org " +
            "and p.id not in ( select pa.person.id from PersonActivity pa where pa.activity = :act)" )
    List<Person> findAllWithoutActivity(Org org, Activity act);

}
