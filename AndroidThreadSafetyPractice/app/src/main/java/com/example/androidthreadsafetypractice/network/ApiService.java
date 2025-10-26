package com.example.androidthreadsafetypractice.network;

import com.example.androidthreadsafetypractice.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

// Retrofitを使用してAPIエンドポイントを定義するインターフェース
public interface ApiService {
    @GET("users")
    Call<List<User>> getUsers();
}