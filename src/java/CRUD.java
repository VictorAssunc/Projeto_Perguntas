import java.io.*;
import java.lang.reflect.Constructor;

import aed3.ArvoreBMais_Int_Int;
import aed3.ArvoreBMais_String_Int;
import aed3.HashExtensivel;

public class CRUD<T extends Registro> {
    public static final String __author__ = "Texugo";

    public final Constructor<T> constructor;
    public final String fileName;
    public final HashExtensivel primaryIndex;
    public final ArvoreBMais_String_Int secondaryIndex;

    public CRUD(Constructor<T> constructor, String fileName) throws Exception {
        this.constructor = constructor;
        this.fileName = "testdata/" + fileName;

        // CRIAÇÃO DO DIRETÓRIO `testdata`, CASO NÃO EXISTA
        File testDir = new File("testdata");
        if(!testDir.exists()) {
            testDir.mkdir();
        }

        this.primaryIndex = new HashExtensivel(10, this.fileName + ".directory.idx", this.fileName + ".bucket.idx");
        this.secondaryIndex = new ArvoreBMais_String_Int(5, this.fileName + ".tree.idx");
    }

    public int create(T object) throws Exception {
        RandomAccessFile file = new RandomAccessFile(this.fileName + ".db", "rw");

        int ID = 1;
        try {
            // LEITURA DO ÚLTIMO ID
            ID += file.readInt();
        } catch (IOException ignored) {}
        finally {
            // ALTERAÇÂO DO ÚLTIMO ID
            file.seek(0);
            file.writeInt(ID);
        }

        // ALTERAÇÃO DO ID DO OBJETO
        object.setID(ID);

        // ESCRITA NO ARQUIVO
        file.seek(file.length());
        byte[] byteArray = object.toByteArray();
        long address = file.getFilePointer();
        file.writeByte(0);  // Lápide
        file.writeShort(byteArray.length);    // Tamanho
        file.write(byteArray);  // Dados
        file.close();

        // ESCRITA INDICES DIRETOS E INDIRETOS
        this.primaryIndex.create(ID, address);
        String key = object.secondaryKey();
        if(key != null) {
            this.secondaryIndex.create(key, ID);
        }

        return ID;
    }

    public T read(int ID) throws Exception {
        RandomAccessFile file = new RandomAccessFile(this.fileName + ".db", "rw");

        // CHECA SE O ARQUIVO ESTÁ VAZIO
        if(file.length() == 0) {
            return null;
        }

        // CHECA SE O ID ALVO É MAIOR QUE O ÚLTIMO ID INSERIDO
        int lastID = file.readInt();
        if(ID > lastID) {
            return null;
        }

        // BUSCA O ID PELO ÍNDICE DIRETO E CHECA SE EXISTE
        long address = this.primaryIndex.read(ID);
        if(address == -1) {
            return null;
        }

        // POSICIONA O PONTEIRO NO INÍCIO DO REGISTRO DO DADO PARA REALIZAR A LEITURA
        file.seek(address);
        file.readByte();
        short dataLength = file.readShort();
        byte[] data = new byte[dataLength];
        file.read(data);

        // ESCREVE OS DADOS LIDOS NO OBJETO FINAL
        T object = this.constructor.newInstance();
        object.fromByteArray(data);
        return object;
    }

    public T read(String key) throws Exception {
        // BUSCA A KEY PELO ÍNDICE INDIRETO E CHECA EXISTÊNCIA
        int ID = this.secondaryIndex.read(key);
        if(ID == -1) {
            return null;
        }

        // RETORNA A BUSCA PELO ID OBTIDO ANTERIORMENTE
        return this.read(ID);
    }

