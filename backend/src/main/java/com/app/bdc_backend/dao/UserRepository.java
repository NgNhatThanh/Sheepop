package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.user.Role;
import com.app.bdc_backend.model.user.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    Page<User> findAllByDeletedAndRole(boolean deleted,
                                       Role role,
                                       Pageable pageable);

    Page<User> findAllByDeletedAndRoleAndFullNameContainingIgnoreCase(boolean deleted,
                                                                      Role role,
                                                                      String fullName,
                                                                      Pageable pageable);

    Page<User> findAllByDeletedAndRoleAndUsernameContainingIgnoreCase(boolean deleted,
                                                                      Role role,
                                                                      String username,
                                                                      Pageable pageable);

    Page<User> findAllByDeletedAndRoleAndPhoneNumberContainingIgnoreCase(boolean deleted,
                                                                      Role role,
                                                                      String username,
                                                                      Pageable pageable);

    Page<User> findAllByDeletedAndRoleAndEmailContainingIgnoreCase(boolean deleted,
                                                                      Role role,
                                                                      String username,
                                                                      Pageable pageable);

    boolean existsByEmail(String email);
}
