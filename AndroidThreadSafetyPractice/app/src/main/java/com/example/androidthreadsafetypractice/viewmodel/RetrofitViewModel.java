package com.example.androidthreadsafetypractice.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidthreadsafetypractice.model.User;
import com.example.androidthreadsafetypractice.network.ApiService;
import com.example.androidthreadsafetypractice.network.RetrofitClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit + ExecutorService + ViewModel構成
 * 実際のAPI通信をViewModel内で管理
 */
public class RetrofitViewModel extends ViewModel {
    private final MutableLiveData<String> result = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ApiService service = RetrofitClient.getApiService();

    // コンストラクタでRetrofitとApiServiceを初期化
    public RetrofitViewModel() {}

    // LiveDataを外部に公開
    public LiveData<String> getResult() {
        return result;
    }

    // APIからユーザーデータを取得
    public void fetchUsers() {
        executor.execute(() -> {
            try {
                Response<List<User>> response = service.getUsers().execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    String msg = "ユーザー取得成功！\n最初のユーザー: " +
                            users.get(0).getName() +
                            "\n総ユーザー数: " + users.size();
                    handler.post(() -> result.setValue(msg));
                } else {
                    handler.post(() -> result.setValue("APIエラー: " + response.code()));
                }
            } catch (IOException e) {
                Log.e("RetrofitViewModel", "通信エラー", e);
                handler.post(() -> result.setValue("通信失敗: " + e.getMessage()));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
