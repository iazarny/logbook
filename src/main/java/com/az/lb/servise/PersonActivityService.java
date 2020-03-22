package com.az.lb.servise;

import com.az.lb.misc.DurationValidator;
import com.az.lb.model.*;
import com.az.lb.repository.ActivityRepository;
import com.az.lb.repository.PersonActivityRepository;
import com.az.lb.repository.PersonRepository;
import com.az.lb.repository.TeamPersonRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonActivityService {

    @Autowired
    private ActivityService activityService;


    @Autowired
    private PersonRepository personRepository;


    @Autowired
    private PersonActivityRepository personActivityRepository;

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

    public String getDetailsAsHtmlTable(final List<PersonActivityDetail> details,
                                        final String notes, final String tags, final boolean useNotesTags) {

        DurationValidator durationValidator = new DurationValidator("");

        String rez = "<table width='100%' class='detail-table'>";

        if (details.isEmpty() && useNotesTags && (StringUtils.isNotBlank(notes) || StringUtils.isNotBlank(tags))) {
            return "<tr>" + getNotesAndTags(details, notes, tags, useNotesTags) + "</tr>";
        } else {
            Duration totalSpend = Duration.ZERO;
            for (int i = 0; i < details.size(); i++) {

                PersonActivityDetail ad = details.get(i);

                totalSpend = totalSpend.plus(durationValidator.getDuration(ad.getSpend()));

                rez += String.format(
                        "<tr class='detail-table-tr'>" +
                                "<td  valign='top' >%s</td>" +
                                "<td  valign='top' >%s</td>" + //todo several lines, br up to param
                                "<td  valign='top' aligin=right>%s</td>" +
                                "%s" +
                                "</tr>",
                        ad.getTask(),
                        ad.getDetail(),
                        ad.getSpend(),
                        i == 0 ? getNotesAndTags(details, notes, tags, useNotesTags) : ""

                );

            }

            rez += String.format(
                    "<tr class='detail-table-tr'>" +
                            "<td width='95%%' valign='top' colspan=2>%s</td>" +
                            "<td width='5%%' valign='top' aligin=right>%s</td>" +
                            "</tr>",
                    "Total",
                    totalSpend.toString()

            );

        }

        rez += "</table>";


        return rez;
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
