package entity;

import java.io.IOException;

public interface Registro {
    String __author__ = "Texugo";

    void setID(int ID);
    int getID();
    String secondaryKey();
    byte[] toByteArray() throws IOException;
    void fromByteArray(byte[] byteArray) throws IOException;
}
