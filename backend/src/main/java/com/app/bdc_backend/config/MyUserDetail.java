package com.app.bdc_backend.config;

import com.app.bdc_backend.model.enums.RoleName;
import com.app.bdc_backend.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class MyUserDetail implements UserDetails {

    private String username;

    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public MyUserDetail(User user){
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority(user.getRole().getName().toString()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
