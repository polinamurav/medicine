package com.example.medicine.Entity;

import  org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER, ROLE_ADMIN, ROLE_DOCTOR;

    @Override
    public String getAuthority() {
        return name();
    }

}