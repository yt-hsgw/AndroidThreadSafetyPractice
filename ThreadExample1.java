public class ThreadExample1 {
        public static void main(String[] args) {
        MyThread thread = new MyThread();
        thread.start();  // run()ではなくstart()を呼ぶ！
        System.out.println("メインスレッド: " + Thread.currentThread().getName());
    }
}
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("別スレッドで実行中: " + Thread.currentThread().getName());
    }
}