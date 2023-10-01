package com.example.common.common.event;

import com.example.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;
@Getter
public class UserRegisterEvent extends ApplicationEvent {
    private User user;
    public UserRegisterEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public UserRegisterEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
