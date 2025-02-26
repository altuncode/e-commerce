package com.altuncode.myshop.services;

import com.altuncode.myshop.model.Person;
import com.altuncode.myshop.model.PersonPrincipal;
import com.altuncode.myshop.repositories.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("PersonDetailsService")
public class PersonDetailsService implements UserDetailsService {

    private final PersonRepo personRepo;

    @Autowired
    public PersonDetailsService(@Qualifier("PersonRepo") PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Person person = personRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new PersonPrincipal(person);
    }
}
