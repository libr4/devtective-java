package com.devtective.devtective.dominio.worker;

import com.devtective.devtective.dominio.user.AppUser;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="worker")
public class Worker {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="worker_id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private AppUser userId;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="position_id")
    private Position positionId;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="hire_date")
    private Date hireDate;

    //bellow, getters and setters only
    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public AppUser getUserId() {
        return userId;
    }

    public void setUserId(AppUser userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Position getPositionId() {
        return positionId;
    }

    public void setPositionId(Position positionId) {
        this.positionId = positionId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }
}
