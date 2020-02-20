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
/*
    @Query(value = "select p from Person p, TeamPerson t " +
            "where  p.id = t.person.id and t.team.id =:teamId")*/
@Query(value = "select t.person from  TeamPerson t " +
        "where  t.team.id =:teamId")
    List<Person> findAllInTeam(@Param("teamId") UUID teamId);




    /*select p.* from Person p
where p.org_id = 'c91e603826134c7c915133c3e9313b74'
and p.id not in
(
  select * from TeamPerson tp
   where  tp.team_id = 'f06a09a03fe046c4a12c2b0a54564dd1'
)*/

    @Query("select p from Person p where p.org.id = :orgId " +
            "and p.id not in ( select tp.person.id from TeamPerson tp where tp.team.id = :teamId)" )
    List<Person> findAllOutOfTeam(@Param("teamId") UUID teamId,
                                  @Param("orgId") UUID orgId);

}
