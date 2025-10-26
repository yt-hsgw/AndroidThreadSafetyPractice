package com.example.androidthreadsafetypractice.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * シンプルなThread + ViewModel構成
 * HandlerでUIスレッドに戻す
 */
public class ThreadViewModel extends ViewModel {
    private final MutableLiveData<String> result = new MutableLiveData<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    // LiveDataを外部に公開
    public LiveData<String> getResult() {
        return result;
    }

    // 疑似ネットワーク通信をThreadで実行
    public void loadData() {
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 疑似ネットワーク通信
                String data = "Thread + Handler で通信成功！";
                handler.post(() -> result.setValue(data));
            } catch (InterruptedException e) {
                handler.post(() -> result.setValue("エラー: " + e.getMessage()));
            }
        }).start();
    }
}
