package com.company;

import com.company.controller.MessageController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Slf4j
@Controller
public class MyTelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.username}")
    private String username;

    @Value("${telegram.token}")
    private String token;

    @Autowired
    private MessageController messageController;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {

        log.info("update = {}", update.getMessage());
        if (update.hasMessage()) {

            Message message = update.getMessage();

            User user = message.getFrom();

            if (message.hasText()) {
                log.info("user = {}", user);
                log.info("message = {}", message);
                messageController.hasText(user, message);
            }



        }
    }


    public Message send(Object object) {

        try {
            if (object instanceof SendMessage) {
                return execute((SendMessage) object);
            } else if (object instanceof EditMessageText) {
                execute((EditMessageText) object);
            } else if (object instanceof SendPhoto) {
                return execute((SendPhoto) object);
            } else if (object instanceof SendVideo) {
                return execute((SendVideo) object);
            } else if (object instanceof SendContact) {
                return execute((SendContact) object);
            } else if (object instanceof SendLocation) {
                return execute((SendLocation) object);
            } else if (object instanceof DeleteMessage) {
                execute((DeleteMessage) object);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}


