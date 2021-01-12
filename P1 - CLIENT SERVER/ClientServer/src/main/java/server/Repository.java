package server;
import common.Sale;
import common.Show;
import common.Ticket;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Repository {

    private int sum = 0;
    private List<Sale> sales = new ArrayList<>();
    public List<Ticket> remainingTickets = new ArrayList<>();
    private List<Ticket> soldTickets = new ArrayList<>();
    private List<Show> shows = new ArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static LocalDate lastDate = LocalDate.parse("2020-01-01", formatter);
    private static String salesFile = "sales.txt";
    private static String showsFile = "shows.txt";
    private static String ticketsFile = "tickets.txt";

    // parametri repository : nr locuri, nr spectacole, pret bilet pt fiecare spectacol, time to sleep intre 2 verificari
    public Repository (int nrSeats, int noShows, ArrayList<Integer> prices) {
        generateShows(noShows, prices);
        generateTickets(nrSeats);
    }

    private void generateShows(int noShows, ArrayList<Integer> prices) {
        Random r = new Random();
        try {
            for (int i = 0; i < noShows; ++i) {
                String uniqueID = UUID.randomUUID().toString();
                int price = prices.get(i);
                Show show = new Show(uniqueID, lastDate.plusDays(1), "Show" + uniqueID, price, 0);
                shows.add(show);
                writeToFile(show, showsFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateTickets(int noSeats) {
        shows.forEach(each -> {
            for (int i = 0; i < noSeats; i++) {
                Ticket ticket = new Ticket(each.getId(), i);
                remainingTickets.add(ticket);
                try {
                    writeToFile(ticket, ticketsFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public synchronized List<Ticket> buyTicket(String showId, List<Integer> seats) throws IOException {
        int noSeats = seats.size();
        List<Ticket> ticketsAvailableForShow = remainingTickets
                                                .stream()
                                                .filter(each -> each.getShowId().equals(showId))
                                                .collect(Collectors.toList());
        if (ticketsAvailableForShow.size() < noSeats)   // nu am destule locuri
            return null;
        // am destule locuri
        List<Ticket> forClient = new ArrayList<>();
        for (int each : seats) {
            // verific daca sunt disponibile toate
            Ticket t = ticketsAvailableForShow.stream().filter(x -> x.getSeat() == each).findFirst().orElse(null);
            if (t == null)
                return null;
        }
        for (int each : seats) {
            // sunt disponibile; actualizez remainingTickets si soldTickets
            Ticket t = ticketsAvailableForShow.stream().filter(x -> x.getSeat() == each).findFirst().orElse(null);
            remainingTickets.remove(t);
            soldTickets.add(t);
            forClient.add(t);
        }
        // adaug o vanzare
        Show show = shows.stream().filter(each -> each.getId().equals(showId)).findFirst().get();
        Sale sale = new Sale(showId, show.getDate(), noSeats, show.getPrice() * noSeats);
        sales.add(sale);
        // adaug vanzarea si in fisier
        writeToFile(sale, salesFile);
        // actualizez soldul
        sum += show.getPrice() * noSeats;
        return forClient;
    }

    // data, ora, sold_per spectacol, lista vanzarilor per spectacol, ‘corect/incorect’
    /*
        data, ora
        spectacol 1
        sale1
        sale2
        ...
        sold spectacol 1
     */
    void verify() {
        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter("logs.txt", true)))) {
            printWriter.println(LocalDateTime.now());
            int total_sum = 0;
            for (Show each : shows) {
                int sold_per_show = 0;
                printWriter.println(each.getTitle());
                for (Sale x : sales) {
                    if (x.getShowId().equals(each.getId())) {
                        printWriter.println(x.toString());
                        sold_per_show += x.getSum();
                    }
                }
                printWriter.println(sold_per_show);
                printWriter.println();
                total_sum += sold_per_show;
            }
            printWriter.println("SOLD TOTAL SISTEM : " + sum + "  SOLD TOTAL CALCULAT : " + total_sum);
            if (sum == total_sum)
                printWriter.println("CORECT\n");
            else printWriter.println("INCORECT\n");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void writeToFile(Object o, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
        writer.write(o.toString() + "\n");
        writer.close();
    }
}
