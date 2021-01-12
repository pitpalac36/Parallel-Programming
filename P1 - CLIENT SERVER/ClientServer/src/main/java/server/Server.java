package server;
import common.Reservation;
import common.Ticket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

public class Server {

    private int port;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Repository repo;
    private int noSecondsIdle;
    public static ObjectOutputStream stream;

    public Server(int port, int noThreads, Repository repo, int noSecondsIdle) {
        this.port = port;
        this.repo = repo;
        this.noSecondsIdle = noSecondsIdle;
        threadPool = Executors.newFixedThreadPool(noThreads);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server has started...");
            threadPool.execute(new Logger(repo));
            for (Ticket t : repo.remainingTickets) {
                System.out.println(t.toString());
            }
            while (true) {
                if (repo.remainingTickets.size() == 0)
                    break;
                System.out.println("Waiting for clients...");
                Socket client = serverSocket.accept();
                System.out.println("Client connected...");
                stream = new ObjectOutputStream(client.getOutputStream());
                stream.writeObject(repo.remainingTickets);
                stream.reset();
                System.out.println("Sent available tickets to client...");
                processRequest(client);
            }
            stream.flush();
            stream.close();
            threadPool.awaitTermination(noSecondsIdle, TimeUnit.SECONDS);
            serverSocket.close();
            System.out.println("Shutting down server...");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processRequest(Socket client) {
        Future<List<Ticket>> future = threadPool.submit(new MyCallable(client));
        Thread thread = new Thread(new Worker(future, client));
        thread.setDaemon(false);
        thread.start();
    }

    private class MyCallable implements Callable<List<Ticket>>{
        private ObjectInputStream inputStream;

        public MyCallable(Socket client) {
            try {
                inputStream = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public List<Ticket> call() throws Exception {
            Reservation reservation = (Reservation)inputStream.readObject();
            return repo.buyTicket(reservation.getShowId(), reservation.getSeats());
        }
    }
}
