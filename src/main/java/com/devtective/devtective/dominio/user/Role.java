package com.devtective.devtective.dominio.user;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="role_id")
    private long id;

    @Column(name="role_name")
    private String roleName;

    public Role() {
    }

    public Role(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
