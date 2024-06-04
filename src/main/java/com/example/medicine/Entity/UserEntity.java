package com.example.medicine.Entity;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import  org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
//@Table(name = "users")
@Data //чтобы не писать конструктор геттер, сеттер - lombok
@AllArgsConstructor //конструктор со всеми полями, которые присутствуют
@NoArgsConstructor
public class UserEntity implements UserDetails {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iduser;
    @Getter
    private String username;
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "surname", unique = true)
    private String surname;

    @Column(name = "birth")
    private Date birth;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "disease_history")
    private String disease_history;

    private boolean active;

    @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private ImageUserEntity images;

    //один пользователь может иметь целый список записей
//    @Getter
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user") //если удаляем пользователя, то и из базы данных удаляем все записи, которые были прекреплены к пользователю
//    private List<Appointment> appointmentsList;


    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "iduser")) //primary key - user_id
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private DoctorEntity doctor;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointment> appointmentsList;



    //security
    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }

    public boolean isDoctor() {
        return roles.contains(Role.ROLE_DOCTOR);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
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
        return active;
    }

    public void addImageToUser(ImageUserEntity images) {
        images.setUserEntity(this);
        this.images = images;
    }
}