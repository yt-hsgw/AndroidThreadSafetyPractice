public class VolatileExample {
    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            while (running) { }
            System.out.println("スレッド停止");
        });

        t.start();
        Thread.sleep(1000);
        running = false; // ← volatileで他スレッドに即反映
        System.out.println("フラグをfalseに変更");
    }
}
