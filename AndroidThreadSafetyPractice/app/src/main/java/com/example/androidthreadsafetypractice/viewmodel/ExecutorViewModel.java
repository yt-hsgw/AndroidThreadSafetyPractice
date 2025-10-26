package com.example.androidthreadsafetypractice.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ExecutorService + ViewModel構成
 * スレッドプールで非同期実行し、HandlerでUI更新
 */
public class ExecutorViewModel extends ViewModel {
    private final MutableLiveData<String> result = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    // LiveDataを外部に公開
    public LiveData<String> getResult() {
        return result;
    }

    // 疑似ネットワーク通信をExecutorServiceで実行
    public void loadData() {
        executor.execute(() -> {
            try {
                Thread.sleep(2000);
                String data = "ExecutorService + Handler で通信成功！";
                handler.post(() -> result.setValue(data));
            } catch (InterruptedException e) {
                handler.post(() -> result.setValue("エラー: " + e.getMessage()));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown(); // ViewModel破棄時にリソース解放
    }
}
