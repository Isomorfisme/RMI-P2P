package common;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class P2PFile implements Serializable {
    private static final long serialVersionUID = 6529685098267757691L;

    public String hash = "";
    public String name = "";
    public String[] keywords;
    public String description = "";
    public Path localPath;
    public File file;

    public P2PFile(Path path, String name) throws IOException {
        localPath = path;
        file = new File(String.valueOf(path));
        this.name = name;
        hash(toBytes());
    }

    public void setName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write the new Name: ");
        String name = scanner.nextLine();
        this.name = name;
    }

    public void setKeywords() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write the keywords: ");
        String keywords = scanner.nextLine();
        this.keywords = keywords.split(" ");
    }

    public void setDescription() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write the description: ");
        String description = scanner.nextLine();
        this.description = description;
    }

    public byte[] toBytes() throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public void hash(byte[] fileContent){
        BigInteger bigInt = new BigInteger(1, fileContent);
        String bigHash = String.format("%0" + (fileContent.length << 1) + "x", bigInt);
        hash = bigHash.substring(0, Math.min(bigHash.length(), 100));
    }

    public String toString(){
        return this.name;
    }
}
