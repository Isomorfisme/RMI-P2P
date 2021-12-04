package common;

import java.io.Serializable;
import java.util.Scanner;

public class P2PFile implements Serializable {
    private static final long serialVersionUID = 6529685098267757691L;

    String name = "filename";
    int id = 0;

    public P2PFile(int port){
        this.id = port;
    }

    public void setName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write the filename: ");
        String filename = scanner.nextLine();
        this.name = filename;
    }

    public String toString(){
        return this.name + this.id;
    }
}