    public boolean update(T newObject) throws Exception {
        RandomAccessFile file = new RandomAccessFile(this.fileName + ".db", "rw");

        // CHECA SE O ARQUIVO ESTÁ VAZIO
        if(file.length() == 0) {
            return false;
        }

        // CHECA SE O ID DO OBJETO É MAIOR QUE O ÚLTIMO ID INSERIDO
        int lastID = file.readInt();
        if(newObject.getID() > lastID) {
            return false;
        }

        // BUSCA O ID PELO ÍNDICE DIRETO E CHECA SE EXISTE
        long address = this.primaryIndex.read((newObject.getID()));
        if(address == -1) {
            return false;
        }

        // RECUPERA O REGISTRO ATUAL
        T currentObject = this.read(newObject.getID());
        // GERA O BYTE ARRYA DO NOVO OBJETO
        byte[] newData = newObject.toByteArray();
        // POSICIONA O PONTEIRO NO ENDEREÇO DO REGISTRO ATUAÇ
        file.seek(address);
        // CHECA SE O NOVO BYTE ARRAY É MAIOR QUE O ATUAL
        if(newData.length > currentObject.toByteArray().length) {
            // CASO SEJA MAIOR, O REGISTRO ATUAL É ASSASSINADO
            file.writeByte(1);  // F
            // POSICIONA O PONTEIRO NO FINAL DO ARQUIVO
            file.seek(file.length());
            // SALVA O ENDEREÇO ONDE O NOVO REGISTRO SERÁ ARMAZENADO
            long newAddress = file.getFilePointer();
            // ESCREVE OS DADOS DO NOVO REGISTRO
            file.writeByte(0);  // REVIVEU \o/
            file.writeShort(newData.length);
            file.write(newData);
            // FAZ ATUALIZAÇÃO DOS ÍNDICES DIRETO E INDIRETO
            boolean result = this.primaryIndex.update(currentObject.getID(), newAddress);

            String currentKey = currentObject.secondaryKey();
            if(currentKey != null) {
                result = this.secondaryIndex.delete(currentKey);
            }

            String newKey = newObject.secondaryKey();
            if(newKey != null) {
                result = this.secondaryIndex.create(newKey, newObject.getID());
            }

            return result;
        }

        // CASO SEJA MENOR OU IGUAL, O NOVO BYTE ARRAY É ESCRITO, SOBRESCREVENDO O ATUAL
        file.readByte();
        file.readShort();
        file.write(newData);
        // ATUALIZAÇÃO DO ÍNDICE INDIRETO, JÁ QUE O REGISTRO PERMANECE NO MESMO ENDEREÇO
        boolean result = true;
        String currentKey = currentObject.secondaryKey();
        if(currentKey != null) {
            result = this.secondaryIndex.delete(currentKey);
        }

        String newKey = newObject.secondaryKey();
        if(newKey != null) {
            result = this.secondaryIndex.create(newKey, newObject.getID());
        }

        return result;
    }

    public boolean delete(int ID) throws Exception {
        RandomAccessFile file = new RandomAccessFile(this.fileName + ".db", "rw");

        // CHECA SE O ARQUIVO ESTÁ VAZIO
        if(file.length() == 0) {
            return false;
        }

        // CHECA SE O ID DO OBJETO É MAIOR QUE O ÚLTIMO ID INSERIDO
        int lastID = file.readInt();
        if(ID > lastID) {
            return false;
        }

        // BUSCA O ID PELO ÍNDICE DIRETO E CHECA SE EXISTE
        long address = this.primaryIndex.read(ID);
        if(address == -1) {
            return false;
        }

        // REALIZA A BUSCA POR ID
        T object = this.read(ID);
        // POSICIONA O PONTEIRO NO INÍCIO DO REGISTRO
        file.seek(address);
        // ESCREVE A LÁPIDE
        file.writeByte(1);  // SUPER F
        // RETORNA SE A DELEÇÃO DOS ÍNDICES INDIRETO E DIRETO FOI CONCLUÍDA COM SUCESSO
        boolean result = true;
        String key = object.secondaryKey();
        if(key != null) {
            result = this.secondaryIndex.delete(key);
        }

        return result && this.primaryIndex.delete(ID);
    }
}
