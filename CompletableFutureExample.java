import java.util.concurrent.CompletableFuture;

/**
 * CompletableFutureの基本的な使用例を示すクラス。
 * 非同期計算を行い、その結果を加工して最終的に出力します。
 * 
 * CompletableFuture<T> は 非同期処理の結果（futureな値）を保持するクラス。
 */
public class CompletableFutureExample {
    public static void main(String[] args) {
        // 戻り値を持つ非同期タスクを起動する。引数のラムダは別スレッドで実行される。
        // defaultのForkJoinPool.commonPool()が使われる。（Javaが持つグローバル共有スレッドプール）
       CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("非同期計算中...");
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            return 10;
        }).thenApply(result -> {
            return result * 2;
        }) .exceptionally(ex -> {
            System.out.println("例外発生: " + ex.getMessage());
            return 0;
        }).thenAccept(result -> System.out.println("最終結果: " + result));

        System.out.println("メインスレッドは他の処理を続行中...");
        future.join(); // メインスレッドが完了を待機
        System.out.println("全ての処理が完了しました。");
    }
}
