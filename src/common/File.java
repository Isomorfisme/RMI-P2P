package common;

import java.io.Serializable;

public class File implements Serializable {
    private static final long serialVersionUID = 6529685098267757691L;

    int id = 0;
    public File(int port){
        this.id = port;
    }

    public String toString(){
        return "hello" + this.id;
    }
}
