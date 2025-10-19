import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Callableインターフェースの使用例
 * 
 * Runnable: 戻り値なし、例外処理なし、単純なタスク実行
 * Callable: 戻り値あり、例外処理あり、計算結果が必要なタスク実行
 */
public class CallableExample {
    public static void main(String[] args) throws Exception {
        // Callableを実装してFutureTaskに渡す
        Callable<Integer> callable = () -> {
            System.out.println("計算中...");
            Thread.sleep(1000);
            return 42;
        };

        // FutureTaskを使ってCallableを実行
        FutureTask<Integer> future = new FutureTask<>(callable);
        new Thread(future).start();

        // 結果を取得(FutureTaskを使うことで、非同期処理の「結果」を後で取得可能)
        System.out.println("結果: " + future.get()); // 結果を待って取得
    }
}
