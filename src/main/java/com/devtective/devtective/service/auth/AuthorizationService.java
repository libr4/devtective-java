package com.devtective.devtective.service.auth;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user =  repository.findByUsername(username);
        System.out.println("USER: " + user.getUsername() + " PASS:" + user.getPassword());
        return user;
    }
}
