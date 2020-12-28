package dev.avetisyan.egs.bookstore.auth;

import dev.avetisyan.egs.bookstore.entities.RoleEntity;
import dev.avetisyan.egs.bookstore.entities.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class User implements UserDetails {

    private UserEntity user;

    public User(UserEntity user) {
        this.user = user;
    }

    public long getUserId() {
        return user.getId();
    }

    public boolean isAdmin() {
        return UserRole.ADMIN.getName().equals(user.getRole().getName());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        RoleEntity role = user.getRole();
        return Collections.singletonList(new SimpleGrantedAuthority(role.getName()));
    }

    @Override
    public String getPassword() {
        return user.getPassHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
