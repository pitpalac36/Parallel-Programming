package client;
import common.Reservation;
import common.Ticket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Worker {
    private static final String host = "localhost";
    private static final int port = 4000;
    private Socket connection;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Random random = new Random();
    private List<Ticket> ticketsAvailable = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public void connect() throws IOException {
        try {
            connection = new Socket(host, port);
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            inputStream = new ObjectInputStream(connection.getInputStream());
            ticketsAvailable = Collections.unmodifiableList((ArrayList<Ticket>) inputStream.readObject());
            System.out.println("received " + ticketsAvailable.size() + " tickets available");
            sendSales();
        } catch (SocketException e) {
            System.out.println("server is shut down");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } finally {
            if (connection != null && connection.isConnected())
                connection.close();
        }
    }

    private void sendSales() {
        try {
            List<Integer> seats = new ArrayList<>();
            String targetShowId = ticketsAvailable.get(random.nextInt(ticketsAvailable.size())).getShowId();
            List<Ticket> targetTickets = ticketsAvailable.stream().filter(x -> x.getShowId().equals(targetShowId)).collect(Collectors.toList());
            int nrSeatsWanted = Math.min(random.nextInt(10) + 1, targetTickets.size());
            for (int i = 0; i < nrSeatsWanted; i++) {
                if (seats.size() == nrSeatsWanted)
                    break;
                int randomIndex = random.nextInt(targetTickets.size());
                int seat = targetTickets.get(randomIndex).getSeat();
                if (!seats.contains(seat)) {
                    seats.add(seat);
                }
            }
            if (ticketsAvailable.size() == 0) {
                System.out.println("No more tickets available!!");
                return;
            }
            Reservation reservation = new Reservation(targetShowId, seats);
            System.out.println("Sent reservation to server : " + reservation.toString());
            outputStream.writeObject(reservation);
            boolean ok = inputStream.readBoolean();
            String response = ok ? "received" : "failed";
            System.out.println(response + "\n");
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            connection.close();
        } catch (IllegalArgumentException e) {
            System.out.println("No more tickets available!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
