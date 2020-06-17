package com.az.lb.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Blob;
import java.time.LocalDate;
import java.util.UUID;

/**
 * No history.
 */
@Entity(name = "PersonPhoto")
@Table(name = "personphoto")
public class PersonPhoto {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "char(36)")
    @Type(type="uuid-char")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;

    @Lob
    @Column(name = "image", columnDefinition="LONGBLOB")
    private Blob image;

    @Column(name = "imagect", length = 256)
    private String imagect;

    @Column(name = "imagedt")
    private LocalDate imagedt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public String getImagect() {
        return imagect;
    }

    public void setImagect(String imagect) {
        this.imagect = imagect;
    }

    public LocalDate getImagedt() {
        return imagedt;
    }

    public void setImagedt(LocalDate imagedt) {
        this.imagedt = imagedt;
    }
}
