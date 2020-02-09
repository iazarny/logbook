package com.az.lb.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

public class PersonActivityDetail {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    private PersonActivity activity;

    @Column(name = "task", length = 32)
    private String task;

    @Column(name = "name", length = 512, nullable = false)
    private String name;

    @Column(name = "detail", length = 32768)
    private String detail;

    @Column(name = "spend", length = 32)
    private String spend;

    private int done;
}
