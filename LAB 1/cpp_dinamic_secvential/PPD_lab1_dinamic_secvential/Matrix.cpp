#include "Matrix.h"

Matrice::Matrice(int w, int h, int** arr) {
    width = w;
    height = h;

    matrix = new int* [height];
    for (int i = 0; i < height; ++i)
        matrix[i] = new int[width];

    for (int i = 0; i < height; i++)
        for (int j = 0; j < width; j++)
            matrix[i][j] = arr[i][j];
}

Matrice::Matrice(int w, int h) {
    width = w;
    height = h;
    matrix = new int* [height];
    for (int i = 0; i < height; ++i)
        matrix[i] = new int[width];
}

int** Matrice::convolution(Matrice& kernel) {
    int nrHorizontalShifts = width - (kernel.width - 1) + 1 + 1;
    int nrVerticalShifts = height - (kernel.height - 1) + 1 + 1;
    auto submatrix = new Matrice(kernel.width, kernel.height);

    int** output = new int* [nrVerticalShifts];
    for (int i = 0; i < nrVerticalShifts; ++i)
        output[i] = new int[nrHorizontalShifts];

    for (int i = 0; i < nrVerticalShifts; i++) {
        for (int j = 0; j < nrHorizontalShifts; j++) {
            //cout << "\n" << "j=" << j << "\n";
            if (i == 0 && j == 0) { // iau o matrice de (kernel.width - 1) x (kernel.width -1) si o bordez stanga + sus;
                submatrix->borderLeftAndUp(*this);
                //cout << "left and up " << i << " " << j;
            }
            else {
                if (i == 0 && j < nrHorizontalShifts - 1) {   // iau o matrice de (kernel.width) x (kernel.width - 1) si o bordez sus;
                    //cout << "up " << i << " " << j << "\n";
                    //cout << "this is " << this->matrix[0][0];
                    submatrix->borderUp(*this, j - 1);
                    //cout << "am iesit din borderUp cu i si j " << i << " " << j;
                }
                else {
                    if (i == 0 && j == nrHorizontalShifts - 1) {   // iau o matrice de (kernel.width - 1) x (kernel.width - 1) si o bordez dreapta + sus;
                        submatrix->borderRightAndUp(*this);
                        //cout << "right and up " << i << " " << j;
                    }
                    else {
                        if (i > 0 && j == 0 && i < nrVerticalShifts - 1) {     // iau o matrice de (kernel.width - 1) x (kernel.width) si o bordez la stanga
                            submatrix->borderLeft(*this, i - 1);
                            //cout << "left " << i << " " << j;
                        }
                        else {
                            if (i > 0 && i < nrVerticalShifts - 1 && j == nrHorizontalShifts - 1) {    // iau o matrice de (kernel.width - 1) x (kernel.width) si o bordez la dreapta
                                submatrix->borderRight(*this, i - 1);
                                //cout << "right " << i << " " << j;
                            }
                            else {
                                if (i == nrVerticalShifts - 1 && j == 0) {  // iau o matrice de (kernel.width - 1) x (kernel.width -1) si o bordez stanga + jos;
                                    submatrix->borderLeftAndDown(*this);
                                    //cout << "left and down " << i << " " << j;
                                }
                                else {
                                    if (i == nrVerticalShifts - 1 && j > 0 && j < nrHorizontalShifts - 1) {     // iau o matrice de (kernel.width) x (kernel.width - 1) si o bordez jos;
                                        submatrix->borderDown(*this, j - 1);
                                        //cout << "down " << i << " " << j;
                                    }
                                    else {
                                        if (i == nrVerticalShifts - 1 && j == nrHorizontalShifts - 1) {
                                            submatrix->borderRightAndDown(*this);
                                            //cout << "right and down " << i << " " << j;
                                        }
                                        else
                                            submatrix->copy(*this, i, j);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            output[i][j] = submatrix->process(kernel);
        }
    }
    return output;
}

int Matrice::process(Matrice& kernel) {
    int result = 0;
    for (int i = 0; i < height; i++)
        for (int j = 0; j < width; j++)
            result += matrix[i][j] * kernel.matrix[i][j];
    return result;
}


void Matrice::copy(Matrice& someMatrix, int i, int j) {
    i--;
    j--;
    for (int k = 0; k < height; k++)
        for (int l = 0; l < width; l++)
            matrix[k][l] = someMatrix.matrix[i + k][j + l];
}


void Matrice::borderRightAndDown(Matrice& someMatrix) {
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


void Matrice::borderDown(Matrice& someMatrix, int someMatrixColumn) {
    // copiere elemente din someMatrix
    for (int i = 0; i < height - 1; i++) {
        for (int j = 0; j < width; j++) {
            matrix[i][j] = someMatrix.matrix[someMatrix.height - height + i + 1][someMatrixColumn + j];
        }
    }
    for (int i = 0; i < width; i++)                 // bordare jos
        matrix[width - 1][i] = someMatrix.matrix[someMatrix.height - 1][someMatrixColumn + i];
}


void Matrice::borderLeftAndDown(Matrice& someMatrix) {
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


void Matrice::borderLeftAndUp(Matrice& someMatrix) {

    // copiere elemente din someMatrix
    for (int i = 1; i < height; i++) {
        int lineIndexInSomeMatrix = i - 1;
        for (int j = 1; j < width; j++) {
            int columnIndexInSomeMatrix = j - 1;
            matrix[i][j] = someMatrix.matrix[lineIndexInSomeMatrix][columnIndexInSomeMatrix];
        }
    }
    this->matrix[0][0] = someMatrix.matrix[0][0];    // coltul din stanga sus
    for (int i = 1; i < width; i++)                 // bordare sus
        matrix[0][i] = matrix[1][i];
    for (int i = 1; i < height; i++)                 // bordare stanga
        matrix[i][0] = matrix[i][1];
}


void Matrice::borderUp(Matrice& someMatrix, int someMatrixColumn) {

    // copiere elemente din someMatrix
    for (int i = 1; i < height; i++) {
        for (int j = 0; j < width; j++) {
            this->matrix[i][j] = someMatrix.matrix[i - 1][someMatrixColumn + j];
        }
    }
    for (int i = 0; i < width; i++)                 // bordare sus
        this->matrix[0][i] = someMatrix.matrix[0][someMatrixColumn + i];
}


void Matrice::borderRightAndUp(Matrice& someMatrix) {
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


void Matrice::borderLeft(Matrice& someMatrix, int someMatrixLine) {
    // copiere elemente din someMatrix
    for (int i = 0; i < height; i++) {
        for (int j = 1; j < width; j++) {
            matrix[i][j] = someMatrix.matrix[someMatrixLine + i][j - 1];
        }
    }
    for (int i = 0; i < height; i++)                 // bordare stanga
        matrix[i][0] = someMatrix.matrix[someMatrixLine + i][0];
}


void Matrice::borderRight(Matrice& someMatrix, int someMatrixLine) {
    // copiere elemente din someMatrix
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width - 1; j++) {
            matrix[i][j] = someMatrix.matrix[someMatrixLine + i][someMatrix.width - width + j + 1];
        }
    }
    for (int i = 0; i < height; i++) {
        // bordare dreapta
        matrix[i][width - 1] = someMatrix.matrix[someMatrixLine + i][someMatrix.width - 1];
    }
}

Matrice::~Matrice()
{
    for (int i = 0; i < height; ++i) {
        delete[] matrix[i];
    }
    delete[] matrix;
}
