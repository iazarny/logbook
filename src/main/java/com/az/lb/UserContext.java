package com.az.lb;

import com.az.lb.model.Org;
import com.az.lb.repository.OrgRepository;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@VaadinSessionScope
public class UserContext {

    private Org org = null;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private TeamService service;

    public Org getOrg() {
        if (org == null) {
            Org no = new Org();
            no.setName("Default");
            orgRepository.save(no);
            System.out.println(">>>>>>>>>>>>>>>> resolving org" );
            org = orgRepository.findAll().get(0);
            System.out.println(">>>>>>>>>>>>>>>>  org is "  +org);
            System.out.println(">>>>>>>>>>>>>>>>" + org.getId());
           // org = orgRepository.findById(UUID.fromString("12345")).get();

            service.createNewTeam(
                    org.getId().toString(),
                    "Simple test value");
        }
        return org;
    }
}
