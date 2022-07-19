package com.company.model;

import com.company.enums.AttachType;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

@Entity
@Table(name = "video_link_ins")
@Getter
@Setter
public class VideoLinksEntity extends BaseEntity{

    @Column(name = "video_id")
    private String videoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id",insertable = false,updatable = false)
    private VideoEntity video;

    private String url;

    private String tgAttachId;

    @Enumerated(EnumType.STRING)
    private AttachType type;

    @Override
    public String toString() {
        return "VideoLinksEntity{" +
                "videoId='" + videoId + '\'' +
                ", url='" + url + '\'' +
                ", tgAttachId='" + tgAttachId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
