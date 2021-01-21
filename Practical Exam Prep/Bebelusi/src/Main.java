import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        int[] delta;
        System.out.print("Nr parinti : ");
        int n = scanner.nextInt();
        delta = new int[n];
        System.out.println("Introduceti pentru fiecare parinte intervalul de timp la care trebuie sa isi alimenteze copilul!");
        for (int i = 0; i < n; i++) {
            System.out.print("parinte " + i + " : ");
            delta[i] = scanner.nextInt();
        }

        Thread[] t = new Thread[n];
        Clock clock = new Clock();

        clock.start();

        for (int i = 0; i < n; i++) {
            t[i] = new Parent(clock, delta[i], i);
            t[i].start();
        }

        for (int i = 0; i < n; i++) {
            t[i].join();
        }

        clock.join();
    }
}

// fara scriere in fisier (timelapse-urile sunt mai evidente in consola)