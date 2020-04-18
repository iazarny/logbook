package com.az.lb.repository;

import com.az.lb.model.Activity;
import com.az.lb.model.Person;
import com.az.lb.model.PersonActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonActivityRepository extends JpaRepository<PersonActivity, UUID> {


    List<PersonActivity> findAllByActivity(Activity activity);
    //List<PersonActivity> findAllByActivityAndPerson(Activity activity, Person person);
    Optional<PersonActivity> findOneByActivityAndPerson(Activity activity, Person person);

}
