package se.kthraven.journalservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SimpleAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null) {
            Authentication authentication = buildAuthenticationFromToken(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    private Authentication buildAuthenticationFromToken(String token) {
        String[] tokenParts = null;
        if (token.contains("Bearer")) {
            tokenParts = token.split(" ");
            tokenParts = tokenParts[1].split(":");


            for (String s : tokenParts)
                System.out.println(s);
            String id = tokenParts[0];
            String username = tokenParts[1];
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(tokenParts[2]));
            String personId = tokenParts[3];
            return new CustomAuthenticationToken(id, username, authorities, personId);
        }
        return null;
    }
}

