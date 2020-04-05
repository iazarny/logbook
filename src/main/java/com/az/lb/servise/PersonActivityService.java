package com.az.lb.servise;

import com.az.lb.misc.DurationValidator;
import com.az.lb.model.*;
import com.az.lb.repository.ActivityRepository;
import com.az.lb.repository.PersonActivityRepository;
import com.az.lb.repository.PersonRepository;
import com.az.lb.repository.TeamPersonRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.LobHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonActivityService {

    @Autowired
    private ActivityService activityService;


    @Autowired
    private PersonRepository personRepository;


    @Autowired
    private PersonActivityRepository personActivityRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Transactional
    public List<PersonActivity> findAllByTeamDate(Team team, LocalDate date) {

        final Activity activity = activityService.createActivity(team, date);
        return findAllByActivity(activity);
    }

    @Transactional
    public List<PersonActivity> findAllByActivity(Activity activity) {
        PersonActivity personActivity = new PersonActivity();
        personActivity.setActivity(activity);
        Example<PersonActivity> example = Example.of(personActivity);
        return personActivityRepository.findAll(example);
    }

    /**
     * Create empty activities sheet for team. todo optimize
     *
     * @param team team
     * @param date date
     */
    @Transactional
    public Activity createPersonsActivitySheet(Team team, LocalDate date) {

        final Activity activity = activityService.createActivity(team, date);

        personRepository.findAllInTeam(team.getId()).stream()
                .forEach(
                        teamMember -> {
                            PersonActivity personActivity = new PersonActivity();
                            personActivity.setActivity(activity);
                            personActivity.setPerson(teamMember);
                            Example<PersonActivity> example = Example.of(personActivity);
                            Optional<PersonActivity> pa = personActivityRepository.findOne(example);
                            if (!pa.isPresent()) {
                                personActivityRepository.save(personActivity);
                            }
                        }
                );

        return activity;
    }

    @Transactional
    public void delete(PersonActivity pa) {
        personActivityRepository.delete(pa);
    }

    @Transactional
    public PersonActivity save(PersonActivity pa) {
        return personActivityRepository.save(pa);
    }

    @Transactional
    public List<PersonActivity> findAll() {
        return personActivityRepository.findAll();
    }

    public Pair<InputStream, String> getAudio(String id) throws Exception {
        UUID pid = UUID.fromString(id);
        PersonActivity pa = personActivityRepository.getOne(pid);
        if (pa != null) {
            return Pair.of(
                    pa.getRecord().getBinaryStream(),
                    pa.getContentType()
            );
        }
        return null;
    }

    @Transactional
    public void addAudio(String id, String contentType, Long size, InputStream is) {
        UUID pid = UUID.fromString(id);
        PersonActivity pa = personActivityRepository.getOne(pid);
        if (pa != null) {
            SessionFactory sf = entityManagerFactory.unwrap(SessionFactory.class);
            Session ses = sf.openSession();
            LobHelper lb = ses.getLobHelper();
            pa.setContentType(contentType);
            pa.setRecord(lb.createBlob(is, size));
            personActivityRepository.save(pa);
        }
    }

    public String getDetailsAsHtmlTable(final List<PersonActivityDetail> detailsRaw,
                                        final String notes, final String tags, final boolean useNotesTags) {

        Duration totalTeamSpend = Duration.ZERO;
        final List<PersonActivityDetail> details  = new ArrayList<>(detailsRaw);
        details.sort((o1, o2) -> Boolean.compare(o1.isDone(), o2.isDone()));

        DurationValidator durationValidator = new DurationValidator("");

        String rez = "<table width='100%' class='detail-table'>";

        if (details.isEmpty() && useNotesTags && (StringUtils.isNotBlank(notes) || StringUtils.isNotBlank(tags))) {
            return "<tr>" + getNotesAndTags(details, notes, tags, useNotesTags) + "</tr>";
        } else {
            Duration totalSpend = Duration.ZERO;
            for (int i = 0; i < details.size(); i++) {
                PersonActivityDetail ad = details.get(i);
                totalSpend = totalSpend.plus(durationValidator.getDuration(ad.getSpend()));
                totalTeamSpend = totalTeamSpend.plus(durationValidator.getDuration(ad.getSpend()));
                String doneClass = ad.isDone() ? "task-tr task-done" : "task-tr task-not-done";
                rez += String.format(
                        "<tr class='%s'>" +
                                "<td>%s</td>" +
                                "<td>%s</td>" +
                                "<td  style='text-align: end;'>%s</td>" +
                                "%s" +
                                "</tr>",
                        doneClass,
                        ad.getTask(),
                        ad.getDetail(),
                        ad.getSpend(),
                        i == 0 ? getNotesAndTags(details, notes, tags, useNotesTags) : ""
                );
            }

            rez += String.format(
                    "<tr class='task-tr'>" +
                            "<td width='95%%' valign='top' colspan=2 class='task-total'>%s</td>" +
                            "<td width='5%%'  class='task-total-value'>%s</td>" +
                            "</tr>",
                    "Total",
                    humanizeTotal(totalSpend.toString())
            );

        }

        /*rez += String.format(
                "<tr class='task-tr'>" +
                        "<td width='95%%' valign='top' colspan=2 class='task-total'>%s</td>" +
                        "<td width='5%%'  class='task-total-value'>%s</td>" +
                        "</tr>",
                "Team Total",
                humanizeTotal(totalTeamSpend.toString())
        );*/

        rez += "</table>";
        return rez;
    }

    private String humanizeTotal (String total) {
        return total.replace("P", "")
                .replace("T", "")
                .replace("D", "d ")
                .replace("H", "h ")
                .replace("M", "m ")
                .replace("S", "s ")
                .replace("  ", " ")
                .replace("  ", " ")
                ;
    }

    public String getNotesAndTags(final List<PersonActivityDetail> details,
                                  final String notes, final String tags, final boolean useNotesTags) {
        String rez = "";
        if (useNotesTags && (StringUtils.isNotBlank(notes) || StringUtils.isNotBlank(tags))) {
            rez = String.format(
                    "<td rowspan=%d valign=top>%s<hr/>%s</td>",
                    details.size(),
                    ObjectUtils.defaultIfNull(notes, ""),
                    ObjectUtils.defaultIfNull(tags, "")
            );

        }
        return rez;

    }
}
