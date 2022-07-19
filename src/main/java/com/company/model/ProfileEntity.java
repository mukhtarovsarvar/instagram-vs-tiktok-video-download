package com.company.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tg_profile")
@Getter
@Setter
public class ProfileEntity extends BaseEntity{

    @Column(unique = true)
    private Long userId;

    private String username;

    private String name;

    private String lastName;

}
