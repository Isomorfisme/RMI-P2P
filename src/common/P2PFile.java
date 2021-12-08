package common;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class P2PFile implements Serializable {
    private static final long serialVersionUID = 6529685098267757691L;

    private byte[] bytes;
    private String hash;
    private ArrayList<String> names = new ArrayList<>();
    private String name = "";
    private String filename = "";
    private ArrayList<String> filenames = new ArrayList<>();
    private ArrayList<String> keywords = new ArrayList<>();
    private ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<Node> folders = new ArrayList<>();

    public P2PFile(){
    }

    public P2PFile(Path path, String name, Node folder) throws IOException {
        File file = new File(String.valueOf(path));
        filename = file.getName();
        filenames.add(filename);
        this.name = name;
        names.add(this.name);
        folders.add(folder);
        bytes = toBytes(file);
        hash(bytes);
    }

    public void addFolder(Node folder){
        folders.add(folder);
    }

    public ArrayList<Node> getFolders(){
        return folders;
    }

    public void setName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write the new Name: ");
        String name = scanner.nextLine();
        names.remove(this.name);
        this.name = name;
        names.add(name);
    }

    public void addName(ArrayList<String> name){
        names.addAll(name);
    }

    public String getName(){
        return name;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setKeywords() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write the keywords separated by one space: ");
        String keywords = scanner.nextLine();
        String[] allWords = keywords.split(" ");
        this.keywords.addAll(Arrays.asList(allWords));
    }

    public void addKeywords(ArrayList<String> moreKeywords){
        keywords.addAll(moreKeywords);
    }

    public ArrayList<String> getKeywords(){
        return keywords;
    }

    public String setFilename(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write the new filename: ");
        String filename = scanner.nextLine();
        this.filename = filename;
        return filename;
    }

    public void setDescription() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write the description: ");
        String description = scanner.nextLine();
        descriptions.add(description);
    }

    public void addDescription(ArrayList<String> moreDescriptions) {
        descriptions.addAll(moreDescriptions);
    }

    public ArrayList<String> getDescription(){
        return descriptions;
    }

    public byte[] toBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    public void hash(byte[] fileContent){
        BigInteger bigInt = new BigInteger(1, fileContent);
        String bigHash = String.format("%0" + (fileContent.length << 1) + "x", bigInt);
        hash = bigHash.substring(0, Math.min(bigHash.length(), 100));
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getHash(){
        return hash;
    }

    public String getFilename(){
        return filename;
    }

    public void addFilename(String filename){
        filenames.add(filename);
    }

    public String toString(){
        return "Filename: " + this.filename + "\nHash: " + this.hash + "\nName: " + this.names +
                "\nKeywords: " + this.keywords + "\nDescription: " + this.descriptions + "\n";
    }
}
