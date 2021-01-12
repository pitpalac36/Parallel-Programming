package common;

import java.io.Serializable;

public class Ticket implements Serializable {
    private String showId;
    private int seat;

    public Ticket(String showId, int seat) {
        this.showId = showId;
        this.seat = seat;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "showId='" + showId + '\'' +
                ", seat=" + seat +
                '}';
    }
}
