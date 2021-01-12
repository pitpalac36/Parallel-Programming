#include <iostream>
#include <mpi.h>
#include <fstream>
#include <string>
#include <cstdlib>
using std::ofstream;
using std::ifstream;
using std::string;
using std::cout;

/*
   primeste ca parametri numele fisierului, pointer catre un vector respectiv lungimea vectorului si scrie in fisier elementele vectorului
   in ordine inversa
*/
void writeToFileReversed(string fileName, int* number, int& length) {
    ofstream out(fileName);
    for (int i = length - 1; i >= 0; i--)
        out << number[i];
    out.close();
}


/*
    genereaza un fisier ce contine lungimea data si un numar random de aceasta lungime
*/
void generateNumber(string fileName, int length) {
    ofstream out(fileName);
    out << length << "\n";
    for (int i = 0; i < length; i++) {
        out << rand() % 10 << "\n";
    }
    out.close();
}


int main(int argc, char* argv[])
{
    if (argv[1] == string("generate")) {
        long length = strtol(argv[2], NULL, 10);    // lungime nr de generat
        string fileName = argv[3];   // nume fisier
        generateNumber(fileName, (int)length);
        return 0;
    }

    int p, rank, length1, length2, chunk;  // nr procese, id proces curent, lungime nr 1, lungime nr 2, lungime chunk
    double startTime, endTime;
    int* nr1 = new int;
    int* nr2 = new int;
    int* nr3 = new int;
    int N, initMaxSize;   //   max { length1, length2 }
    MPI_Status status;
    
    MPI_Init(NULL, NULL);
    MPI_Comm_size(MPI_COMM_WORLD, &p);  // nr de procese
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);   // rangul procesului curent

    if (rank == 0) {
        ifstream input1(argv[1]);
        ifstream input2(argv[2]);
        input1 >> length1;
        input2 >> length2;
        if (length1 > length2)
            N = length1;
        else 
            N = length2;
        initMaxSize = N;
        while (N % p != 0) {   // pt a distribui in mod egal
            N++;
        }

        nr1 = new int[N + 1];   // spatiul pt primul numar
        nr2 = new int[N + 1];   // spatiu pt al doilea numar
        nr3 = new int[N + 1];   // spatiu pt rezultat

        for (int i = length1 - 1; i >= 0; i--) {
            input1 >> nr1[i];
        }
        for (int i = length2 - 1; i >= 0; i--) {
            input2 >> nr2[i];
        }
        input1.close();
        input2.close();

        if (length1 < N) {
            for (int i = length1; i < N; i++) {     // completez cu 0 a.i. cele doua numere sa aiba aceeasi lungime
                nr1[i] = 0;
            }
        }
        else { // pun oricum un 0 la final in caz ca rezultatul ocupa mai mult spatiu
            nr1[N] = 0;
        }

        if (length2 < N) {
            for (int i = length2; i < N; i++) {     // completez cu 0 a.i. cele doua numere sa aiba aceeasi lungime
                nr2[i] = 0;
            }
        }
        else {  // pun oricum un 0 la final in caz ca rezultatul ocupa mai mult spatiu
            nr2[N] = 0;
        }

        chunk = N / p;
        startTime = MPI_Wtime();
    }

    MPI_Bcast(&chunk, 1, MPI_INT, 0, MPI_COMM_WORLD);   // trimit din procesul 0/primesc in celelalte procese dimensiunea chunkului
    int* a = new int[chunk];
    int* b = new int[chunk];
    MPI_Scatter(nr1, chunk, MPI_INT, a, chunk, MPI_INT, 0, MPI_COMM_WORLD);     // impart primul numar
    MPI_Scatter(nr2, chunk, MPI_INT, b, chunk, MPI_INT, 0, MPI_COMM_WORLD);     // impart al doilea numar
    int* c = new int[chunk];
    int carry = 0;

    if (rank > 0) {
        MPI_Recv(&carry, 1, MPI_INT, rank - 1, 1, MPI_COMM_WORLD, &status);     // primesc carry de la procesul anterior
    }
    for (int i = 0; i < chunk; i++) {      // calculul efectiv al fiecarei cifre din chunkul respectiv (si actualizarea carry-ului)
        c[i] = (a[i] + b[i] + carry) % 10;
        if (a[i] + b[i] + carry >= 10)
            carry = 1;
        else carry = 0;
    }

    if (rank < p - 1) {
        MPI_Send(&carry, 1, MPI_INT, rank + 1, 1, MPI_COMM_WORLD);  // trimit procesului urmator
    }
    if (rank == p - 1) {
        MPI_Send(&carry, 1, MPI_INT, 0, 2, MPI_COMM_WORLD);    // trimit procesului 0 carry-ul
    }
    if (rank == 0) {
        MPI_Recv(&carry, 1, MPI_INT, p - 1, 2, MPI_COMM_WORLD, &status);    // primesc carry de la ultimul proces
    }
    MPI_Gather(c, chunk, MPI_INT, nr3, chunk, MPI_INT, 0, MPI_COMM_WORLD);  // "adun"/culeg chunkurile

    delete[] a;     // nu mai am nevoie de array-urile dinamice locale fiecarui proces
    delete[] b;
    delete[] c;

    if (rank == 0) {
        delete[] nr1;       // nu mai am nevoie de array-urile dinamice pt numar 1 si numar 2
        delete[] nr2;

        if (carry != 0) {
            initMaxSize++;  // size initial + 1 (carry)
            while (nr3[N - 1] == 0 && N > initMaxSize)  // elimin zerourile extra
                N--;
        }
        else {
            while (nr3[N - 1] == 0)     // elimin zerourile extra
                N--;
        }

        if (carry != 0) {
            nr3[N] = 1;     // nr isi incrementeaza dimensiunea si cifra cea mai semnificativa devine 1
            N++;
        }
        endTime = MPI_Wtime();
        writeToFileReversed("rezultat.txt", nr3, N);
        cout << endTime - startTime;    // secunde
        delete[] nr3;
    }

    MPI_Finalize();
    return 0;
}