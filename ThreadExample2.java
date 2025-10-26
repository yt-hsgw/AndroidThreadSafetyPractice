public class ThreadExample2 {
    public static void main(String[] args) {
        Runnable task = () -> {
            System.out.println("Runnable実行中: " + Thread.currentThread().getName());
        };

        Thread thread = new Thread(task);
        thread.start();

        System.out.println("メインスレッド: " + Thread.currentThread().getName());
    }
}
