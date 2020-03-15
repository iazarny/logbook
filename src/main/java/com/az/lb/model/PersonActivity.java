package com.az.lb.model;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Arrays;
import java.util.UUID;

@Entity(name = "PersonActivity")
@Table(name = "personactivity")
public class PersonActivity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "note", length = 32768)
    private String note;

    @Column(name = "tags", length = 256)
    private String tags;

    @Lob
    @Column(name = "record", columnDefinition="BLOB")
    private byte[] record;

    @ManyToOne(fetch = FetchType.EAGER)
    private Activity activity;

    @ManyToOne(fetch = FetchType.EAGER)
    private Person person;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public byte[] getRecord() {
        return record;
    }

    public void setRecord(byte[] record) {
        this.record = record;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "PersonActivity{" +
                "id=" + id +
                ", note='" + note + '\'' +
                ", tags='" + tags + '\'' +
                ", activity=" + activity +
                ", person=" + person +
                '}';
    }
}
