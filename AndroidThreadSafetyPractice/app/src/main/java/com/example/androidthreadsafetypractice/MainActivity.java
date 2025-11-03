package com.example.androidthreadsafetypractice;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidthreadsafetypractice.viewmodel.ExecutorViewModel;
import com.example.androidthreadsafetypractice.viewmodel.FlowViewModel;
import com.example.androidthreadsafetypractice.viewmodel.ProducerConsumerViewModel;
import com.example.androidthreadsafetypractice.viewmodel.RetrofitViewModel;
import com.example.androidthreadsafetypractice.viewmodel.ThreadViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);

        // Thread + ViewModel
        ThreadViewModel threadVM = new ViewModelProvider(this).get(ThreadViewModel.class);
        threadVM.getResult().observe(this, result -> textView1.setText(result));
        threadVM.loadData();

        // ExecutorService + ViewModel
        ExecutorViewModel execVM = new ViewModelProvider(this).get(ExecutorViewModel.class);
        execVM.getResult().observe(this, result -> textView2.setText(result));
        execVM.loadData();

        // Retrofit + ViewModel
        RetrofitViewModel retrofitVM = new ViewModelProvider(this).get(RetrofitViewModel.class);
        retrofitVM.getResult().observe(this, result -> textView3.setText(result));
        retrofitVM.fetchUsers();

        // ProducerConsumer + ViewModel
        ProducerConsumerViewModel producerConsumerVM = new ViewModelProvider(this).get(ProducerConsumerViewModel.class);
        producerConsumerVM.getResult().observe(this, result -> textView4.setText(result));
        producerConsumerVM.loadData();

        FlowViewModel flowVM = new ViewModelProvider(this).get(FlowViewModel.class);
        flowVM.getResult().observe(this, result -> textView5.setText(result));
        flowVM.loadData();

        try {
            FutureCancel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void FutureCancel() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("Working... " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread.sleep(2000);
        future.cancel(true); // タスク中断要求
        System.out.println("キャンセル要求を送信しました。");

    }
}