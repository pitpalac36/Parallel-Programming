package server;
import common.Ticket;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Future;

public class Worker implements Runnable {
    private Socket client;
    private DataOutputStream outputStream;
    private Future<List<Ticket>> future;

    public Worker(Future<List<Ticket>> future, Socket client) {
        this.future = future;
        this.client = client;
        try {
            outputStream = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            List<Ticket> response = future.get();
            boolean responseMessage = true;
            if (response == null) responseMessage = false;
            System.out.println(responseMessage);
            Server.stream.writeBoolean(responseMessage);
            Server.stream.reset();
            client.close();
        } catch (Exception e) {
            try {
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}