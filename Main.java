import aed3.ArvoreBMais_Int_Int;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        ArvoreBMais_Int_Int relUserQuestions = new ArvoreBMais_Int_Int(5, "testdata/relationship_user_questions.idx");
        CRUD<Usuario> userDatabase = new CRUD<>(Usuario.class.getConstructor(), "users");
        while(true) {
            if(!logged) {
                System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nACESSO\n");
                System.out.println("1) Login\n2) Cadastro\n3) Esqueci minha senha\n\n0) Fechar\n");
                System.out.print("Opção: ");

                int option = Integer.parseInt(input.readLine());
                if(option == 0) {
                    return;
                }

                switch(option) {
                    case 1:
                        user = login(userDatabase);
                        logged = (user != null);
                        break;

                    case 2:
                        signup(userDatabase);
                        break;

                    case 3:
                        passwordRecovery(userDatabase);
                        break;
                }
            } else {
                System.out.println("\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO");
                System.out.printf("Olá, %s%s%s!\n\n", Colors.ANSI_GREEN, user.getName(), Colors.ANSI_RESET);
//                System.out.println("1) Consultar dados\n2) Alterar senha\n" + Colors.ANSI_RED + "3) Excluir conta[TODO]" + Colors.ANSI_RESET + "\n\n0) Sair\n");
                System.out.println("1) Gerenciamento de perguntas\n2) Consultar/responder perguntas\n3) Notificações: \n\n0) Sair\n");
                System.out.print("Opção: ");

                int option = Integer.parseInt(input.readLine());
                logged = (option != 0);

                switch(option) {
                    case 1:
                        questionsMenu(user, relUserQuestions);
                        break;
//                    ~~ PERFIL ~~
//                    case 1:
//                        System.out.printf("\nNome: %s\nEmail: %s\n", user.getName(), user.getEmail());
//                        break;
//
//                    case 2:
//                        changePassword(userDatabase, user);
//                        break;
//
//                    DEBUG USE ONLY
//                    case 3:
//                        System.out.print(Colors.ANSI_RED + "\nTem certeza que deseja excluir sua conta? [s/N]: " + Colors.ANSI_RESET);
//                        char answer = input.readLine().toLowerCase().charAt(0);
//                        if(answer == 's') {
//                            logged = !userDatabase.delete(user.getID());
//                            System.out.println(Colors.ANSI_RED + "\nUsuário deletado!\n" + Colors.ANSI_RESET);
//                        }
//                        break;
                }
            }

            sleep();
        }
    }

    public static Usuario login(CRUD<Usuario> userDatabase) throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nLOGIN\n");
        System.out.print("Email: ");
        String email = input.readLine();
        System.out.print("Senha: ");
        String password = input.readLine();

        Usuario tmpUser = userDatabase.read(email);
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

    public static void changePassword(CRUD<Usuario> userDatabase, Usuario user) throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > PERFIL > TROCAR SENHA\n");
        System.out.print("Senha atual: ");
        String currentPassword = input.readLine();
        if(!currentPassword.equals(user.getPassword())) {
            System.out.println(Colors.ANSI_RED + "\nSenha incorreta!\n" + Colors.ANSI_RESET);
            return;
        }

        System.out.print("Nova senha: ");
        String newPassword = input.readLine();
        if(newPassword.equals(user.getPassword())) {
            System.out.println(Colors.ANSI_RED + "\nA nova senha deve ser diferente da atual!\n" + Colors.ANSI_RESET);
            return;
        }

        System.out.print("Confirme a nova senha: ");
        String newPassword2 = input.readLine();
        if(!newPassword2.equals(newPassword)) {
            System.out.println(Colors.ANSI_RED + "\nAs senhas não coincidem!\n" + Colors.ANSI_RESET);
            return;
        }

        user.setPassword(newPassword);
        userDatabase.update(user);
        System.out.println(Colors.ANSI_GREEN + "\nSenha alterada com sucesso!\n" + Colors.ANSI_RESET);
    }

    public static void signup(CRUD<Usuario> userDatabase) throws Exception {
        String email;
        Usuario tmpUser;
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nCADASTRO\n");

        do {
            System.out.print("Insira seu email: ");
            email = input.readLine();
            tmpUser = userDatabase.read(email);
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
            userDatabase.create(new Usuario(name, email, password));
        } catch(Exception e) {
            System.out.println(Colors.ANSI_RED + "\nFalha ao cadastrar usuário!\n" + Colors.ANSI_RESET);
            e.printStackTrace();
            return;
        }

        System.out.println(Colors.ANSI_GREEN + "\nUsuário cadastrado com sucesso, faça login para confirmar!" + Colors.ANSI_RESET);
        sleep();
    }

    public static void passwordRecovery(CRUD<Usuario> userDatabase) throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nRECUPERAÇÃO DE SENHA\n");
        System.out.print("Email: ");
        String email = input.readLine();
        Usuario tmpUser = userDatabase.read(email);
        if (tmpUser == null) {
            System.out.println(Colors.ANSI_RED + "\nEmail não cadastrado!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        String tmpPassword = UUID.randomUUID().toString().substring(0, 8);
        tmpUser.setPassword(tmpPassword);
        userDatabase.update(tmpUser);

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

    public static void questionsMenu(Usuario user, ArvoreBMais_Int_Int relationship) throws Exception {
        CRUD<Pergunta> questionDatabase = new CRUD<>(Pergunta.class.getConstructor(), "questions");
        while(true) {
            System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > GERENCIAMENTO DE PERGUNTAS\n");
            System.out.println("1) Listar\n2) Criar\n3) Alterar\n4) Arquivar \n\n0) Retornar\n");
            System.out.print("Opção: ");

            int option = Integer.parseInt(input.readLine());
            if(option == 0) {
                return;
            }

            switch(option) {
                case 1:
                    listQuestions(questionDatabase, relationship, user);
                    break;

                case 2:
                    createQuestion(questionDatabase, relationship, user);
                    break;

                case 3:
                    updateQuestion(questionDatabase, relationship, user);
                    break;

                case 4:
                    archiveQuestion(questionDatabase, relationship, user);
                    break;
            }
        }
    }

    private static void listQuestions(CRUD<Pergunta> questionDatabase, ArvoreBMais_Int_Int relationship, Usuario user) throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > GERENCIAMENTO DE PERGUNTAS > MINHAS PERGUNTAS\n");
        int[] questionsIDs = relationship.read(user.getID());
        for (int questionID : questionsIDs) {
            Pergunta question = questionDatabase.read(questionID);
            System.out.printf("%d. %s\n", questionID, (question.getStatus() ? "" : "(Arquivada)"));
            System.out.println(question.getFormattedDate());
            System.out.printf("%s\n\n", question.getQuestion());
        }

        sleep(questionsIDs.length * 1.5);
    }

    private static void createQuestion(CRUD<Pergunta> questionDatabase, ArvoreBMais_Int_Int relationship, Usuario user) throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > GERENCIAMENTO DE PERGUNTAS > CRIAÇÂO DE PERGUNTAS\n");
        System.out.print("Insira sua pergunta: ");
        String questionText = input.readLine();
        if(questionText.length() == 0) {
            System.out.println(Colors.ANSI_RED + "\nO texto da pergunta não pode ser vazio!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        System.out.print("Confirmar criação da pergunta?[Y/n]: ");
        String confirm = input.readLine();
        if(confirm.length() > 0 && confirm.charAt(0) == 'n') {
            System.out.println(Colors.ANSI_RED + "\nNão confirmado!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        System.out.println(Colors.ANSI_GREEN + "Confirmado!" + Colors.ANSI_RESET);

        Pergunta question = new Pergunta(user.getID(), questionText);
        int id = questionDatabase.create(question);
        boolean ok = relationship.create(user.getID(), id);
        if(!ok) {
            questionDatabase.delete(id);
            System.out.println(Colors.ANSI_RED + "\nNão foi possível criar a pergunta!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        System.out.println(Colors.ANSI_GREEN + "Pergunta criada!" + Colors.ANSI_RESET);
        sleep();
    }

    private static void updateQuestion(CRUD<Pergunta> questionDatabase, ArvoreBMais_Int_Int relationship, Usuario user) throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > GERENCIAMENTO DE PERGUNTAS > ALTERAÇÃO DE PERGUNTAS\n");
        int[] questionsIDs = relationship.read(user.getID());
        for (int questionID : questionsIDs) {
            Pergunta question = questionDatabase.read(questionID);
            if(question.getStatus()) {
                System.out.printf("%d.\n", questionID);
                System.out.println(question.getFormattedDate());
                System.out.printf("%s\n\n", question.getQuestion());
            }
        }

        System.out.println("0) Retornar\n");
        System.out.print("Insira o ID: ");
        int id = Integer.parseInt(input.readLine());
        if(id == 0) {
            return;
        }

        Pergunta question = questionDatabase.read(id);
        if(question.getStatus()) {
            System.out.printf("%d.\n", id);
            System.out.println(question.getFormattedDate());
            System.out.printf("%s\n\n", question.getQuestion());

            System.out.print("Insira a nova pergunta: ");
            String newQuestionText = input.readLine();
            if(newQuestionText.length() == 0) {
                System.out.println(Colors.ANSI_RED + "\nO texto da pergunta não pode ser vazio!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.print("Confirmar alteração da pergunta?[Y/n]: ");
            String confirm = input.readLine();
            if(confirm.length() > 0 && confirm.charAt(0) == 'n') {
                System.out.println(Colors.ANSI_RED + "\nNão confirmado!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.println(Colors.ANSI_GREEN + "Confirmado!" + Colors.ANSI_RESET);
            question.setQuestion(newQuestionText);
            questionDatabase.update(question);
            System.out.println(Colors.ANSI_GREEN + "Pergunta alterada!" + Colors.ANSI_RESET);
            sleep();
        }
    }

    private static void archiveQuestion(CRUD<Pergunta> questionDatabase, ArvoreBMais_Int_Int relationship, Usuario user) throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > GERENCIAMENTO DE PERGUNTAS > ARQUIVAR PERGUNTAS\n");
        int[] questionsIDs = relationship.read(user.getID());
        for (int questionID : questionsIDs) {
            Pergunta question = questionDatabase.read(questionID);
            if(question.getStatus()) {
                System.out.printf("%d.\n", questionID);
                System.out.println(question.getFormattedDate());
                System.out.printf("%s\n\n", question.getQuestion());
            }
        }

        System.out.println("0) Retornar\n");
        System.out.print("Insira o ID: ");
        int id = Integer.parseInt(input.readLine());
        if(id == 0) {
            return;
        }

        Pergunta question = questionDatabase.read(id);
        if(question.getStatus()) {
            System.out.printf("%d.\n", id);
            System.out.println(question.getFormattedDate());
            System.out.printf("%s\n\n", question.getQuestion());

            System.out.print("Confirmar criação da pergunta? Essa ação não pode ser revertida![Y/n]: ");
            String confirm = input.readLine();
            if(confirm.length() > 0 && confirm.charAt(0) == 'n') {
                System.out.println(Colors.ANSI_RED + "\nNão confirmado!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.println(Colors.ANSI_GREEN + "Confirmado!" + Colors.ANSI_RESET);
            question.setStatus(false);
            questionDatabase.update(question);
            System.out.println(Colors.ANSI_GREEN + "Pergunta arquivada!" + Colors.ANSI_RESET);
            sleep();
        }
    }

    public static void sleep() {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException ignored) {}
    }

    public static void sleep(double second) {
        try {
            Thread.sleep((long) (second * 1000));
        } catch(InterruptedException ignored) {}
    }
}