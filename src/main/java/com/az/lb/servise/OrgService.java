package com.az.lb.servise;

import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.repository.OrgRepository;
import com.az.lb.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Service

public class OrgService {

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private PersonRepository personRepository;

    @Transactional
    public Org updateOrganization(Org org) {
        return orgRepository.save(org);
    }

    @Transactional
    public Org createNewOrganiation(String orgName, String emailManager, String firstName, String lastName, String pwd) {
        Org org = new Org();
        org.setName(orgName);
        org = orgRepository.save(org);

        Optional<Person> checkManager = personRepository.findByEmail(emailManager);

        if (!checkManager.isPresent()) {
            final Person manager = new Person();
            manager.setEmail(emailManager);
            manager.setFirstName(firstName);
            manager.setLastName(lastName);
            manager.setOrgManager(true);
            manager.setOrg(org);
            manager.setPwd(pwd);
            manager.setPwdchanged(LocalDate.now());
            personRepository.save(manager);
        }



        return org;
    }

    @Transactional
    public Org createNewOrganiation(String orgName, String emailManager, String firstName, String lastName) {
        return  createNewOrganiation(orgName, emailManager, firstName, lastName, null);
    }

}
