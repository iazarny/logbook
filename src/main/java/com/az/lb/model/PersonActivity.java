package com.az.lb.model;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Blob;
import java.sql.SQLException;
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
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "char(36)")
    @Type(type="uuid-char")
    private UUID id;

    @Column(name = "note", length = 8096)
    private String note;

    @Column(name = "tags", length = 256)
    private String tags;

    @Column(name = "recordct", length = 256)
    private String contentType;

    @Lob
    @Column(name = "record", columnDefinition="LONGBLOB")
    private Blob record;

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

    public Blob getRecord() {
        return record;
    }

    public void setRecord(Blob record) {
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public boolean isRecordEmpty() {
        try {
            return getRecord() == null || getRecord().length() == 0;
        } catch (SQLException e) {
            e.printStackTrace(); // to do log
            return true;
        }
    }
}
