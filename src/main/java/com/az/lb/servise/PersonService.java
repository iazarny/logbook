package com.az.lb.servise;

import com.az.lb.model.*;
import com.az.lb.repository.PersonRepository;
import com.az.lb.repository.RegistrationRepository;
import com.az.lb.servise.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
public class PersonService {

    @Value("${lb.changepwd.callbackurl}")
    private String callbackurl;

    @Autowired
    private PersonRepository repository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MailService mailService;

    public List<Person> findAll(Org org) {
        return repository.findAllByOrg(org);
    }

    public List<Person> findAllInTeam(UUID teamId) {
        return repository.findAllInTeam(teamId);
    }

    public List<Person> findAllOutOfTeam(UUID teamId, UUID orgId) {
        return repository.findAllOutOfTeam(teamId, orgId);
    }

    public List<Person> findAllWithoutActivity(Org org, Activity act) {
        return repository.findAllWithoutActivity(org, act);
    }

    public Optional<Person> findById(String id) {
        return repository.findById(UUID.fromString(id));
    }

    @Transactional
    public Person save(Person person) {
        return repository.save(person);
    }

    @Transactional
    public void acceptInvitation(String invitationId, String newPwd) {
        registrationRepository.findById(UUID.fromString(invitationId)).ifPresent(
                reg -> {
                    updatePassword(reg.getEmail(), newPwd);
                    registrationRepository.delete(reg);
                }
        );
    }

    @Transactional
    public void updatePassword(String email, String newPwd) {
        repository.findByEmail(email).ifPresent(
                p -> {
                    p.setPwd(this.passwordEncoder.encode(newPwd));
                    p.setPwdchanged(LocalDate.now());
                    repository.save(p);
                }
        );
    }

    @Transactional
    public boolean forgotPassword(String email) {
        Optional<Person> optPerson = repository.findByEmail(email);
        if (optPerson.isPresent()) {
            changePassword(optPerson.get(),  MailService.MAIL_FORGOTPWD, "Reset password");
            return true;
        }
        return false;
    }

    @Transactional
    public boolean invitePerson(String email) {
        Optional<Person> optPerson = repository.findByEmail(email);
        if (optPerson.isPresent()) {
            changePassword(optPerson.get(),  MailService.MAIL_INVITE, "Invitation the Log Book ");
            return true;
        }
        return false;
    }


    public void changePassword(Person person, String templateKey, String emailSubj) {
        Registration reg = new Registration();
        reg.setFirstname(person.getFirstName());
        reg.setLastname(person.getLastName());
        reg.setEmail(person.getEmail());
        reg = registrationRepository.save(reg);

        Map<String, Object> data = new HashMap<>();
        data.put("email", person.getEmail());
        data.put("orgName", person.getOrg().getName());
        data.put("firstName", person.getFirstName());
        data.put("lastName", person.getLastName());
        data.put("callbackkey", reg.getId().toString());
        data.put("callbackurl", callbackurl);
        data.put("callbackhit", callbackurl+ "?regreq=" + reg.getId().toString());

        mailService.send(
                templateKey,
                person.getEmail(),
                emailSubj,
                data,
                null //todo
        );
    }


}
