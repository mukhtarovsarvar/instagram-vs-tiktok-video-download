package com.company.service;

import com.company.enums.AttachType;
import com.company.model.VideoLinksEntity;
import com.company.repository.VideoLinksRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Service
@Slf4j

public class VideoLinkService {

    @Autowired
    private VideoLinksRepository videoLinksRepository;


    public void save(Message message, String originLink, AttachType type ){
        VideoLinksEntity entity = new VideoLinksEntity();
        entity.setUrl(originLink);

        if(type.equals(AttachType.VIDEO)){
            entity.setTgAttachId(message.getVideo().getFileId());
        }else if (type.equals(AttachType.PHOTO)){
            entity.setTgAttachId(message.getPhoto().get(0).getFileId());
        }
        entity.setType(type);

        videoLinksRepository.save(entity);
    }


    public List<VideoLinksEntity> getByUrl(String url){
      return videoLinksRepository.findByUrl(url);
    }
}
