package entity;

import colors.Colors;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Comentario implements Registro {
    public static final String __author__ = "Texugo";

    private int ID, userID, relatedID;
    private byte relatedType;
    private long createdAt;
    private String comment;

    public Comentario() {
        this.ID = -1;
        this.userID = -1;
        this.relatedID = -1;
        this.createdAt = 0;
        this.relatedType = ' ';
        this.comment = "";
    }

    public Comentario(int userID, int relatedID, byte relatedType, String comment) {
        this.ID = -1;
        this.userID = userID;
        this.relatedID = relatedID;
        this.relatedType = relatedType;
        this.createdAt = System.currentTimeMillis();
        this.comment = comment;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setUserID(int userID) { this.userID = userID; }

    public void setRelatedID(int relatedID) { this.relatedID = relatedID; }

    public void setRelatedType(byte relatedType) { this.relatedType = relatedType; }

    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public void setComment(String comment) { this.comment = comment; }

    public int getID() { return this.ID; }

    public int getUserID() { return this.userID; }

    public int getRelatedID() { return this.relatedID; }

    public byte getRelatedType() { return relatedType; }

    public long getCreatedAt() { return this.createdAt; }

    public String getComment() { return this.comment; }

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date createdAt = new Date(this.createdAt);
        return formatter.format(createdAt);
    }

    public String getHumanizedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");
        Date createdAt = new Date(this.createdAt);
        return formatter.format(createdAt);
    }

    public String secondaryKey() {
        return null;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeInt(this.ID);
        dataOutputStream.writeInt(this.userID);
        dataOutputStream.writeInt(this.relatedID);
        dataOutputStream.writeByte(this.relatedType);
        dataOutputStream.writeLong(this.createdAt);
        dataOutputStream.writeUTF(this.comment);

        return byteArrayOutputStream.toByteArray();
    }

    public void fromByteArray(byte[] byteArray) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        this.ID = dataInputStream.readInt();
        this.userID = dataInputStream.readInt();
        this.relatedID = dataInputStream.readInt();
        this.relatedType = dataInputStream.readByte();
        this.createdAt = dataInputStream.readLong();
        this.comment = dataInputStream.readUTF();
    }

    @Override
    public String toString() {
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        Date createdAt = new Date(this.createdAt);
        return "entity.Resposta{" +
                "ID=" + this.ID +
                ", userID=" + this.userID +
                ", relatedID=" + this.relatedID +
                ", relatedType=" + relatedType +
                ", createdAt=" + formatter.format(createdAt) +
                ", comment='" + this.comment + '\'' +
                '}';
    }
}
