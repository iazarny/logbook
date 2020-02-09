package com.az.lb.repository;

import com.az.lb.model.Org;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrgRepository extends JpaRepository<Org, UUID> {

    Optional<Org> findByName(String name);


}
