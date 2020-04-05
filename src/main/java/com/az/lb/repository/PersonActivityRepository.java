package com.az.lb.repository;

import com.az.lb.model.PersonActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonActivityRepository extends JpaRepository<PersonActivity, UUID> {



}
