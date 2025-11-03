package com.example.androidthreadsafetypractice.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerConsumerViewModel extends ViewModel {
    // ProducerとConsumerが共有するブロッキングキュー
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    // ログ履歴を保持し、UIに通知するためのLiveData
    private final MutableLiveData<String> result = new MutableLiveData<>("");

    // メインスレッドでLiveDataを更新するためのHandler
    private final Handler handler = new Handler(Looper.getMainLooper());

    // 実行中のスレッドを保持し、ViewModel破棄時に中断できるようにする
    private Thread producerThread;
    private Thread consumerThread;

    // LiveDataを外部に公開
    public LiveData<String> getResult() {
        return result;
    }

    /**
     * ログメッセージをLiveDataに安全に追記します。
     * 必ずメインスレッドで実行されます。
     */
    private void setMessage(String message) {
        // メインスレッドで実行されるようにHandlerを使用
        handler.post(() -> result.setValue(message + "\n"));
    }

    /**
     * ProducerとConsumerのスレッドを起動します。
     */
    public void loadData() {
        // 既に実行中の場合は何もしない
        if (producerThread != null && producerThread.isAlive()) return;

        // 処理開始時にログをクリア
        result.setValue("--- 処理開始 ---\n");

        // ProducerとConsumerをインスタンス化し、ログコールバックを渡す
        Producer producer = new Producer(this::setMessage);
        Consumer consumer = new Consumer(this::setMessage);

        producerThread = new Thread(producer, "Producer");
        consumerThread = new Thread(consumer, "Consumer");

        producerThread.start();
        consumerThread.start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // ViewModelが破棄されたとき、実行中のスレッドを安全に中断する
        if (producerThread != null && producerThread.isAlive()) {
            producerThread.interrupt();
        }
        if (consumerThread != null && consumerThread.isAlive()) {
            consumerThread.interrupt();
        }
        queue.clear();
    }

    // ログ出力処理を抽象化するための関数型インターフェース
    private interface LogCallback {
        void log(String message);
    }

    /**
     * Producerスレッドの実装
     */
    private static class Producer implements Runnable {
        private final LogCallback callback;

        Producer(LogCallback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                for (int i = 1; i <= 5; i++) {
                    // スレッドが中断されたかチェック
                    if (Thread.interrupted()) throw new InterruptedException();

                    String data = "Task-" + i;
                    callback.log("🟢 生産: " + data);
                    queue.put(data); // 満杯なら待機
                    Thread.sleep(500);
                }

                // 終了シグナルをキューに投入
                queue.put("END");
                callback.log("✅ 生産終了");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                callback.log("❌ 生産処理が中断されました。");
            }
        }
    }

    /**
     * Consumerスレッドの実装
     */
    private static class Consumer implements Runnable {
        private final LogCallback callback;

        Consumer(LogCallback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // スレッドが中断されたかチェック
                    if (Thread.interrupted()) throw new InterruptedException();

                    String data = queue.take(); // キューが空なら待機
                    if (data.equals("END")) break;

                    callback.log("🔵 消費: " + data);

                    // 消費処理のシミュレーション
                    Thread.sleep(1000);
                }
                callback.log("✅ 消費終了");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                callback.log("❌ 消費処理が中断されました。");
            }
        }
    }
}
