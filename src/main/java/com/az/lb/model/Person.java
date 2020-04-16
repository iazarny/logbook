package com.az.lb.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity(name = "Person")
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name= "email", length = 128, nullable = false, unique = true)
    private String email;

    @Column(name= "firstName", length = 128)
    private String firstName;

    @Column(name= "lastName", length = 128)
    private String lastName;

    @ManyToOne(fetch = FetchType.EAGER)
    private Org org;

    @Column(name = "manager")
    private Boolean orgManager;

    @Column(name = "pwd")
    private String pwd;

    @Column(name = "pwdchanged")
    private LocalDate pwdchanged;



    public Person() {
    }

    public Person(String email, String firstName, String lastName, Org org) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.org = org;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public LocalDate getPwdchanged() {
        return pwdchanged;
    }

    public void setPwdchanged(LocalDate pwdchanged) {
        this.pwdchanged = pwdchanged;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getOrgManager() {
        return orgManager;
    }

    public void setOrgManager(Boolean orgManager) {
        this.orgManager = orgManager;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
