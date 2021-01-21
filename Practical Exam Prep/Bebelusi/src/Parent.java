public class Parent extends Thread {
    private Clock clock;
    private int interval;
    private int name;

    public Parent(Clock clock, int interval, int name) {
        this.clock = clock;
        this.interval = interval;
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            int time = clock.getTime();
            if (clock.isOver)
                return;
            if (time != 0 && (time % interval == 0)) {  // gotta feed the bastard
                System.out.println(TimeUtils.getFormatterTime() + " - parinte " + name + " - alimentat copil");
            }
            synchronized (clock) {
                try {
                    clock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
