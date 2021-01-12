package common;
import java.io.Serializable;
import java.util.List;

public class Reservation implements Serializable {
    private String showId;
    private List<Integer> seats;

    public Reservation(String showId, List<Integer> seats) {
        this.showId = showId;
        this.seats = seats;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public List<Integer> getSeats() {
        return seats;
    }

    public void setSeats(List<Integer> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "showId='" + showId + '\'' +
                ", seats=" + seats +
                '}';
    }
}
