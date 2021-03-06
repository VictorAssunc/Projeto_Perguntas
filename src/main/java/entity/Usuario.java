package entity;

import java.io.*;
import java.util.Date;

public class Usuario implements Registro {
    public static final String __author__ = "Texugo";

    private int ID;
//    private String user;
    private String password, name, email;
//    private Date birthdate;

    public Usuario() {
        this.ID = -1;
//        this.user = "";
        this.password = "";
        this.name = "";
        this.email = "";
//        this.birthdate = null;
    }

    public Usuario(int ID, String user, String password, String name, String email, Date birthdate) {
        this.ID = ID;
//        this.user = user;
        this.password = password;
        this.name = name;
        this.email = email;
//        this.birthdate = birthdate;
    }

    public Usuario(String name, String email, String password) {
        this.ID = -1;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

//    public void setUser(String user) {
//        this.user = user;
//    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public void setBirthdate(Date birthdate) {
//        this.birthdate = birthdate;
//    }

    public int getID() {
        return this.ID;
    }

//    public String getUser() {
//        return this.user;
//    }

    public String getPassword() {
        return this.password;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

//    public Date getBirthdate() {
//        return this.birthdate;
//    }

    public String secondaryKey() {
        return this.email;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeInt(this.ID);
//        dataOutputStream.writeUTF(this.user);
        dataOutputStream.writeUTF(this.password);
        dataOutputStream.writeUTF(this.name);
        dataOutputStream.writeUTF(this.email);
//        dataOutputStream.writeLong(this.birthdate.getTime());

        return byteArrayOutputStream.toByteArray();
    }

    public void fromByteArray(byte[] byteArray) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        this.ID = dataInputStream.readInt();
//        this.user = dataInputStream.readUTF();
        this.password = dataInputStream.readUTF();
        this.name = dataInputStream.readUTF();
        this.email = dataInputStream.readUTF();
//        this.birthdate = new Date(dataInputStream.readLong());
    }

    public String toString() {
        return "entity.Usuario{" +
                "ID=" + this.ID +
//                ", user='" + user + '\'' +
                ", password='" + this.password + '\'' +
                ", name='" + this.name + '\'' +
                ", email='" + this.email + '\'' +
//                ", birthdate=" + birthdate +
                '}';
    }
}
