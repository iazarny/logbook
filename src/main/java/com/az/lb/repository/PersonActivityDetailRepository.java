package com.az.lb.repository;

import com.az.lb.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PersonActivityDetailRepository extends JpaRepository<PersonActivityDetail, UUID> {

    List<PersonActivityDetail> findAllByActivity(PersonActivity personActivity);
    //todo not optimal
    long countAllByActivity(PersonActivity personActivity);

    @Query("select pad from PersonActivityDetail pad, PersonActivity pa, Activity a, Team t  " +
            "where pad.activity.id = pa.id and pa.activity.id = a.id " +
            "  and a.dt >= :fromdt and  a.dt <= :tilldt " +
            "  and a.team.id = t.id and t.org.id = :orgId " +
            "  order by pad.task,  pa.person.lastName, pad.id")
    List<PersonActivityDetail> findAllFromTillDate(
            @Param("orgId")  UUID orgId,
            @Param("fromdt") LocalDate fromdt,
            @Param("tilldt") LocalDate tilldt);

    @Query("select pad from PersonActivityDetail pad, PersonActivity pa, Activity a, Team t  " +
            "where pad.activity.id = pa.id and pa.activity.id = a.id " +
            "  and a.dt >= :fromdt and  a.dt <= :tilldt " +
            "  and a.team.id = t.id and t.org.id = :orgId " +
            "  and pa.person.id = :personId " +
            "  order by a.dt, pad.task")
    List<PersonActivityDetail> findAllFromTillDate(
            @Param("orgId")  UUID orgId,
            @Param("personId")  UUID personId,
            @Param("fromdt") LocalDate fromdt,
            @Param("tilldt") LocalDate tilldt);

}
