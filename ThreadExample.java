public class ThreadExample {
    public static void main(String[] args) {
        // 方法① Threadを継承して使用
        new MyThread().start();  // 新しいスレッドを開始
        // 方法② Runnableを実装して使用
        new Thread(new MyRunnable()).start(); // Runnableを使って起動
    }
}

// 方法① Threadを継承してrun()をオーバーライド
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread実行中: " + Thread.currentThread().getName());
    }
}

// 方法② Runnableを実装してThreadに渡す
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable実行中: " + Thread.currentThread().getName());
    }
}