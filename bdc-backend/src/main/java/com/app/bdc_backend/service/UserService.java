package com.app.bdc_backend.service;

import com.app.bdc_backend.dao.RoleRepository;
import com.app.bdc_backend.dao.UserRepository;
import com.app.bdc_backend.exception.RequestException;
import com.app.bdc_backend.model.enums.RoleName;
import com.app.bdc_backend.model.user.Role;
import com.app.bdc_backend.model.user.User;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private RoleRepository roleRepository;

    public void register(User user) {
        if(userRepository.existsByUsername(user.getUsername())) {
            throw new RequestException("Username already exists");
        }
        if(user.getPhoneNumber() != null
                && !user.getPhoneNumber().isEmpty()
                && userRepository.existsByPhoneNumber(user.getPhoneNumber())){
            throw new RequestException("Phone number already exists");
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
        return user.orElse(null);
    }

    public User findById(String id){
        return userRepository.findById(new ObjectId(id))
                .orElse(null);
    }

    public Page<User> getUserListForAdmin(int filterType,
                                          String keyword,
                                          boolean active,
                                          Pageable pageable){
        Role roleUser = roleRepository.findByName(RoleName.USER);
        switch (filterType){
            case 0:
                return userRepository.findAllByDeletedAndRole(!active, roleUser, pageable);
            case 1:
                return userRepository.findAllByDeletedAndRoleAndFullNameContainingIgnoreCase(
                        !active,
                        roleUser,
                        keyword,
                        pageable);
            case 2:
                return userRepository.findAllByDeletedAndRoleAndUsernameContainingIgnoreCase(
                        !active,
                        roleUser,
                        keyword,
                        pageable
                );
            case 3:
                return userRepository.findAllByDeletedAndRoleAndPhoneNumberContainingIgnoreCase(
                        !active,
                        roleUser,
                        keyword,
                        pageable
                );
            case 4:
                return userRepository.findAllByDeletedAndRoleAndEmailContainingIgnoreCase(
                        !active,
                        roleUser,
                        keyword,
                        pageable
                );
        }
        return new PageImpl<>(new ArrayList<>());
    }


}
