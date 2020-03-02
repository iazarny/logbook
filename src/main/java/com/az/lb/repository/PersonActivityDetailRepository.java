package com.az.lb.repository;

import com.az.lb.model.PersonActivity;
import com.az.lb.model.PersonActivityDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonActivityDetailRepository extends JpaRepository<PersonActivityDetail, UUID> {

    List<PersonActivityDetail> findAllByActivity(PersonActivity personActivity);

}
