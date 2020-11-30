package com.example.chatapp.fragments;

import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAQutQZ3A:APA91bHvs2qz-y0PczjZBSkEKAkmjaZBJBwk7GAfwDeocY8FApF2MEzmYLVR0TMwjkq3YKnTUupqIVHNyoQ8faOi4M0me4k7FxHYvsh0O7NWmpxLVgrnvkQj4R30jde8I-zi_3RU5dNa"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender sender);



}
