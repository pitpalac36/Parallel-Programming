package common;
import java.time.LocalDate;

public class Show {
    private String id;
    private LocalDate date;
    private String title;
    private float price;
    private int sum;  // sold

    public Show(String id, LocalDate date, String title, float price, int sum) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.price = price;
        this.sum = sum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "Show{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", sum=" + sum +
                '}';
    }
}
