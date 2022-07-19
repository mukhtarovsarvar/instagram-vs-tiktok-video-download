package com.company.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@ToString
public class VideoDTOINS {


    public String video;

    public String image;

    public List<String> contents;

    public String caption;
}
