package server;
import java.util.ArrayList;

public class Main {
    private static int NUMBER_OF_THREADS = 5;
    private static int port = 4000;

    // parametri repository : nr locuri, nr spectacole, pret bilet pt fiecare spectacol

    public static void main(String[] args) {
        ArrayList<Integer> prices = new ArrayList<>();
        prices.add(100);
        prices.add(200);
        prices.add(175);
        Repository repository = new Repository(100,3, prices);
        Server showServer = new Server(port,NUMBER_OF_THREADS,repository, 2);   // port, nrThreads, repo, time to sleep intre 2 verificari
        showServer.start();
    }
}