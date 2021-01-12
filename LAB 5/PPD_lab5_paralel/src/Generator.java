import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Generator {

    private static Generator generator = null;

    private Generator() {}

    public void generate(String filename, int numerOfElements, int maxExp) throws IOException {
        FileWriter writer = new FileWriter(filename);
        Random rand = new Random();
        String line;
        for (int i = 0; i < numerOfElements; i++) {
            int coef = rand.nextInt(10 + 10) - 10;
            if (coef == 0)
                coef++;
            int exp = rand.nextInt(maxExp);
            line = coef + "," + exp;
            writer.write(line + "\n");
        }
        writer.close();
    }

    public static Generator getInstance() {
        if (generator == null)
            return new Generator();
        return generator;
    }
}
