package com.app.bdc_backend.dao;

import com.app.bdc_backend.model.Notification;
import com.app.bdc_backend.model.enums.NotificationScope;
import com.app.bdc_backend.model.user.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.OffsetScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    Window<Notification> findLastByReceiverAndScopeOrderByCreatedAtDesc(User receiver,
                                                                        NotificationScope scope,
                                                                        OffsetScrollPosition offset,
                                                                        Limit limit);

    List<Notification> findAllByReadAndReceiver(boolean read, User user);

    int countByReceiverAndRead(User receiver, boolean b);
}
