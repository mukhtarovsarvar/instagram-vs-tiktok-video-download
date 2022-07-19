package com.company.service;

import com.company.MyTelegramBot;
import com.company.dto.VideoDTOINS;
import com.company.dto.VideoDTOTT;
import com.company.enums.AttachType;
import com.company.enums.VideoType;
import com.company.model.VideoEntity;
import com.company.model.VideoLinksEntity;
import com.company.repository.VideoRepository;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
public class VideoService {

    @Autowired
    @Lazy
    private MyTelegramBot myTelegramBot;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoLinkService videoLinkService;

    @Value("${FILEPATH}")
    private String FILEPATH;

    @Value("${telegram.username}")
    private String username;

    @Value("${tiktok.api.X_RapidAPI_KEY}")
    private String X_RapidAPI_KEY_TT;
    @Value("${tiktok.api.X_RapidAPI_HOST}")
    private String X_RapidAPI_HOST_TT;
    @Value("${tiktok.api.url}")
    private String url_TT;


    /**
     * LINK TIKTOK ENCODED
     */
    public void downloadAndSend(String link, String originLink, User user, String text) {

        VideoEntity videoEntity = getByOriginLink(originLink, VideoType.TIKTOK);

        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(user.getId());
        sendVideo.setCaption(originLink + "\n \n" + username);

        if (videoEntity == null) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url_TT + link).get()
                    .addHeader("X-RapidAPI-Key", X_RapidAPI_KEY_TT)
                    .addHeader("X-RapidAPI-Host", X_RapidAPI_HOST_TT)
                    .build();

            Response response = null;
            VideoDTOTT videoDTO = null;
            try {
                log.info("Call tiktok Api url = {}, origin url = {}", link, originLink);
                response = client.newCall(request).execute();

                Gson gson = new Gson();

                String json = response.body().string();

                videoDTO = gson.fromJson(json, VideoDTOTT.class);

                File file = getFile(videoDTO.getVideo()[0], "mp4");

                sendVideo.setVideo(new InputFile(file));

                Message message = myTelegramBot.send(sendVideo);

                save(message, originLink, VideoType.TIKTOK);

                boolean delete = file.delete();

                if (!delete) log.warn("File Deleted Failed file = {}", file);

            } catch (IOException e) {
                log.warn("Call tiktok api exeption message = {}", e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            sendVideo.setVideo(new InputFile().setMedia(videoEntity.getTgAttachId()));

            myTelegramBot.send(sendVideo);
        }

    }

