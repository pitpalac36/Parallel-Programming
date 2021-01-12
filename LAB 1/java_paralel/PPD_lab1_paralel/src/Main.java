import java.io.IOException;

public class Main {

    public static void generate_file(String[] args) {
        int rows = Integer.parseInt(args[1]);
        int columns = Integer.parseInt(args[2]);
        FileUtils.generate_file("date.txt", rows, columns);
    }

    public static void main(String[] args) throws IOException {

        if (args[0].equals("generate")){
            generate_file(args);
        }
        else {
                int rows = Integer.parseInt(args[0]);
                int columns = Integer.parseInt(args[1]);
                int kernel_width = Integer.parseInt(args[2]);
                int p = Integer.parseInt(args[3]);
                int[] numbers = FileUtils.readFromFile("date.txt", rows * columns);
                Matrix matrix = new Matrix(columns, rows, numbers);
                Matrix kernel;

                if (kernel_width == 3)
                    kernel = new Matrix(3, 3, new int[]{0, -1, 0, -1, 5, -1, 0, -1, 0});
                else
                    kernel = new Matrix(5,5, new int[]{1, 4, 6, 4, 1, 4, 16, 24, 16, 4, 6, 24, 36, 24, 6, 4, 16, 24, 16, 4, 1, 4, 6, 4, 1});

                long startTime = System.nanoTime();
                int[][] result = matrix.runConvolution(kernel, p);
                long endTime = System.nanoTime();

                FileUtils.writeToFile(result, result.length, result[0].length);

                System.out.println((double) (endTime - startTime) / 1E6);
        }
    }
}