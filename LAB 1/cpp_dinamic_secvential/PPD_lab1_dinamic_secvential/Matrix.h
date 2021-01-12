#include <iostream>
using std::cout;

class Matrice {

public:

    int width;
    int height;
    int** matrix;

    Matrice(int width, int height);

    Matrice(int width, int height, int** array);

    int** convolution(Matrice& kernel);

    int process(Matrice& kernel);

    void copy(Matrice& someMatrix, int i, int j);

    void borderRightAndDown(Matrice& someMatrix);

    void borderDown(Matrice& someMatrix, int someMatrixColumn);

    void borderLeftAndDown(Matrice& someMatrix);

    void borderLeftAndUp(Matrice& someMatrix);

    void borderUp(Matrice& someMatrix, int someMatrixColumn);

    void borderRightAndUp(Matrice& someMatrix);

    void borderLeft(Matrice& someMatrix, int someMatrixLine);

    void borderRight(Matrice& someMatrix, int someMatrixLine);

    ~Matrice();

};