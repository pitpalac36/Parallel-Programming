#include <iostream>
#include <fstream>
#include <string>
#include <cstdlib>
#include <time.h>
#include <ctime>
#include <ratio>
#include <chrono>
using std::ofstream;
using std::ifstream;
using std::string;
using std::cout;
using namespace std::chrono;

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

    int length1, length2, carry;  // lungime nr 1, lungime nr 2, carry
    int N, initMaxSize;   //   max { length1, length2 }

    ifstream input1(argv[1]);
    ifstream input2(argv[2]);
    input1 >> length1;
    input2 >> length2;
    if (length1 > length2)
        N = length1;
    else
        N = length2;
    initMaxSize = N;

    int* nr1 = new int[N + 1];
    int* nr2 = new int[N + 1];
    int* nr3 = new int[N + 1];

    for (int i = length1 - 1; i >= 0; i--) {
        input1 >> nr1[i];
    }
    for (int i = length2 - 1; i >= 0; i--) {
        input2 >> nr2[i];
    }
    input1.close();
    input2.close();

    if (length1 < N) {
        for (int i = length1; i < N; i++) {
            nr1[i] = 0;
        }
    }
    else { // pun oricum un 0 la final in caz ca rezultatul ocupa mai mult spatiu
        nr1[N] = 0;
    }

    if (length2 < N) {
        for (int i = length2; i < N; i++) {
            nr2[i] = 0;
        }
    }
    else {  // pun oricum un 0 la final in caz ca rezultatul ocupa mai mult spatiu
        nr2[N] = 0;
    }

    high_resolution_clock::time_point startTime = high_resolution_clock::now();
    carry = 0;

    for (int i = 0; i < N; i++) {
        nr3[i] = (nr1[i] + nr2[i] + carry) % 10;
        if (nr1[i] + nr2[i] + carry >= 10)
            carry = 1;
        else carry = 0;
    }

    if (carry != 0) {
        cout << "carry nu e 0\n";
        initMaxSize++;  // size initial + 1 (carry)
        while (nr3[N - 1] == 0 && N > initMaxSize)
            N--;
    }
    else {
        while (nr3[N - 1] == 0)
            N--;
    }

    if (carry != 0) {
        nr3[N] = 1;
        N++;
    }

    high_resolution_clock::time_point endTime = high_resolution_clock::now();
    duration<double, std::milli> time_span = endTime - startTime;

    writeToFileReversed("rezultat.txt", nr3, N);
    delete[] nr1;
    delete[] nr2;
    delete[] nr3;

    cout << time_span.count();

    return 0;
}