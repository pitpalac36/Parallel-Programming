import java.io.*;

public class LinkedList {
    private Node first;

    public LinkedList(String[] filenames) throws IOException {
        for (String each : filenames) {
            BufferedReader reader = new BufferedReader(new FileReader(each));
            String line;
            while((line = reader.readLine()) != null) {
                var fields = line.split(",");
                Node nod = new Node(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]));
                insert(nod);
            }
            reader.close();
        }
    }

    public void insert(Node nod) {
        if (first == null)   // adaug primul
            first = nod;
        else
        {
            if (first.exp >= nod.exp) {    // actualizez primul
                if (first.exp == nod.exp)
                    first.coef += nod.coef;
                else {      // adaug inaintea primului
                    nod.next = first;
                    nod.next.prev = nod;
                    first = nod;
                }
            }
            else {
                Node current = first;
                while (current.next != null && current.next.exp <= nod.exp)
                    current = current.next;
                if (current.exp == nod.exp) {   // actualizez nodul curent
                    current.coef += nod.coef;
                }
                else {      // adaug dupa nodul curent
                    nod.next = current.next;
                    if (current.next != null)
                        nod.next.prev = nod;
                    current.next = nod;
                    nod.prev = current;
                }
            }
        }
    }

    public void writeToFile(String filename) {
        try {
            FileWriter writer = new FileWriter(new File(filename));
            Node curent = first;
            String line;
            while(curent != null) {
                if (curent.coef != 0) {
                    line = "coef : " + curent.coef + "   exp : " + curent.exp + "\n";
                    writer.write(line);
                }
                curent = curent.next;
            }
            writer.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
