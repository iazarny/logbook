package com.az.lb.servise;

import com.az.lb.model.Activity;
import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.model.Team;
import com.az.lb.repository.ActivityRepository;
import com.az.lb.repository.OrgRepository;
import com.az.lb.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service

public class ActivityService {


    @Autowired
    private ActivityRepository activityRepository;

    @Transactional
    public Activity createActivity(Team team, LocalDate activityDate) {
        Activity activity = new Activity();
        activity.setTeam(team);
        activity.setDt(activityDate);
        Example<Activity> activityExample  = Example.of(activity);
        Optional<Activity> existingActivity = activityRepository.findOne(activityExample);
        if (existingActivity.isPresent()) {
            return existingActivity.get();
        }
        return activityRepository.save(activity);
    }

    @Transactional
    public List<Activity> findAll(LocalDate localDate) {
        Activity activity = new Activity();
        activity.setDt(localDate);
        Example<Activity> activityExample  = Example.of(activity);
        return activityRepository.findAll();
    }

}
