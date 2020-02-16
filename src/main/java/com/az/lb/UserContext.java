package com.az.lb;

import com.az.lb.model.Org;
import com.az.lb.repository.OrgRepository;
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

    public synchronized Org getOrg() {
        if (org == null) {
            System.out.println(">>>>>>>>>>>>>>>> resolving org" );
            org = orgRepository.findAll().get(0);
            System.out.println(">>>>>>>>>>>>>>>>  org is "  +org);
            System.out.println(">>>>>>>>>>>>>>>>" + org.getId());
           // org = orgRepository.findById(UUID.fromString("12345")).get();
        }
        return org;
    }
}
