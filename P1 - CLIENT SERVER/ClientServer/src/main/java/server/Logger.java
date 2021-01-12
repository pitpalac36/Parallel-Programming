package server;

public class Logger implements Runnable {
    private Repository repo;

    public Logger(Repository repo) {
        this.repo = repo;
    }
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(20000);
                repo.verify();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}