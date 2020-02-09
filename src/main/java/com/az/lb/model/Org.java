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

    @Column(name = "name", length = 128, nullable = false, unique = true)
    private String name;


    @OneToMany(
            mappedBy = "org",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Team> comments = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Team> getComments() {
        return comments;
    }

    public void setComments(List<Team> comments) {
        this.comments = comments;
    }


}