    public void downloadAndSendInstagram(String link, String originLink, User user, String text) {

        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(user.getId());
        sendVideo.setCaption(originLink + "\n \n \n  " + username);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(user.getId());
        sendPhoto.setCaption(originLink + "\n \n \n " + username);

        VideoEntity videoEntity = getByOriginLink(originLink, VideoType.INSTAGRAM);

        List<VideoLinksEntity> videoList = videoLinkService.getByUrl(originLink);

        if (videoEntity == null && videoList.size() < 1) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://instagram-media-downloader.p.rapidapi.com/rapid/post.php?url=" + link)
                    .get()
                    .addHeader("X-RapidAPI-Key", "73d2f04358mshbbbfdad75748faap1f05dejsn6a2a9a4f6321")
                    .addHeader("X-RapidAPI-Host", "instagram-media-downloader.p.rapidapi.com")
                    .build();

            try {
                log.info("Call instagram Api url = {}, origin url = {}", link, originLink);
                Response response = client.newCall(request).execute();

                String gson = response.body().string();

                VideoDTOINS video = getVideoIns(gson);

                if (video.getVideo() != null) {
                    File file = getFile(video.getVideo(), "mp4");
                    sendVideo.setVideo(new InputFile(file));

                    Message message = myTelegramBot.send(sendVideo);
                    save(message, originLink, VideoType.INSTAGRAM);
                    boolean delete = file.delete();
                    if (!delete) {
                        log.warn("file Delete file ={}", file);
                    }
                } else if (video.getContents().size() > 0) {

                    for (String content : video.getContents()) {

                        if (content != null) {
                            File file;
                            if (content.contains("mp4")) {
                                file = getFile(content, "mp4");
                                sendVideo.setVideo(new InputFile(file));
                                Message message = myTelegramBot.send(sendVideo);
                                videoLinkService.save(message, originLink, AttachType.VIDEO);
                                file.delete();
                            } else {
                                file = getFile(content, "jpg");
                                sendPhoto.setPhoto(new InputFile(file));
                                Message message = myTelegramBot.send(sendPhoto);
                                videoLinkService.save(message, originLink, AttachType.PHOTO);
                                file.delete();
                            }
                        }
                    }
                }

            } catch (IOException e) {
                log.warn("Call instagram api exeption message = {}", e.getMessage());
                throw new RuntimeException(e);
            }
        } else if (!videoList.isEmpty() && videoList.get(0) != null && videoEntity == null) {

            for (VideoLinksEntity entity : videoList) {
                SendVideo video = new SendVideo();
                video.setChatId(user.getId());
                video.setCaption(originLink + "\n " + username);
                SendPhoto photo = new SendPhoto();
                photo.setChatId(user.getId());
                photo.setCaption(originLink + "\n \n \n " + username);

                switch (entity.getType()) {
                    case VIDEO -> {
                        video.setVideo(new InputFile(entity.getTgAttachId()));
                        myTelegramBot.send(video);
                    }
                    case PHOTO -> {
                        photo.setPhoto(new InputFile(entity.getTgAttachId()));
                        myTelegramBot.send(photo);
                    }
                }

            }
        } else {
            assert videoEntity != null;
            sendVideo.setVideo(new InputFile().setMedia(videoEntity.getTgAttachId()));
            myTelegramBot.send(sendVideo);
        }

    }

    public void save(Message message, String originLink, VideoType type) {

        VideoEntity entity = new VideoEntity();
        entity.setType(type);
        entity.setUrl(originLink);
        entity.setTgAttachId(message.getVideo().getFileId());

        videoRepository.save(entity);
    }


    /**
     * URl TIKTOK API GET video url
     */

    public File getFile(String url, String extension) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url).get().build();

        Response response = null;

        try {
            response = client.newCall(request).execute();

            InputStream inputStream = response.body().byteStream();


            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);


            byte[] bytes = bufferedInputStream.readAllBytes();


            return writeByte(bytes, extension);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getInputStream(String url) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url).get().build();

        Response response = null;

        try {
            response = client.newCall(request).execute();

            InputStream inputStream = response.body().byteStream();

            return new BufferedInputStream(inputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public VideoEntity getByOriginLink(String originLink, VideoType type) {
        return videoRepository.findByUrlAndType(originLink, type).orElse(null);
    }

    private File writeByte(byte[] bytes, String extension) {

        // Try block to check for exceptions
        try {

            String id = UUID.randomUUID().toString();

            File file = new File(FILEPATH);

            if (!file.exists()) {
                file.mkdirs();
            }

            Path path = Paths.get(FILEPATH + id + "." + extension);

            Files.write(path, bytes);

            // Display message onconsole for successful
            // execution
            System.out.println("Successfully"
                    + " byte inserted");

            // Close the file connections

            return new File(FILEPATH + id + "." + extension);
        }
        // Catch block to handle the exceptions
        catch (Exception e) {
            // Display exception on console
            System.out.println("Exception: " + e);
        }

        return null;
    }

    private VideoDTOINS getVideoIns(String json) {

        VideoDTOINS video = new VideoDTOINS();


        Object obj = JSONValue.parse(json);
        JSONObject jsonObject = (JSONObject) obj;

        boolean check = true;

        List<String> images = new LinkedList<>();
        int count = 0;

        while (check) {

            String image = (String) jsonObject.get(String.valueOf(count));

            if (image == null) {
                check = false;
            } else {
                images.add(image);
            }

            count++;
        }

        String caption = (String) jsonObject.get("caption");
        String videoLink = (String) jsonObject.get("video");


        if (images.size() > 0) {
            if (images.get(0) != null) video.setContents(images);
            video.setVideo(videoLink);

        } else {
            video.setVideo(videoLink);
        }

        video.setCaption(caption);

        return video;
    }
}
