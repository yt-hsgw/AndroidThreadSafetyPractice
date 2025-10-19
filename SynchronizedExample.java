public class SynchronizedExample {
    private static int count = 0;
    private static final int LOOP = 10000;
    private static final Object locked = new Object(); 

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== 同期ありの場合 ===");
        runTest(true);

        // System.out.println("\n=== 同期なしの場合 ===");
        // runTest(false);
    }

    /**
     * synchronizedの有無でカウントアップを実行する関数
     * 
     * @param useSync
     * @throws InterruptedException
     */
    private static void runTest(boolean useSync) throws InterruptedException {
        count = 0;

        Thread t1 = new Thread(() -> increment(useSync, "スレッド1"));
        Thread t2 = new Thread(() -> increment(useSync, "スレッド2"));

        long start = System.currentTimeMillis();

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        long end = System.currentTimeMillis();
        System.out.println("最終結果: " + count + "（実行時間: " + (end - start) + "ms）");
    }

    /**
     * カウントアップを行う関数
     * 
     * @param useSync
     * @param name
     */
    private static void increment(boolean useSync, String name) {
        // synchronizedの有無で処理を分岐
        if (useSync) {
            synchronized (locked) {
                for (int i = 0; i < LOOP; i++) {
                    count++;
                    if (i % 3000 == 0) {
                        System.out.println(name + " → count=" + count);
                        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
                    }
                }
            }
        } else {
            for (int i = 0; i < LOOP; i++) {
                count++;
                if (i % 3000 == 0) {
                    System.out.println(name + " → count=" + count);
                    try { Thread.sleep(10); } catch (InterruptedException ignored) {}
                }
            }
        }
    }
}
