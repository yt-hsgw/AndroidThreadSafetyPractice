import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicIntegerを使用してスレッドセーフなカウンタを実装する例
 */
public class AtomicExample {
    // AtomicInteger: 不可分操作を提供するクラス。
    private static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(AtomicExample::increment);
        Thread t2 = new Thread(AtomicExample::increment);

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("最終結果: " + count.get());
    }

    static void increment() {
        for (int i = 0; i < 10000; i++) {
            // インクリメント操作はスレッドセーフ
            count.incrementAndGet();
        }
    }
}
