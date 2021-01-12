import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        if (args[0].equals("generate")) {
            Generator generator = Generator.getInstance();
            String filename = args[1];
            int numerOfElements = Integer.parseInt(args[2]);
            int maxExp = Integer.parseInt(args[3]);
            generator.generate(filename, numerOfElements, maxExp);
        }
        else {
            long startTime = System.nanoTime();
            LinkedList list = new LinkedList(args);
            long endTime = System.nanoTime();
            list.writeToFile("rezultat.txt");
            System.out.println((double)(endTime - startTime) / 1E6);    // ms
        }
    }
}