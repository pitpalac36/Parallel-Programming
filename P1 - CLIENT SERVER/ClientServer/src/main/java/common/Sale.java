package common;
import java.time.LocalDate;

public class Sale {
    private String showId;
    private LocalDate date;
    private int noSoldSeats;
    private float sum;

    public Sale(String showId, LocalDate date, int noSoldSeats, float sum) {
        this.showId = showId;
        this.date = date;
        this.noSoldSeats = noSoldSeats;
        this.sum = sum;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getNoSoldSeats() {
        return noSoldSeats;
    }

    public void setNoSoldSeats(int noSoldSeats) {
        this.noSoldSeats = noSoldSeats;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "showId='" + showId + '\'' +
                ", date=" + date +
                ", noSoldSeats=" + noSoldSeats +
                ", sum=" + sum +
                '}';
    }
}
