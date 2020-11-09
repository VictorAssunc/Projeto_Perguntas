package entity;

import colors.Colors;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Pergunta implements Registro {
    public static final String __author__ = "Texugo";

    private int ID, userID;
    private long createdAt;
    private short rating;
    private boolean status;
    private String keywords;
    private String question;

    public Pergunta() {
        this.ID = -1;
        this.userID = -1;
        this.createdAt = 0;
        this.rating = 0;
        this.question = "";
        this.status = true;
        this.keywords = "";
    }

    public Pergunta(int userID, String question, String keywords) {
        this.ID = -1;
        this.userID = userID;
        this.createdAt = System.currentTimeMillis();
        this.rating = 0;
        this.question = question;
        this.status = true;
        this.keywords = keywords;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setUserID(int userID) { this.userID = userID; }

    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public void setQuestion(String question) { this.question = question; }

    public void setRating(short rating) { this.rating = rating; }

    public void updateRating(boolean vote) { if(vote) { this.rating++; } else { this.rating--; } }

    public void setStatus(boolean status) { this.status = status; }

    public void setKeywords(String keywords) { this.keywords = keywords; }

    public int getID() {
        return this.ID;
    }

    public int getUserID() { return this.userID; }

    public long getCreatedAt() { return this.createdAt; }

    public String getQuestion() { return this.question; }

    public short getRating() { return this.rating; }

    public String getHumanizedRating() {
        String color = Colors.ANSI_YELLOW;
        String icon = "●";

        if(this.rating > 0) {
            color = Colors.ANSI_GREEN;
            icon = "▲";
        } else if(this.rating < 0) {
            color = Colors.ANSI_RED;
            icon = "▼";
        }

        return color + this.rating + " " + icon + Colors.ANSI_RESET;
    }

    public boolean getStatus() { return this.status; }

    public String getKeywords() { return this.keywords; }

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
        dataOutputStream.writeLong(this.createdAt);
        dataOutputStream.writeShort(this.rating);
        dataOutputStream.writeUTF(this.question);
        dataOutputStream.writeBoolean(this.status);
        dataOutputStream.writeUTF(this.keywords);

        return byteArrayOutputStream.toByteArray();
    }

    public void fromByteArray(byte[] byteArray) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        this.ID = dataInputStream.readInt();
        this.userID = dataInputStream.readInt();
        this.createdAt = dataInputStream.readLong();
        this.rating = dataInputStream.readShort();
        this.question = dataInputStream.readUTF();
        this.status = dataInputStream.readBoolean();
        this.keywords = dataInputStream.readUTF();
    }

    @Override
    public String toString() {
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        Date createdAt = new Date(this.createdAt);
        return "entity.Pergunta{" +
                "ID=" + this.ID +
                ", userID=" + this.userID +
                ", createdAt=" + formatter.format(createdAt) +
                ", rating=" + this.rating +
                ", question='" + this.question + '\'' +
                ", status=" + this.status +
                ", keywords='" + this.keywords + '\'' +
                '}';
    }
}
