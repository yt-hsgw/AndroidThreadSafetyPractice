public class ThreadExample3 {
    public static void main(String[] args) throws InterruptedException {
        // スレッドの作成
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("タスク1完了");
            } catch (InterruptedException e) {
                System.out.println("割り込みされました");
            }
        });

        // 別のスレッドの作成
        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(500);
                System.out.println("タスク2完了");
            } catch (InterruptedException e) {
                System.out.println("割り込みされました");
            }
        });

        t1.start(); // スレッドt1の開始
        t2.start(); // スレッドt2の開始

        t1.join(); // スレッドt1が終わるまで待つ
        t2.join(); // スレッドt2が終わるまで待つ

        System.out.println("全タスク完了");
    }
}
