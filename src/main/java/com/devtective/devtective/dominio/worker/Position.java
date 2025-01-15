package com.devtective.devtective.dominio.worker;

import jakarta.persistence.*;

@Entity
@Table(name="position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private long id;

    @Column(name = "position_name")
    private String name;

}
