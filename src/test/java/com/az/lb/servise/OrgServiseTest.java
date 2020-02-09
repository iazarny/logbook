package com.az.lb.servise;

import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.repository.OrgRepository;
import com.az.lb.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Optional;

import static org.junit.Assert.*;

@SpringBootTest
public class OrgServiseTest {

    @Autowired
    private OrgService orgService;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void createNewOrganisation() {
        Org org = orgService.createNewOrganiation("Jungle", "man@jungle.com", "John","Dow");
        Optional<Person> manager = personRepository.findByEmail("man@jungle.com");
        assertTrue(manager.isPresent());
        manager.ifPresent(
                m -> {
                    assertEquals("John", m.getFirstName());
                    assertEquals("Dow", m.getLastName());
                    assertEquals(org.getId(), m.getOrg().getId());
                    assertTrue(m.getOrgManager());
                }
        );

        assertEquals("Jungle", org.getName());
    }
}