#include <iostream>
#include <vector>
#include <fstream>
#include <string>
#include <chrono>
#include <mpi.h>
using std::ifstream;
using std::ofstream;
using std::cout;
using std::endl;
using std::string;
using std::vector;

/*
   primeste ca parametri numele fisierului si referinta catre un vector si scrie in fisier elementele vectorului
   in ordine inversa dupa ce elimina zerourile nenecesare
*/
void writeToFileReversed(string filename, vector<int>& vector) {
    ofstream out(filename);
    int i = vector.size() - 1;
    while (vector[i] == 0)
        i--;
    for (int j = i; j >= 0; j--) {
        out << vector[j];
    }
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

    string line;
    int length1, length2, N;  // lungimile celor 3 numere
    int carry, rank, p, chunk;
    MPI_Status status;
    double startTime, endTime;

    vector<int> c;

    MPI_Init(NULL, NULL);
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    if (rank == 0) {

        ifstream input1(argv[1]);
        ifstream input2(argv[2]);
        input1 >> length1;      // lungimile numerelor
        input2 >> length2;

        if (length1 >= length2)
            N = length1;
        else N = length2;

        while (N % (p - 1) != 0) {      // pt a distribui in mod egal
            N++;
        }
        chunk = N / (p - 1);
        int index = 0;
        int process_id;

        MPI_Request carry_request, chunk_request;
        carry = 0;
        MPI_Isend(&chunk, 1, MPI_INT, 1, 5, MPI_COMM_WORLD, &chunk_request);    // trimit procesului 1 dimensiunea chunkului
        MPI_Isend(&carry, 1, MPI_INT, 1, 4, MPI_COMM_WORLD, &carry_request);    // trimit procesului 1 carry-ul
        for (process_id = 1; process_id < p; process_id++) {    // pt fiecare proces
            vector<int> a;
            vector<int> b;
            for (int i = 0; i < chunk; i++) {
                if (index < length1) {
                    input1 >> line;
                    a.insert(a.begin(), stoi(line));    // construiesc vectorul inserand cifrele in ordine inversa
                }
                else {
                    a.push_back(0);     // zerouri la final
                }
                if (index < length2) {
                    input2 >> line;
                    b.insert(b.begin(), stoi(line));
                }
                else {
                    b.push_back(0);
                }
                index++;
            }
            if (process_id == 1)
                startTime = MPI_Wtime();
            MPI_Request request;
            MPI_Isend(a.data(), chunk, MPI_INT, process_id, 1, MPI_COMM_WORLD, &request);   // trimit procesului curent a (chunk-ul tocmai citit din a)
            MPI_Isend(b.data(), chunk, MPI_INT, process_id, 2, MPI_COMM_WORLD, &request);   // trimit procesului curent b (chunk-ul tocmai citit din b)
        }

        input1.close();
        input2.close();

        c.resize(N + 1);

        for (process_id = 1; process_id < p; process_id++) {    // primirea rezultatelor
            if (process_id == p - 1) {
                MPI_Recv(c.data() + (process_id - 1) * chunk, chunk + 1, MPI_INT, process_id, 3, MPI_COMM_WORLD, &status);
            }
            else {
                MPI_Recv(c.data() + (process_id - 1) * chunk, chunk, MPI_INT, process_id, 3, MPI_COMM_WORLD, &status);
            }
        }

        endTime = MPI_Wtime();
        cout << endTime - startTime << endl;   // secunde
        writeToFileReversed("rezultat.txt", c);

    }
    else if (rank != 0) {
        vector<int> a, b, c;
        MPI_Request carry_request, a_request, b_request, chunk_request;
        int carry;

        MPI_Irecv(&chunk, 1, MPI_INT, rank - 1, 5, MPI_COMM_WORLD, &chunk_request);     // primeste dimensiunea unui chunk
        MPI_Wait(&chunk_request, &status);
        cout << "Sunt procesul " << rank << " si am primit chunk " << chunk << " de la procesul " << rank - 1 << endl;
        a.resize(chunk);
        b.resize(chunk);
        
        MPI_Irecv(&carry, 1, MPI_INT, rank - 1, 4, MPI_COMM_WORLD, &carry_request);     // primeste carry de la procesul anterior
        MPI_Irecv(a.data(), chunk, MPI_INT, 0, 1, MPI_COMM_WORLD, &a_request);      // primeste a de la procesul 0
        MPI_Irecv(b.data(), chunk, MPI_INT, 0, 2, MPI_COMM_WORLD, &b_request);      // primeste b de la procesul 0
        
        MPI_Wait(&carry_request, &status);
        MPI_Wait(&a_request, &status);
        MPI_Wait(&b_request, &status);
        
        for (int i = 0; i < chunk; i++) {       // calculul efectiv
            c.push_back((a[i] +b[i] + carry) % 10);
            if (a[i] + b[i] + carry >= 10)
                carry = 1;
            else carry = 0;
        }

        if (rank < p - 1) {
            MPI_Request send_carry_request, send_result_request;
            
            MPI_Isend(&chunk, 1, MPI_INT, rank + 1, 5, MPI_COMM_WORLD, &chunk_request);     // trimite dimensiunea chunkului catre procesul urmator
            cout << "Sunt procesul " << rank << " si am trimis chunk " << chunk << " procesului " << rank + 1 << endl;
            MPI_Isend(&carry, 1, MPI_INT, rank + 1, 4, MPI_COMM_WORLD, &send_carry_request);    // trimite carry catre procesul urmator
            MPI_Isend(c.data(), chunk, MPI_INT, 0, 3, MPI_COMM_WORLD, &send_result_request);    // trimite procesului 0 rezultatul calculat
        }
        else {  // ultimul proces pune 1 pe pozitia care va fi cea mai semnificativa dupa inversare si trimite procesului 0 rezultatul calculat
            c.push_back(carry);     
            MPI_Request send_result_request;
            MPI_Isend(c.data(), chunk + 1, MPI_INT, 0, 3, MPI_COMM_WORLD, &send_result_request);
        }
    }

    MPI_Finalize();
    return 0;
}