#include <iostream>
#include <string>
#include <random>
#include <fstream>
#include <stdlib.h>
#include "Matrix.h"
#include <chrono>
#include <ctime>
#include <ratio>
using std::string;
using std::ifstream;
using std::ofstream;
using std::mt19937;
using std::uniform_int_distribution;
using std::cout;
using std::cin;
using std::exception;
using std::ostream;
using std::endl;


void generate_file(string filename, int rows, int columns) {
    ofstream out(filename);
    mt19937 mt{ std::random_device{}() };
    uniform_int_distribution<> dist(-100, 100);

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
            int rndNr = dist(mt);
            out << rndNr << " ";
        }
        out << "\n";
    }
    out.close();
}


void write_to_file(string filename, int rows, int columns, int** arr) {
    ofstream out(filename);
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
            out << arr[i][j] << " ";
        }
        out << "\n";
    }
    out.close();
}


int** read_from_file(string filename, int rows, int columns) {
    int** m = new int* [rows];
    for (int i = 0; i < rows; ++i)
        m[i] = new int[columns];

    ifstream in(filename);

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
            in >> m[i][j];
        }
    }
    in.close();
    return m;
}


int main(int argc, char* argv[]) {

    if (argv[1] == std::string("generate")) {
        long rows = strtol(argv[2], NULL, 10);
        long columns = strtol(argv[3], NULL, 10);
        generate_file("data.txt", rows, columns);
    }
    else {
        long rows = strtol(argv[1], NULL, 10);
        long columns = strtol(argv[2], NULL, 10);
        auto m = new Matrice(rows, columns);
        m->matrix = read_from_file("data.txt", rows, columns);

        long kWidth = strtol(argv[3], NULL, 10);
        Matrice* kernel;

        if (kWidth == 3) {
            kernel = new Matrice(3, 3);
            kernel->matrix = read_from_file("kernel3.txt", 3, 3);
        }
        else {
            kernel = new Matrice(5, 5);
            kernel->matrix = read_from_file("kernel5.txt", 5, 5);
        }

        /*cout << "\n\nMATRIX : \n";
        for (int i = 0; i < m->height; i++) {
            for (int j = 0; j < m->width; j++)
                cout << m->matrix[i][j] << " ";
            cout << "\n";
        }

        cout << "\n\nKERNEL\n";
        for (int i = 0; i < kWidth; i++) {
            for (int j = 0; j < kWidth; j++)
                cout << kernel->matrix[i][j] << " ";
            cout << "\n";
        }*/

        auto startTime = std::chrono::high_resolution_clock::now();


        int** convolutie = m->convolution(*kernel);

        auto endTime = std::chrono::high_resolution_clock::now();


        int nrHorizontalShifts = m->width - (kernel->width - 1) + 1 + 1;
        int nrVerticalShifts = m->height - (kernel->height - 1) + 1 + 1;

        write_to_file("rezultat.txt", nrVerticalShifts, nrHorizontalShifts, convolutie);

        std::chrono::duration<double, std::milli> time_span = endTime - startTime;

        cout << time_span.count();

    }
    return 0;
}
