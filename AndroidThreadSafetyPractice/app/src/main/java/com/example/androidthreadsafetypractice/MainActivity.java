package com.example.androidthreadsafetypractice;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

interface ApiService {
    @GET("users")
    Call<List<User>> getUsers();
}

public class MainActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;

    // 通信方法の種類を表すenum
    enum Type {
        HANDLER_POST,
        RUN_ON_UI_THREAD,
        VIEW_POST,
        EXECUTOR_SERVICE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

        // Threadを使った例
        new Thread(() -> {
            // バックグラウンド処理
            String result = loadDataFromNetwork(Type.HANDLER_POST);

            // UIスレッドへ戻る
            // ex1) Handler.post()
            // ex2) Activity.runOnUiThread()
            // ex3) View.post()
            handler.post(() -> textView1.setText(result));
        }).start();

        // ExecutorServiceを使った例
        executor.execute(() -> {
            String result = loadDataFromNetwork(Type.EXECUTOR_SERVICE);
            handler.post(() -> {
            textView2.setText(result);
            });
        });

        // Retrofitを使った例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/") // サンプルAPIのベースURL
                .addConverterFactory(GsonConverterFactory.create()) // JSONコンバータ
                .build(); // Retrofitインスタンスの作成
        ApiService service = retrofit.create(ApiService.class);

        // Retrofitの同期通信をExecutorServiceで実行
        executor.execute(() -> {
            try {
                // 同期的な実行 (execute()) を非同期スレッドで行う
                Response<List<User>> response = service.getUsers().execute();
                // 通信成功時の処理
                if (response.isSuccessful() && response.body() != null) {
                    List<User> userList = response.body();
                    String firstUserName = userList.get(0).getName();
                    String displayMessage = "ユーザー取得成功！\n最初のユーザー: " + firstUserName + "\n総ユーザー数: " + userList.size();
                    // UIスレッドでTextViewを更新
                    handler.post(() -> textView3.setText(displayMessage));
                } else {
                    // 通信はできたが、ステータスコードがエラーの場合（例: 404, 500）
                    String errorMessage = "APIエラー: " + response.code() + " " + response.message();
                    handler.post(() -> textView3.setText(errorMessage));
                }
            } catch (IOException e) {
                // 通信自体が失敗した場合（ネットワーク接続なしなど）
                String errorMessage = "通信エラー: " + e.getMessage();
                handler.post(() -> textView3.setText(errorMessage));
            }
        });
    }

    // 疑似的なネットワーク通信処理
    private String loadDataFromNetwork(Type type) {
        try { Thread.sleep(2000); } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return type.name() + "で通信成功！";
    }

    // Edge-to-Edge対応の初期化
    private void initViews() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
    }
}