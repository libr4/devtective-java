package com.devtective.devtective.dominio.user;

import jakarta.persistence.*;

@Entity
@Table(name = "user_discoverability")
public class UserDiscoverability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;



    @Column(name="code")
    private String code;

    public UserDiscoverability() {
    }

    public UserDiscoverability(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String roleName) {
        this.code = code;
    }
}
