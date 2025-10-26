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
import com.example.androidthreadsafetypractice.viewmodel.RetrofitViewModel;
import com.example.androidthreadsafetypractice.viewmodel.ThreadViewModel;

public class MainActivity extends AppCompatActivity {
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;

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
    }
}