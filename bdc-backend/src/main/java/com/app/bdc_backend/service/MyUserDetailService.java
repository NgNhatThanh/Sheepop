package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.UserRepository;
import com.app.bdc_backend.model.user.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;


@Component
@AllArgsConstructor
public class MyUserDetailService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()){
            return new org.springframework.security.core.userdetails.User(
                    user.get().getUsername(),
                    user.get().getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }
        else{
            throw new UsernameNotFoundException("Username not found");
        }
    }

}
