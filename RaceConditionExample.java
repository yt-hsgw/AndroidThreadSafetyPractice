public class RaceConditionExample {
    private static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(RaceConditionExample::increment);
        Thread t2 = new Thread(RaceConditionExample::increment);

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("最終結果: " + count);
    }

    static void increment() {
        for (int i = 0; i < 10000; i++) {
            count++;  // ← 競合が発生する
        }
    }
}
