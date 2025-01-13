package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.UserRepository;
import com.app.bdc_backend.exception.DataExistedException;
import com.app.bdc_backend.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public void register(User user) {
        if(userRepository.existsByUsername(user.getUsername())) {
            throw new DataExistedException("Username already exists");
        }
        if(userRepository.existsByPhoneNumber(user.getPhoneNumber())){
            throw new DataExistedException("Phone number already exists");
        }
        user.setCreatedAt(new Date());
        String hassPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hassPassword);
        userRepository.save(user);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public User findByUsername(String username){
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()){
            return user.get();
        }
        throw new DataExistedException("Username not found");
    }

}
