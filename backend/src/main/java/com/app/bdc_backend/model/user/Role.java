package com.app.bdc_backend.model.user;

import com.app.bdc_backend.model.enums.RoleName;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
@Getter
@Setter
public class Role {

    @Id
    private ObjectId id;

    private RoleName name;

    private String description;

}
