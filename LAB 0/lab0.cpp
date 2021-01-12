#include <iostream>
#include <string>
#include <random>
#include <fstream>
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


void generate_file(string filename, int size, int min, int max) {
    ofstream out(filename);
    mt19937 mt{ std::random_device{}() };
    uniform_int_distribution<> dist(min, max);

    for (int i = 0; i < size; i++) {
        int rndNr = dist(mt);
        out << rndNr << "\n";
    }
    out.close();
}


bool equal(string filename1, string filename2) {
    ifstream in1(filename1);
    ifstream in2(filename2);
    string data1, data2;
    while ((in1 >> data1) && (in2 >> data2)) {
        if (data1 != data2) {
            return false;
        }
    }
    if (!in1.eof() || !in2.eof()) {
        return false;
    }
    return true;
}


int main()
{
	int cmd = 1;
	while (true) {
		cout << "\n1. Generare fisier\n2. Compararea a doua fisiere\n3. Iesire\n\nComanda : ";
		cin >> cmd;
		try {
			switch (cmd) {

			case 1:
			{
				string filename;
				int size, min, max;
				cout << "Nume fisier : ";
				cin >> filename;
				cout << "Dimensiune : ";
				cin >> size;
				cout << "Minim : ";
				cin >> min;
				cout << "Maxim : ";
				cin >> max;
				generate_file(filename, size, min, max);
				break;
			}

			case 2:
			{
				string filename1, filename2;
				cout << "Nume fisier 1: ";
				cin >> filename1;
				cout << "Nume fisier 2: ";
				cin >> filename2;
				if (equal(filename1, filename2)) {
					cout << "Fisierele au acelasi continut!";
				}
				else {
					cout << "Fisierele sunt diferite!";
				}
				break;
			}

			case 3:
			{
				return 0;
			}
				
			default:
			{
				cout << "Optiune invalida!";
				break;
			}
				
			}
		}
		catch (exception& ex) {
			cout << ex.what();
			cout << endl;
		}
	}
	return 0;
}
