package com.az.lb.servise;


import com.az.lb.misc.DurationHumanizer;
import com.az.lb.misc.DurationValidator;
import com.az.lb.model.*;
import com.az.lb.repository.PersonActivityRepository;
import com.az.lb.repository.PersonPhotoRepository;
import com.az.lb.repository.PersonRepository;
import com.az.lb.repository.TeamRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.LobHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PersonPhotoService {

    @Autowired
    private PersonPhotoRepository personPhotoRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Transactional
    public void addPhoto(String personId, String contentType, Long size, InputStream is) {
        UUID pid = UUID.fromString(personId);
        personRepository.findById(pid).ifPresent(
                person -> {
                    final Optional<PersonPhoto> pfOpt = personPhotoRepository.findByPerson(person);
                    final PersonPhoto pf;
                    if (pfOpt.isPresent()) {
                        pf = pfOpt.get();
                    } else {
                        PersonPhoto personPhoto = new PersonPhoto();
                        personPhoto.setPerson(person);
                        pf = personPhotoRepository.save(personPhoto);
                    }
                    SessionFactory sf = entityManagerFactory.unwrap(SessionFactory.class);
                    Session ses = sf.openSession();
                    LobHelper lb = ses.getLobHelper();
                    pf.setImagect(contentType);
                    pf.setImage(lb.createBlob(is, size));
                    pf.setImagedt(LocalDateTime.now());
                    personPhotoRepository.save(pf);
                }
        );

    }

    public List<PersonPhoto> getTeamsPhoto(String teamId) {
        return personPhotoRepository.findAllInTeam(UUID.fromString(teamId));
    }

    public PersonPhoto getPersonPhoto(String personId) {
        return personPhotoRepository.findByPerson(
                personRepository.findById(UUID.fromString(personId)).get()
        ).get();
    }


}
