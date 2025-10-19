public class SyncAsyncExample {

    public static void main(String[] args) {
        // System.out.println("=== 同期処理 ===");
        // doTaskSync();

        System.out.println("\n=== 非同期処理 ===");
        doTaskAsync();
    }

    // 同期処理（1→2→3の順に実行）
    static void doTaskSync() {
        task("A");
        task("B");
        task("C");
    }

    // 非同期処理（スレッドを利用して同時進行）
    static void doTaskAsync() {
        new Thread(() -> task("A")).start();
        new Thread(() -> task("B")).start();
        new Thread(() -> task("C")).start();
    }

    static void task(String name) {
        System.out.println(name + " 開始");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        System.out.println(name + " 終了");
    }
}