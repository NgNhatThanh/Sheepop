package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.enums.RoleName;
import com.app.bdc_backend.model.user.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    Role findByName(RoleName name);

}
