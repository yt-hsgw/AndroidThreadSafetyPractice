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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView textView1;
    private TextView textView2;

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
    }
}