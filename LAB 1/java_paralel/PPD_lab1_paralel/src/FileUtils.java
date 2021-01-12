import java.io.*;
import java.util.Random;

public class FileUtils {

    public static void generate_file(String fileName, int rows, int columns) {
        try {
            Random random  = new Random();
            FileWriter writer = new FileWriter(fileName);
            for (int i = 0; i < rows; i ++) {
                for (int j = 0; j < columns; j++) {
                    int x = random.nextInt(100);
                    writer.write(String.valueOf(x) + ' ');
                }
                writer.write('\n');
            }
            writer.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int[] readFromFile(String fileName, int size) throws FileNotFoundException {
        FileReader reader = new FileReader(new File(fileName));
        BufferedReader buffer = new BufferedReader(reader);
        int[] result = new int[size];
        int last = 0;
        try {
            String line = buffer.readLine();
            while (line != null) {
                String[] fields = line.split(" ");
                for (String each : fields) {
                    result[last] = Integer.parseInt(each);
                    last++;
                }
                line = buffer.readLine();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return  result;
    }


    public static void writeToFile(int[][] result, int rows, int columns) throws IOException {
        FileWriter writer = new FileWriter(new File("rezultat.txt"));
        try {
            for (int i = 0; i < rows; i++) {
                String line = "";
                for (int j = 0; j < columns; j++)
                    line += result[i][j] + " ";
                writer.write(line + "\n");
            }
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}