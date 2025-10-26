package com.example.androidthreadsafetypractice.model;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

// ユーザーデータを表すモデルクラス
public class User {
    // APIレスポンスのJSONキーと一致させるため、@SerializedNameを付けるのが安全です
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("username")
    private String username;

    // 必要に応じて、他のフィールド（email, addressなど）も追加できます。
    // 今回は例としてIDと名前だけを使用します。

    // Gsonがオブジェクトを生成するために引数なしのコンストラクタが推奨される場合があります
    public User() {}

    // ゲッター (Retrofit/Gsonが動作する上では必須ではありませんが、データアクセスに必要です)
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @NonNull
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Username: " + username;
    }
}