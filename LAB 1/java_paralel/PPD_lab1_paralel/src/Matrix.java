class Worker extends Thread {

    private Matrix m;
    private Matrix kernel;
    private int start;
    private int end;
    private int name;
    private int[][] result;

    public Worker(Matrix m, Matrix kernel, int start, int end, int i, int[][] result) {
        this.m = m;
        this.kernel = kernel;
        this.start = start;
        this.end = end;
        this.name = i;
        this.result = result;
    }

    @Override
    public void run() {
        int nrHorizontalShifts = m.getWidth() - (kernel.getWidth() - 1) + 1 + 1;
        int nrVerticalShifts = m.getHeight() - (kernel.getHeight() - 1) + 1 + 1;
        Matrix submatrix;
        submatrix = new Matrix(kernel.getWidth(), kernel.getHeight());

        int i = 0, j = 0;
        if (start != 0){
            for (int c = 0 ; c < name * (end - start); c++){
                if (j == m.getWidth() - 1) {
                    j = 0;
                    i++;
                } else j++;
            }
        }

        if(end == m.getWidth() * m.getHeight())
            end++;

        for (int chunk = start; chunk < end; chunk++) {

                if (i == 0 && j == 0)       // iau o matrice de (kernel.width - 1) x (kernel.width -1) si o bordez stanga + sus;
                    submatrix.borderLeftAndUp(m);
                else {
                    if (i == 0 && j < nrHorizontalShifts - 1)      // iau o matrice de (kernel.width) x (kernel.width - 1) si o bordez sus;
                        submatrix.borderUp(m, j - 1);
                    else {
                        if (i == 0 && j == nrHorizontalShifts - 1)      // iau o matrice de (kernel.width - 1) x (kernel.width - 1) si o bordez dreapta + sus;
                            submatrix.borderRightAndUp(m);
                        else {
                            if (i > 0 && j == 0 && i < nrVerticalShifts - 1)        // iau o matrice de (kernel.width - 1) x (kernel.width) si o bordez la stanga
                                submatrix.borderLeft(m, i - 1);
                            else {
                                if (i > 0 && i < nrVerticalShifts - 1 && j == nrHorizontalShifts - 1)       // iau o matrice de (kernel.width - 1) x (kernel.width) si o bordez la dreapta
                                    submatrix.borderRight(m, i - 1);
                                else {
                                    if (i == nrVerticalShifts - 1 && j == 0)     // iau o matrice de (kernel.width - 1) x (kernel.width -1) si o bordez stanga + jos;
                                        submatrix.borderLeftAndDown(m);
                                    else {
                                        if (i == nrVerticalShifts - 1 && j > 0 && j < nrHorizontalShifts - 1)      // iau o matrice de (kernel.width) x (kernel.width - 1) si o bordez jos;
                                            submatrix.borderDown(m, j - 1);
                                        else {
                                            if (i == nrVerticalShifts - 1 && j == nrHorizontalShifts - 1)
                                                submatrix.borderRightAndDown(m);
                                            else {
                                                submatrix.copy(m, i, j);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                result[i][j] = submatrix.process(kernel);
                if (j == nrHorizontalShifts -1) {
                    i ++;
                    j = 0;
                }
                else j ++;
            }
    }
}


public class Matrix {

    private int[][] matrix;
    private int width;
    private int height;

    Matrix(int width, int height) {
        this.width = width;
        this.height = height;
        this.matrix = new int[height][width];
    }

    Matrix(int width, int height, int[] numbers) {
        this.width = width;
        this.height = height;
        this.matrix = new int[height][width];
        int k = 0;
        for (int i = 0 ; i < height ; i++)
            for(int j = 0 ; j < width; j++) {
                matrix[i][j] = numbers[k];
                k++;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[][] runConvolution(Matrix kernel, int p) {
        int start = 0, end;
        int nrHorizontalShifts = width - (kernel.width - 1) + 1 + 1;
        int nrVerticalShifts = height - (kernel.height - 1) + 1 + 1;
        int[][] result = new int[nrVerticalShifts][nrHorizontalShifts];
        int chunk = (nrHorizontalShifts * nrVerticalShifts) / p;
        int rest = (nrHorizontalShifts * nrVerticalShifts) % p;

        Thread[] t = new Worker[p];
        for (int i = 0; i < p; i++) {
            end = start + chunk;
            if (rest > 0) {
                end++;
                rest--;
            }
            t[i] = new Worker(this, kernel, start, end, i, result);
            t[i].start();
            start = end;
        }
        for (int i = 0; i < p; i++) {
            try {
                t[i].join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    int process(Matrix kernel) {
        int result = 0;
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                result += matrix[i][j] * kernel.matrix[i][j];
        return result;
    }


    void copy(Matrix someMatrix, int i, int j) {
        i--;
        j--;
        for (int k = 0 ; k < height; k++)
            for (int l = 0; l < width; l++)
                matrix[k][l] = someMatrix.matrix[i + k][j + l];
    }


    void borderRightAndDown(Matrix someMatrix) {
        // copiere elemente din someMatrix
        for (int i = 0; i < height - 1; i++) {
            for (int j = 0; j < width - 1; j++) {
                matrix[i][j] = someMatrix.matrix[someMatrix.height - height + i + 1][someMatrix.width - width + j + 1];
            }
        }
        matrix[height - 1][width - 1] = someMatrix.matrix[someMatrix.height - 1][someMatrix.width - 1];    // coltul din dreapta jos
        for (int i = 0; i < width - 1; i++)                 // bordare jos
            matrix[height - 1][i] = matrix[height - 2][i];
        for (int i = 0; i < height - 1; i++)             // bordare dreapta
            matrix[i][width - 1] = matrix[i][width - 2];
    }


    void borderDown(Matrix someMatrix, int someMatrixColumn) {
        // copiere elemente din someMatrix
        for (int i = 0; i < height - 1; i++) {
            for (int j = 0; j < width; j++) {
                matrix[i][j] = someMatrix.matrix[someMatrix.height - height + i + 1][someMatrixColumn + j];
            }
        }
        for (int i = 0; i < width; i++)                 // bordare jos
            matrix[width - 1][i] = someMatrix.matrix[someMatrix.height - 1][someMatrixColumn + i];
    }


    void borderLeftAndDown(Matrix someMatrix) {
        // copiere elemente din someMatrix
        for (int i = 0; i < height - 1; i++)
            for (int j = 1; j < width; j++)
                matrix[i][j] = someMatrix.matrix[someMatrix.height - height + i + 1][j - 1];

        matrix[height - 1][0] = someMatrix.matrix[someMatrix.height - 1][0];    // coltul din stanga jos
        for (int i = 1; i < width; i++)                 // bordare jos
            matrix[height - 1][i] = someMatrix.matrix[someMatrix.height - 1][i - 1];
        for (int i = 0; i < height - 1; i++)                 // bordare stanga
            matrix[i][0] = someMatrix.matrix[someMatrix.height - height + i + 1][0];
    }


    public void borderLeftAndUp(Matrix someMatrix) {
        // copiere elemente din someMatrix
        for (int i = 1; i < height; i++) {
            int lineIndexInSomeMatrix = i - 1;
            for (int j = 1; j < width; j++) {
                int columnIndexInSomeMatrix = j - 1;
                matrix[i][j] = someMatrix.matrix[lineIndexInSomeMatrix][columnIndexInSomeMatrix];
            }
        }
        this.matrix[0][0] = someMatrix.matrix[0][0];    // coltul din stanga sus
        for (int i = 1; i < width; i++)                 // bordare sus
            matrix[0][i] = matrix[1][i];
        for (int i = 1; i < height; i++)                 // bordare stanga
            matrix[i][0] = matrix[i][1];
    }


    public void borderUp(Matrix someMatrix, int someMatrixColumn) {
        // copiere elemente din someMatrix
        for (int i = 1; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrix[i][j] = someMatrix.matrix[i - 1][someMatrixColumn + j];
            }
        }
        for (int i = 0; i < width; i++)                 // bordare sus
            matrix[0][i] = someMatrix.matrix[0][someMatrixColumn + i];
    }


    public void borderRightAndUp(Matrix someMatrix) {
        // copiere elemente din someMatrix
        for (int i = 1; i < height; i++) {
            for (int j = 0; j < width - 1; j++) {
                int columnIndexInSomeMatrix = someMatrix.width - width + j + 1;
                matrix[i][j] = someMatrix.matrix[i - 1][columnIndexInSomeMatrix];
            }
        }
        matrix[0][width - 1] = someMatrix.matrix[0][someMatrix.width - 1];    // coltul din stanga sus
        for (int i = 0; i < width - 1; i++)          // bordare sus
            matrix[0][i] = someMatrix.matrix[0][someMatrix.width - width + i + 1];
        for (int i = 1; i < height; i++)             // bordare dreapta
            matrix[i][width - 1] = someMatrix.matrix[i - 1][someMatrix.width - 1];
    }


    public void borderLeft(Matrix someMatrix, int someMatrixLine) {
        // copiere elemente din someMatrix
        for (int i = 0; i < height; i++) {
            for (int j = 1; j < width; j++) {
                matrix[i][j] = someMatrix.matrix[someMatrixLine + i][j - 1];
            }
        }
        for (int i = 0; i < height; i++)                 // bordare stanga
            matrix[i][0] = someMatrix.matrix[someMatrixLine + i][0];
    }


    public void borderRight(Matrix someMatrix, int someMatrixLine) {
        // copiere elemente din someMatrix
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width - 1; j++) {
                matrix[i][j] = someMatrix.matrix[someMatrixLine + i][someMatrix.width - width + j + 1];
            }
        }
        for (int i = 0; i < height; i++)     {
            // bordare dreapta
            matrix[i][width - 1] = someMatrix.matrix[someMatrixLine + i][someMatrix.width - 1];
        }
    }

}