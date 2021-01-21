public class Clock extends Thread {
    private int secunde;
    public boolean isOver = false;

    public Clock() {
        secunde = 0;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
                secunde++;
                if (secunde == 60) {    // gata simularea
                    synchronized (this) {
                        isOver = true;
                        notifyAll();
                    }
                    return;
                }
                synchronized (this) {
                    notifyAll();    // notific parintii ca a trecut inca o secunda
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Integer getTime() {
        return secunde;
    }
}
