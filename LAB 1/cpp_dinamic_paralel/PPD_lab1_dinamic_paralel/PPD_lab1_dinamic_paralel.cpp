#include <iostream>
#include <string>
#include <random>
#include <fstream>
#include <stdlib.h>
#include "Matrix.h"
#include <vector>
#include <thread>
#include <functional>
#include <time.h>
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
using std::thread;
using std::vector;


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


void worker(Matrice& m, Matrice& kernel, int start, int end, int name, int** output) {
    int nrHorizontalShifts = m.width - (kernel.width - 1) + 1 + 1;
    int nrVerticalShifts = m.height - (kernel.height - 1) + 1 + 1;
    auto submatrix = new Matrice(kernel.width, kernel.height);

    int i = 0, j = 0;
    if (start != 0) {
        for (int c = 0; c < name * (end - start); c++) {
            if (j == m.width - 1) {
                j = 0;
                i++;
            }
            else j++;
        }
    }
    if (end == m.width * m.height) // trebuie sa o iau si pe ultima
        end++;
    //cout << "\nthread " << name << " merge de la linia " << i << " si coloana " << j << " " << end - start << " pasi pana la end = " << end << "\n";

    for (int chunk = start; chunk < end; chunk++) {

            if (i == 0 && j == 0) { // iau o matrice de (kernel.width - 1) x (kernel.width -1) si o bordez stanga + sus;
                submatrix->borderLeftAndUp(m);
            }
            else {
                if (i == 0 && j < nrHorizontalShifts - 1) {   // iau o matrice de (kernel.width) x (kernel.width - 1) si o bordez sus;
                    submatrix->borderUp(m, j - 1);
                }
                else {
                    if (i == 0 && j == nrHorizontalShifts - 1) {   // iau o matrice de (kernel.width - 1) x (kernel.width - 1) si o bordez dreapta + sus;
                        submatrix->borderRightAndUp(m);
                    }
                    else {
                        if (i > 0 && j == 0 && i < nrVerticalShifts - 1) {     // iau o matrice de (kernel.width - 1) x (kernel.width) si o bordez la stanga
                            submatrix->borderLeft(m, i - 1);
                        }
                        else {
                            if (i > 0 && i < nrVerticalShifts - 1 && j == nrHorizontalShifts - 1) {    // iau o matrice de (kernel.width - 1) x (kernel.width) si o bordez la dreapta
                                submatrix->borderRight(m, i - 1);
                            }
                            else {
                                if (i == nrVerticalShifts - 1 && j == 0) {  // iau o matrice de (kernel.width - 1) x (kernel.width -1) si o bordez stanga + jos;
                                    submatrix->borderLeftAndDown(m);
                                }
                                else {
                                    if (i == nrVerticalShifts - 1 && j > 0 && j < nrHorizontalShifts - 1) {     // iau o matrice de (kernel.width) x (kernel.width - 1) si o bordez jos;
                                        submatrix->borderDown(m, j - 1);
                                    }
                                    else {
                                        if (i == nrVerticalShifts - 1 && j == nrHorizontalShifts - 1) {
                                            submatrix->borderRightAndDown(m);
                                        }
                                        else submatrix->copy(m, i, j);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            output[i][j] = submatrix->process(kernel);
            if (j == nrHorizontalShifts - 1) {
                i++;
                j = 0;
            }
            else j++;
     
    }
}


void main(int argc, char* argv[]) {

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
        long p = strtol(argv[4], NULL, 10);
        Matrice* kernel;

        if (kWidth == 3) {
            kernel = new Matrice(3, 3);
            kernel->matrix = read_from_file("kernel3.txt", 3, 3);
        }
        else {
            kernel = new Matrice(5, 5);
            kernel->matrix = read_from_file("kernel5.txt", 5, 5);
        }

        vector<thread> vt;
        int start = 0, end;
        int nrHorizontalShifts = m->width - (kernel->width - 1) + 1 + 1;
        int nrVerticalShifts = m->height - (kernel->height - 1) + 1 + 1;

        int** output = new int* [nrVerticalShifts];
        for (int i = 0; i < nrVerticalShifts; ++i)
            output[i] = new int[nrHorizontalShifts];

        int chunk = (nrHorizontalShifts * nrVerticalShifts) / p;
        int rest = (nrHorizontalShifts * nrVerticalShifts) % p;

        clock_t startTime = clock();

        for (int i = 0; i < p; i++) {
            end = start + chunk;
            if (rest > 0) {
                end++;
                rest--;
            }
            vt.push_back(thread(worker, std::ref(*m), std::ref(*kernel), start, end, i, output));
            start = end;
        }

        for (int i = 0; i < p; i++) {
            if (vt[i].joinable())
                vt[i].join();
        }

        clock_t endTime = clock();

        write_to_file("rezultat.txt", nrVerticalShifts, nrHorizontalShifts, output);

        double time = ((double)(endTime - startTime)) / CLOCKS_PER_SEC;     // seconds

        cout << time;
    }
}
