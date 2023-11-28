package se.kthraven.journalservice.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomAuthenticationToken implements Authentication {
    private final String userId;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String personId;

    public CustomAuthenticationToken(String userId, String username, Collection<? extends GrantedAuthority> authorities, String personId) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
        this.personId = personId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return personId;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public boolean isAuthenticated() {
        return true; // Assuming all tokens are considered as authenticated
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        //
    }

    @Override
    public String getName() {
        return username;
    }

    public String getPersonId(){
        return personId;
    }

    public String getRole(){
        if(this.authorities.size() == 1){
            List<GrantedAuthority> auths = (List<GrantedAuthority>) authorities;
            return auths.get(0).toString();
        }
        return null;
    }

    public String getUserId() {
        return userId;
    }
}
