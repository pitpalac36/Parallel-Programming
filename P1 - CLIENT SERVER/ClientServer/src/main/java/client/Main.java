package client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Worker clientWorker = new Worker();
        while (true) {
            try {
                clientWorker.connect();
                Thread.sleep(2000);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}