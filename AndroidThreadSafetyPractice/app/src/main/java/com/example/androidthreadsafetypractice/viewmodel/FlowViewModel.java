package com.example.androidthreadsafetypractice.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * Java 9+ Flow API (Reactive Streams) を使用してデータフローを実装するViewModel。
 * Publisherがデータを生成し、Subscriberが受け取り、LiveData経由でUIを更新します。
 * (AndroidではKotlin FlowやRxJavaが主流ですが、ここでは標準APIの概念を示します。)
 */
public class FlowViewModel extends ViewModel {
    // UIへのログ通知に使用するLiveData
    private final MutableLiveData<String> result = new MutableLiveData<>("--- Reactive Flow 処理待機中 ---\n");

    // LiveDataを外部に公開
    public LiveData<String> getResult() {
        return result;
    }

    // UIスレッドでの更新を保証するためのHandler
    private final Handler handler = new Handler(Looper.getMainLooper());

    // データ生成とFlow処理用のExecutorService
    private final ExecutorService flowExecutor = Executors.newSingleThreadExecutor();

    private SubmissionPublisher<String> publisher;

    /**
     * LiveDataにログメッセージを安全に追記します。
     */
    private void setMessage(String message) {
        // LiveDataの更新は常にメインスレッドで行う
        handler.post(() -> result.setValue(message + "\n"));
    }

    /**
     * Reactive Flowを開始し、PublisherとSubscriberを接続します。
     */
    public void loadData() {
        setMessage("--- Reactive Flow (Publisher/Subscriber) 処理開始 ---\n");

        // 1. Publisherの作成
        publisher = new SubmissionPublisher<>();

        // 2. Subscriberの作成と接続
        Flow.Subscriber<String> subscriber = new SimpleViewModelSubscriber(this::setMessage, handler);
        publisher.subscribe(subscriber);

        // 3. データ生成タスク（Producer）をExecutorServiceで開始
        flowExecutor.execute(new DataProducer(publisher));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (publisher != null) {
            // ViewModel破棄時にPublisherをクローズし、SubscriberにonCompleteを通知
            publisher.close();
        }
        flowExecutor.shutdownNow();
    }

    /**
     * データ生成タスク (Publisherにデータをsubmitする Runnable)
     */
    private class DataProducer implements Runnable {
        private final SubmissionPublisher<String> publisher;

        DataProducer(SubmissionPublisher<String> publisher) {
            this.publisher = publisher;
        }

        @Override
        public void run() {
            try {
                for (int i = 1; i <= 5; i++) {
                    if (Thread.interrupted()) break;
                    String data = "Event " + i;
                    setMessage("🟢 Publisher: データを生成 (" + data + ")");

                    // Subscriberにデータを送信
                    publisher.submit(data);
                    Thread.sleep(1000); // 1秒間隔でデータを生成
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (!Thread.interrupted()) {
                    // 正常終了の場合のみclose (onCompleteを通知)
                    publisher.close();
                }
            }
        }
    }

    /**
     * Flow.Subscriberの実装: ViewModel内でデータを処理し、UIを更新する。
     */
    private static class SimpleViewModelSubscriber implements Flow.Subscriber<String> {
        private Flow.Subscription subscription;
        private final LogCallback callback;
        private final Handler mainHandler; // UI更新用

        SimpleViewModelSubscriber(LogCallback callback, Handler mainHandler) {
            this.callback = callback;
            this.mainHandler = mainHandler;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            mainHandler.post(() -> {
                callback.log("🔵 Subscriber: 購読開始。バックプレッシャーリクエストを行います。");
            });
            // 最初の要素をリクエスト
            subscription.request(1);
        }

        @Override
        public void onNext(String item) {
            // 受信したデータをUIスレッドで処理
            mainHandler.post(() -> {
                callback.log("📨 Subscriber: データを受信: " + item);
            });

            // 処理が完了したら次の要素をリクエスト (バックプレッシャー)
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {
            mainHandler.post(() -> {
                callback.log("❌ Subscriber: エラー発生: " + throwable.getMessage());
            });
        }

        @Override
        public void onComplete() {
            mainHandler.post(() -> {
                callback.log("✅ Subscriber: ストリーム完了");
            });
        }
    }

    // ログ出力処理を抽象化するための関数型インターフェース
    private interface LogCallback {
        void log(String message);
    }
}
