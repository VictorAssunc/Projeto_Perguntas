package entity;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Voto implements Registro {
    public static final String __author__ = "Texugo";

    private int ID, userID, relatedID;
    private byte relatedType;
    private boolean vote;

    public Voto() {
        this.ID = -1;
        this.userID = -1;
        this.relatedID = -1;
        this.relatedType = ' ';
        this.vote = false;
    }

    public Voto(int userID, int relatedID, byte relatedType, boolean vote) {
        this.ID = -1;
        this.userID = userID;
        this.relatedID = relatedID;
        this.relatedType = relatedType;
        this.vote = vote;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setUserID(int userID) { this.userID = userID; }

    public void setRelatedID(int relatedID) { this.relatedID = relatedID; }

    public void setRelatedType(byte relatedType) { this.relatedType = relatedType; }

    public void setVote(boolean vote) { this.vote = vote; }

    public int getID() {
        return this.ID;
    }

    public int getUserID() { return this.userID; }

    public int getRelatedID() { return relatedID; }

    public byte getRelatedType() { return relatedType; }

    public boolean getVote() { return vote; }

    public String secondaryKey() {
        return this.userID + "|" + this.relatedType + "|" + this.relatedID;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeInt(this.ID);
        dataOutputStream.writeInt(this.userID);
        dataOutputStream.writeInt(this.relatedID);
        dataOutputStream.writeByte(this.relatedType);
        dataOutputStream.writeBoolean(this.vote);

        return byteArrayOutputStream.toByteArray();
    }

    public void fromByteArray(byte[] byteArray) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        this.ID = dataInputStream.readInt();
        this.userID = dataInputStream.readInt();
        this.relatedID = dataInputStream.readInt();
        this.relatedType = dataInputStream.readByte();
        this.vote = dataInputStream.readBoolean();
    }

    @Override
    public String toString() {
        return "entity.Voto{" +
                "ID=" + ID +
                ", userID=" + userID +
                ", relatedID=" + relatedID +
                ", relatedType=" + relatedType +
                ", vote=" + vote +
                '}';
    }
}
