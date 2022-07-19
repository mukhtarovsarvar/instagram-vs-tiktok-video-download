package com.company.service;

import com.company.MyTelegramBot;
import com.company.model.ProfileEntity;
import com.company.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {



    private final ProfileRepository profileRepository;

    @Lazy
    private final MyTelegramBot myTelegramBot;

    public void checkOrSave(User user){

        ProfileEntity profileEntity = profileRepository.findByUserId(user.getId()).orElse(null);

        if(profileEntity != null){
            return;
        }

        profileEntity = new ProfileEntity();
        profileEntity.setLastName(user.getLastName());
        profileEntity.setName(user.getFirstName());
        profileEntity.setUsername(user.getUserName());
        profileEntity.setUserId(user.getId());

        profileRepository.save(profileEntity);

    }

    public void sendMessageAll(String text) {

        profileRepository.findAll().forEach(profile->{

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(text);
            sendMessage.setChatId(profile.getUserId());
            myTelegramBot.send(sendMessage);

        });
    }

    public void getAllSendAdmin(Long adminId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(adminId);
        StringBuilder builder = new StringBuilder(" USERS ");
        profileRepository.findAll().forEach(profile->{
            builder.append("\nuserId: ");
            builder.append(profile.getUserId());
            builder.append("\nName: ");
            builder.append(profile.getName());
            builder.append("\nusername: ");
            builder.append(profile.getUsername());
            builder.append("\n-----------------");
        });
        sendMessage.setText(builder.toString());

        myTelegramBot.send(sendMessage);
    }

    public void getCountSendAdmin(Long adminId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(adminId);
        Long count = profileRepository.count();
        String message = "All Users: "+count;
        sendMessage.setText(message);

        myTelegramBot.send(sendMessage);
    }
}
