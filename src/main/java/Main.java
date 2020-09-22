import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

class Colors {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
}

public class Main {
    static boolean logged;
    static Usuario user;
    static BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public static void main(String[] args) throws Exception {
        CRUD<Usuario> database = new CRUD<>(Usuario.class.getConstructor(), "users");
        while(true) {
            if(!logged) {
                System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nACESSO\n");
                System.out.println("1) Login\n2) Cadastro\n3) Esqueci minha senha\n\n0) Sair\n");
                System.out.print("Opção: ");

                int option = Integer.parseInt(input.readLine());
                if(option == 0) {
                    return;
                }

                switch(option) {
                    case 1:
                        user = login(database);
                        logged = (user != null);
                        break;

                    case 2:
                        signup(database);
                        break;

                    case 3:
                        passwordRecovery(database);
                        break;
                }
            } else {
                System.out.println("\nPERGUNTAS [Alpha]\n=================\n\nPERFIL");
                System.out.printf("Olá, %s%s%s!\n\n", Colors.ANSI_GREEN, user.getName(), Colors.ANSI_RESET);
                System.out.println("1) Consultar dados\n2) Alterar senha\n" + Colors.ANSI_RED + "3) Excluir conta[TODO]" + Colors.ANSI_RESET + "\n\n0) Sair\n");
                System.out.print("Opção: ");

                int option = Integer.parseInt(input.readLine());
                logged = (option != 0);

                switch(option) {
                    case 1:
                        System.out.printf("\nNome: %s\nEmail: %s\n", user.getName(), user.getEmail());
                        break;

                    case 2:
                        changePassword(database, user);
                        break;

//                    DEBUG USE ONLY
//                    case 3:
//                        System.out.print(Colors.ANSI_RED + "\nTem certeza que deseja excluir sua conta? [s/N]: " + Colors.ANSI_RESET);
//                        char answer = input.readLine().toLowerCase().charAt(0);
//                        if(answer == 's') {
//                            logged = !database.delete(user.getID());
//                            System.out.println(Colors.ANSI_RED + "\nUsuário deletado!\n" + Colors.ANSI_RESET);
//                        }
//                        break;
                }
            }

            sleep();
        }
    }

    public static Usuario login(CRUD<Usuario> database) throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nLOGIN\n");
        System.out.print("Email: ");
        String email = input.readLine();
        System.out.print("Senha: ");
        String password = input.readLine();

        Usuario tmpUser = database.read(email);
        if(tmpUser == null) {
            System.out.println(Colors.ANSI_RED + "\nEmail não cadastrado!\n" + Colors.ANSI_RESET);
            return null;
        }

        if(!password.equals(tmpUser.getPassword())) {
            System.out.println(Colors.ANSI_RED + "\nSenha incorreta!\n" + Colors.ANSI_RESET);
            return null;
        }

        return tmpUser;
    }

    public static void changePassword(CRUD<Usuario> database, Usuario user) throws IOException {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nPERFIL > TROCAR SENHA\n");
        System.out.print("Senha: ");
        String password = input.readLine();
    }

    public static void signup(CRUD<Usuario> database) throws Exception {
        String email;
        Usuario tmpUser;
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nCADASTRO\n");

        do {
            System.out.print("Insira seu email: ");
            email = input.readLine();
            tmpUser = database.read(email);
            if (tmpUser != null) {
                System.out.println(Colors.ANSI_RED + "\nUsuário já cadastrado, tente outro email!\n" + Colors.ANSI_RESET);
                sleep();
            }
        } while(tmpUser != null);

        System.out.print("Insira seu nome: ");
        String name = input.readLine();
        System.out.print("Insira sua senha: ");
        String password = input.readLine();

        try {
            database.create(new Usuario(name, email, password));
        } catch(Exception e) {
            System.out.println(Colors.ANSI_RED + "\nFalha ao cadastrar usuário!\n" + Colors.ANSI_RESET);
            e.printStackTrace();
            return;
        }

        System.out.println(Colors.ANSI_GREEN + "\nUsuário cadastrado com sucesso, faça login para confirmar!" + Colors.ANSI_RESET);
        sleep();
    }

    public static void passwordRecovery(CRUD<Usuario> database) throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nRECUPERAÇÃO DE SENHA\n");
        System.out.print("Email: ");
        String email = input.readLine();
        Usuario tmpUser = database.read(email);
        if (tmpUser == null) {
            System.out.println(Colors.ANSI_RED + "\nEmail não cadastrado!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        String tmpPassword = UUID.randomUUID().toString().substring(0, 8);
        tmpUser.setPassword(tmpPassword);
        database.update(tmpUser);

        FileOutputStream tmpFile = new FileOutputStream("recuperacao.txt");
        String template = String.format("Olá %s, você solicitou alteração de senha e aqui está sua senha temporária!\n" +
                "Altere sua senha assim que fizer login!\n" +
                "\nSENHA: %s", tmpUser.getName(), tmpUser.getPassword());
        tmpFile.write(template.getBytes());
        tmpFile.close();

        System.out.println("\nEnviamos um email com sua senha temporária[ARQUIVO `recuperacao` CRIADO NA RAIZ]\n");
        sleep();
//        return;
    }

    public static void sleep() {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException ignored) {}
    }
}
