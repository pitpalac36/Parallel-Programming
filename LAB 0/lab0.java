import java.io.*;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void generate_file(String fileName, int size, int min, int max) throws IOException {
        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
                System.out.println("Fisierul " + file.getName() + " creat cu succes");
                Random random  = new Random();
                FileWriter writer = new FileWriter(fileName);
                    for (int j = 0; j < size; j++) {
                        int x = random.nextInt(max - min) + min;
                        writer.write(String.valueOf(x) + '\n');
                    }
                writer.close();
            }
            else {
                System.out.println("Fisierul exista deja");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean verify_equal(String fileName1, String fileName2) throws IOException {
        FileReader reader1 = new FileReader(new File(fileName1));
        FileReader reader2 = new FileReader(new File(fileName2));
        BufferedReader buffer1 = new BufferedReader(reader1);
        BufferedReader buffer2 = new BufferedReader(reader2);
        try {
            String data1 = buffer1.readLine();
            String data2 = buffer2.readLine();
            while (data1 != null && data2 != null) {
                if (!data1.equals(data2))
                    return false;
                data1 = buffer1.readLine();
                data2 = buffer2.readLine();
                System.out.println(data1 + " " + data2);
            }
            if (data1 != data2) {
                return false;
            }
        }
        catch(Exception e){
            return false;
        }
        return true;
    }


    public static void main(String[] args) throws IOException {
        while (true){
            Scanner scanner = new Scanner(System.in);
            System.out.print("\n1. Genereaza fisier\n2. Verifica similaritatea a doua fisiere\n3. Iesire din meniu\nOptiune : ");
            try {
                int selection = scanner.nextInt();
                switch (selection) {
                    case 1 :
                        System.out.print("Nume fisier : ");
                        String fileName = scanner.next();
                        System.out.print("Dimensiune :");
                        int size = scanner.nextInt();
                        System.out.print("Minim :");
                        int min = scanner.nextInt();
                        System.out.print("Maxim :");
                        int max = scanner.nextInt();
                        Main.generate_file(fileName, size, min, max);
                        break;
                    case 2:
                        System.out.print("Nume fisier 1: ");
                        String fileName1 = scanner.next();
                        System.out.print("Nume fisier 2: ");
                        String fileName2 = scanner.next();
                        Main.verify_equal(fileName1, fileName2);
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Optiune invalida!");
                        break;
                }
            }
            catch (InputMismatchException e) {
                System.out.println("Optiune invalida!");
            }

        }
    }
}
