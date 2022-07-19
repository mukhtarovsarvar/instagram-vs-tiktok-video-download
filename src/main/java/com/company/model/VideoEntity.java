package com.company.model;

import com.company.enums.VideoType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table
@Getter
@ToString
@Setter
public class VideoEntity extends BaseEntity {


    private String tgAttachId;

    @Column(unique = true,columnDefinition = "text")
    private String url;

    @Enumerated(EnumType.STRING)
    private VideoType type;


}
