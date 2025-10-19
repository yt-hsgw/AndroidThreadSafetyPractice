import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Executorフレームワークの使用例
 * 
 * ExecutorService: スレッドプールを管理し、タスクの実行を効率化
 */
public class ExecutorExample {
    public static void main(String[] args) {
        // 固定サイズのスレッドプールを作成
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // ExecutorService executorSingleThread = Executors.newSingleThreadExecutor();

        // 複数のタスクをスレッドプールで実行
        for (int i = 1; i <= 5; i++) {
            // i を直接ラムダ内で使うと問題になるため、コピーしてキャプチャする。
            int taskId = i;
            // Runnable をキューに入れて、空いているスレッドがあればそこで実行する。
            // これにより、新しいスレッドを毎回作成するオーバーヘッドを削減できる。
            executor.submit(() -> {
                System.out.println("タスク" + taskId + " 実行中: " + Thread.currentThread().getName());
            });
        }
        // 新しいタスクの受付を停止し、既にキューにあるタスクは実行する。完全停止までブロックはしない（非同期）
        executor.shutdown();
        // 完全停止まで待機（最大60秒）。必要に応じて強制終了。
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
