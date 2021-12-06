package common;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

public class P2PFile implements Serializable {
    private static final long serialVersionUID = 6529685098267757691L;

    private String hash = "";
    private String name = "";
    private String[] keywords;
    private String description = "";
    private Path localPath;
    private File file;

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

    public String getName() {
        return name;
    }

    public void setKeywords() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write the keywords separated by one space: ");
        String keywords = scanner.nextLine();
        this.keywords = keywords.split(" ");
    }

    public String[] getKeywords(){
        return keywords;
    }

    public void setDescription() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write the description: ");
        String description = scanner.nextLine();
        this.description = description;
        System.out.println(this.description);
    }

    public String getDescription(){
        System.out.println(this.description);
        return description;
    }

    public byte[] toBytes() throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public void hash(byte[] fileContent){
        BigInteger bigInt = new BigInteger(1, fileContent);
        String bigHash = String.format("%0" + (fileContent.length << 1) + "x", bigInt);
        hash = bigHash.substring(0, Math.min(bigHash.length(), 100));
    }

    public String getHash(){
        return hash;
    }

    public File getFile(){
        return file;
    }

    public String toString(){
        return this.hash + this.name + Arrays.toString(this.keywords) + this.description + this.localPath + this.file;
    }
}
