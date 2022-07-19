package com.company.controller;


import com.company.MyTelegramBot;
import com.company.enums.CurrentStatus;
import com.company.service.CurrentStatusService;
import com.company.service.ProfileService;
import com.company.service.VideoService;
import com.company.util.EncodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@Controller
public class MessageController {


    @Autowired
    @Lazy
    private MyTelegramBot myTelegramBot;

    @Autowired
    private VideoService videoService;


    @Autowired
    @Lazy
    private ProfileService profileService;

    @Value("${admin.id}")
    private Long adminId;

    public void hasText(User user, Message message) {

        String text = message.getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId());
        DeleteMessage deleteMessage = new DeleteMessage();

        CurrentStatus status = CurrentStatusService.getCurrenStatus(user.getId());

        if (status.equals(CurrentStatus.DEFAULT)) {
            if (text.equals("/start")) {
                sendMessage.setText("Assalomu Alekum.\n botdan foydalanish uchun link jo'nating ");
                sendMessage.enableMarkdown(true);
                myTelegramBot.send(sendMessage);

                profileService.checkOrSave(user);

            } else if (text.startsWith("https://") && text.contains("tiktok")) {

                sendMessage.setText("⏳");
                Message send = myTelegramBot.send(sendMessage);
                videoService.downloadAndSend(EncodeUtil.encodeTTLink(text), text, user, "zo'r");

                deleteMessage.setChatId(message.getChatId());
                deleteMessage.setMessageId(send.getMessageId());
                myTelegramBot.send(deleteMessage);
            } else if (text.startsWith("https://") && text.contains("instagram")) {

                sendMessage.setText("⏳");
                Message send = myTelegramBot.send(sendMessage);

                videoService.downloadAndSendInstagram(EncodeUtil.encodeTTLink(text), text, user, "zor");

                deleteMessage.setChatId(message.getChatId());
                deleteMessage.setMessageId(send.getMessageId());
                myTelegramBot.send(deleteMessage);

            } else if (text.equals("/send") && user.getId().equals(adminId)) {
                sendMessage.setText("TEXTNI KIRITING!");

                CurrentStatusService.currentStatus.put(user.getId(), CurrentStatus.SEND);

                myTelegramBot.send(sendMessage);
            } else if (text.equals("/get") && user.getId().equals(adminId)) {
                profileService.getAllSendAdmin(adminId);
            } else if (text.equals("/count") && user.getId().equals(adminId)) {
                profileService.getCountSendAdmin(adminId);
            }
        } else if (status.equals(CurrentStatus.SEND) && user.getId().equals(adminId)) {
            CurrentStatusService.currentStatus.remove(user.getId());
            profileService.sendMessageAll(text);
        }

    }
}
