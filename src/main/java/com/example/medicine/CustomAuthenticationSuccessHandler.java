package com.example.medicine;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_DOCTOR")) {
                response.sendRedirect("/doctor/doctor-main");
                return;
            }

            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/users");
                return;
            }
        }

        // Если пользователь не является доктором, перенаправляем его на обычную страницу
        response.sendRedirect("/");
    }
}
