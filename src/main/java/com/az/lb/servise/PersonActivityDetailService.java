package com.az.lb.servise;

import com.az.lb.misc.DurationHumanizer;
import com.az.lb.misc.DurationValidator;
import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.model.PersonActivity;
import com.az.lb.model.PersonActivityDetail;
import com.az.lb.repository.PersonActivityDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


@Service
public class PersonActivityDetailService {





    @Autowired
    private PersonActivityDetailRepository repository;

    @Transactional
    public PersonActivityDetail save(PersonActivityDetail detail) {
        return repository.save(detail);
    }

    public List<PersonActivityDetail> findActivityDetail(PersonActivity personActivity) {
        return repository.findAllByActivity(personActivity); //OrderByDoneTask
    }

    // TODO is present slave records . Wil be faster
    public long countAllByActivity(PersonActivity personActivity) {
        return repository.countAllByActivity(personActivity);
    }

    @Transactional
    public void delete(PersonActivityDetail personActivityDetail) {
        repository.delete(personActivityDetail);
    }



}
