package com.app.bdc_backend.model;

import com.app.bdc_backend.model.user.User;
import com.app.bdc_backend.util.RandomUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Document(collection = "forgot_password_tokens")
@Getter
@Setter
@Builder
public class ForgotPasswordToken {

    @Id
    private int id;

    @Builder.Default
    private String token = RandomUtil.getRandomString(15);

    @DocumentReference
    private User user;

    @Builder.Default
    private Date createdAt = new Date();

    private Date expiredAt;

}
