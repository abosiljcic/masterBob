package com.masterproject.Master.Bob.utility;

import okhttp3.*;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

public class Email {

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient().newBuilder().build();
    }

    public void sendEmail(String from, String to, String subject, String text, String category) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType,
                "{\"from\":{\"email\":\"" + from + "\",\"name\":\"Master Bob\"},\"to\":[{\"email\":\"" + to + "\"}],\"subject\":\"" + subject + "\",\"text\":\"" + text + "\",\"category\":\"" + category + "\"}");
        Request request = new Request.Builder()
                .url("https://send.api.mailtrap.io/api/send")
                .method("POST", body)
                .addHeader("Authorization", "Bearer c4a3ed4d8448e923e297c40034ada0a3")
                .addHeader("Content-Type", "application/json")
                .build();

        try(Response response = httpClient().newCall(request).execute()) {

        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
