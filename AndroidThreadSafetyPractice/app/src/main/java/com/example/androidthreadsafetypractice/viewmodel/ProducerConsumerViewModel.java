package com.example.androidthreadsafetypractice.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidthreadsafetypractice.model.User;
import com.example.androidthreadsafetypractice.network.ApiService;
import com.example.androidthreadsafetypractice.network.RetrofitClient;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import retrofit2.Response;

/**
 * Retrofit通信の結果をBlockingQueue経由でConsumerに渡し、UIに反映するViewModel。
 * Producer: Retrofit通信 (networkExecutor)
 * Consumer: キュー待機とUI更新 (networkExecutor -> Handler)
 */
public class ProducerConsumerViewModel extends ViewModel {
    // ProducerとConsumerが共有するブロッキングキュー
    private final BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();

    // ログ履歴を保持し、UIに通知するためのLiveData
    private final MutableLiveData<String> result = new MutableLiveData<>("");

    // メインスレッドでLiveDataを更新するためのHandler
    private final Handler handler = new Handler(Looper.getMainLooper());

    // ネットワーク処理実行用のExecutorService (マルチスレッド化: Consumerのブロッキングを防ぐ)
    // 修正: SingleThreadExecutorからFixedThreadPool(2)に変更
    private final ExecutorService networkExecutor = Executors.newFixedThreadPool(2);

    private final ApiService apiService = RetrofitClient.getApiService();

    // LiveDataを外部に公開
    public LiveData<String> getResult() {
        return result;
    }

    /**
     * LiveDataにログメッセージを安全に追記します。
     * 必ずメインスレッドで実行されるようにHandlerを使用します。
     */
    private void setMessage(String message) {
        handler.post(() -> result.setValue(message + "\n"));
    }

    /**
     * ProducerとConsumerのタスクをExecutorServiceに投入します。
     */
    public void loadData() {
        // 処理開始時にログをクリア
        result.setValue("--- Retrofit Producer/Consumer 処理開始 ---\n");

        // 1. ConsumerタスクをExecutorServiceに投入 (キューが満たされるのを待機)
        // FixedThreadPoolの別スレッドがこれを受け持つ
        networkExecutor.execute(new Consumer());

        // 2. ProducerタスクをExecutorServiceに投入 (通信を実行し、キューを満たす)
        // FixedThreadPoolの別のスレッドがこれを受け持つ
        networkExecutor.execute(new Producer());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // ViewModelが破棄されたとき、ExecutorServiceをシャットダウンし、実行中のタスクを中断する
        networkExecutor.shutdownNow();
        responseQueue.clear();
    }

    /**
     * Producerタスクの実装: Retrofit通信を実行し、結果をキューに投入する。
     */
    private class Producer implements Runnable {
        @Override
        public void run() {
            try {
                if (Thread.interrupted()) throw new InterruptedException();

                setMessage("🟢 Producer: API通信開始 (Retrofit同期実行)...");

                // Retrofitの同期実行 (Call.execute())
                Response<List<User>> response = apiService.getUsers().execute();

                String data;
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    User firstUser = response.body().get(0);
                    data = "成功: " + firstUser.toString();
                } else {
                    data = "失敗: " + response.code() + " " + response.message();
                }

                setMessage("🟢 Producer: データ取得完了。キューに投入します。");
                // 結果をキューに投入 (Consumerを待機状態から解放)
                responseQueue.put(data);

            } catch (Exception e) {
                setMessage("❌ Producer: 通信エラー発生: " + e.getMessage());
                try {
                    responseQueue.put("ERROR: 通信失敗 - " + e.getMessage());
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            } finally {
                setMessage("✅ Producerタスク終了");
            }
        }
    }

    /**
     * Consumerタスクの実装: キューを待機し、結果をLiveDataに反映する。
     */
    private class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                if (Thread.interrupted()) throw new InterruptedException();

                setMessage("🔵 Consumer: キューの待機を開始...");

                // キューにデータが来るまでブロッキングして待機
                String response = responseQueue.take();

                setMessage("🔵 Consumer: データを受信: " + response);

                // 最終結果をUIに表示
                setMessage("✅ Consumer: 処理完了。UIに最終結果を反映。");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                setMessage("❌ Consumer: 待機処理が中断されました。");
            } finally {
                setMessage("✅ Consumerタスク終了");
            }
        }
    }
}
