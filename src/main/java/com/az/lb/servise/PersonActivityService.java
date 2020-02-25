package com.az.lb.servise;

import com.az.lb.model.*;
import com.az.lb.repository.ActivityRepository;
import com.az.lb.repository.PersonActivityRepository;
import com.az.lb.repository.PersonRepository;
import com.az.lb.repository.TeamPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    public List<PersonActivity> findAllByTeamDate(Team team, LocalDate date) {

        final Activity activity = activityService.createActivity(team, date);
        return findAllByActivity(activity);
    }

    public List<PersonActivity> findAllByActivity(Activity activity) {
        PersonActivity personActivity = new PersonActivity();
        personActivity.setActivity(activity);
        Example<PersonActivity> example = Example.of(personActivity);
        return personActivityRepository.findAll(example);
    }

    /**
     * Create empty activities sheet for team. todo optimize
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
                            if(!pa.isPresent()) {
                                personActivityRepository.save(personActivity);
                            }
                        }
                );

        return activity;
    }

}
