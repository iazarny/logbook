package com.az.lb.servise;

import com.az.lb.model.PersonActivity;
import com.az.lb.model.PersonActivityDetail;
import com.az.lb.repository.PersonActivityDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
}
