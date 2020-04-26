package com.az.lb.servise;

import com.az.lb.model.Person;
import com.az.lb.repository.PersonRepository;
import com.az.lb.servise.mail.MailSendJob;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@Service
public class LogBookUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(LogBookUserDetailsService.class);

    @Autowired
    private PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Optional<Person> optP = personRepository.findByEmail(username);
        if (optP.isPresent()) {
            Person p = optP.get();

            final List<GrantedAuthority> authorities = new ArrayList<>();
            if (BooleanUtils.toBoolean(p.getOrgManager())) {
                authorities.add(new SimpleGrantedAuthority("ADM"));
            }
            authorities.add(new SimpleGrantedAuthority("USER"));

            final boolean enabled = p.getPwdchanged() != null
                    && LocalDate.now().minusDays(100).isBefore(p.getPwdchanged()); //todo 100

            boolean locked = BooleanUtils.toBoolean(p.getBlocked());

            if (locked) {
                logger.info("User  {0}, {1} is blocked", p.getFullName(), p.getEmail());
            }

            final UserDetails userDetails = new User(
                    p.getEmail(),
                    p.getPwd(),
                    enabled,
                    true, true, !locked,
                    authorities
            );

            return userDetails;
        }
        throw new UsernameNotFoundException("User not found");
    }


}
