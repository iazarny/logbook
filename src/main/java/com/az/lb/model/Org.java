package com.az.lb.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "Org")
@Table(name = "org")
public class Org {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private String name;


    @OneToMany(
            mappedBy = "org",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Team> comments = new ArrayList<>();

}
