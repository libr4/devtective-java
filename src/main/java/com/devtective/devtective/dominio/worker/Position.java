package com.devtective.devtective.dominio.worker;

import jakarta.persistence.*;

@Entity
@Table(name="position")
public class Position {

    public Position() {
    }
    public Position(Long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private long id;

    @Column(name = "position_name")
    private String name;

}
