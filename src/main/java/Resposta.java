import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Resposta implements Registro {
    public static final String __author__ = "Texugo";

    private int ID, userID, questionID;
    private long createdAt;
    private short rating;
    private String answer;
    private boolean status;

    public Resposta() {
        this.ID = -1;
        this.userID = -1;
        this.questionID = -1;
        this.createdAt = 0;
        this.rating = 0;
        this.answer = "";
        this.status = true;
    }

    public Resposta(int userID, int questionID, String answer) {
        this.ID = -1;
        this.userID = userID;
        this.questionID = questionID;
        this.createdAt = System.currentTimeMillis();
        this.rating = 0;
        this.answer = answer;
        this.status = true;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setUserID(int userID) { this.userID = userID; }

    public void setQuestionID(int questionID) { this.questionID = questionID; }

    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public void setAnswer(String answer) { this.answer = answer; }

    public void setRating(short rating) { this.rating = rating; }

    public void setStatus(boolean status) { this.status = status; }

    public int getID() {
        return this.ID;
    }

    public int getUserID() { return this.userID; }

    public int getQuestionID() { return this.questionID; }

    public long getCreatedAt() { return this.createdAt; }

    public String getAnswer() { return this.answer; }

    public short getRating() { return this.rating; }

    public boolean getStatus() { return this.status; }

    public String getFormattedDate() {
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date createdAt = new Date(this.createdAt);
        return formatter.format(createdAt);
    }

    public String getHumanizedDate() {
        SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");
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
        dataOutputStream.writeInt(this.questionID);
        dataOutputStream.writeLong(this.createdAt);
        dataOutputStream.writeShort(this.rating);
        dataOutputStream.writeUTF(this.answer);
        dataOutputStream.writeBoolean(this.status);

        return byteArrayOutputStream.toByteArray();
    }

    public void fromByteArray(byte[] byteArray) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        this.ID = dataInputStream.readInt();
        this.userID = dataInputStream.readInt();
        this.questionID = dataInputStream.readInt();
        this.createdAt = dataInputStream.readLong();
        this.rating = dataInputStream.readShort();
        this.answer = dataInputStream.readUTF();
        this.status = dataInputStream.readBoolean();
    }

    @Override
    public String toString() {
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        Date createdAt = new Date(this.createdAt);
        return "Resposta{" +
                "ID=" + this.ID +
                ", userID=" + this.userID +
                ", questionID=" + this.questionID +
                ", createdAt=" + formatter.format(createdAt) +
                ", rating=" + this.rating +
                ", answer='" + this.answer + '\'' +
                ", status=" + this.status +
                '}';
    }
}
