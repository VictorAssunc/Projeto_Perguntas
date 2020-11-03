import aed3.ArvoreBMais_Int_Int;
import aed3.ListaInvertida;
import colors.Colors;
import entity.Pergunta;
import entity.Resposta;
import entity.Usuario;
import entity.Voto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.*;

class Encrypt {
    public static String getPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for(byte b : bytes) {
            hexString.append(String.format("%02X", 0xFF & b));
        }

        return hexString.toString();
    }

    public static boolean checkPassword(String inputPassword, String hash) throws NoSuchAlgorithmException {
        return getPassword(inputPassword).equals(hash);
    }
}

public class Main {
    static boolean logged;
    static Usuario user;
    static BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    static CRUD<Usuario> usersDatabase;
    static CRUD<Pergunta> questionsDatabase;
    static CRUD<Resposta> answersDatabase;
    static CRUD<Voto> votesDatabase;
    static ListaInvertida keywordsDatabase;
    static ArvoreBMais_Int_Int relUserQuestions;
    static ArvoreBMais_Int_Int relUserAnswers;
    static ArvoreBMais_Int_Int relQuestionAnswers;


    public static void main(String[] args) throws Exception {
        usersDatabase = new CRUD<>(Usuario.class.getConstructor(), "users");
        questionsDatabase = new CRUD<>(Pergunta.class.getConstructor(), "questions");
        answersDatabase = new CRUD<>(Resposta.class.getConstructor(), "answers");
        votesDatabase = new CRUD<>(Voto.class.getConstructor(), "votes");
        keywordsDatabase = new ListaInvertida(5, "testdata/keywords_dictionary.db", "testdata/keywords_blocks.db");

        relUserQuestions = new ArvoreBMais_Int_Int(5, "testdata/relationship_user_questions.idx");
        if(relUserQuestions.empty()) { relUserQuestions.create(0, 0); }

        relUserAnswers = new ArvoreBMais_Int_Int(5, "testdata/relationship_user_answers.idx");
        if(relUserAnswers.empty()) { relUserAnswers.create(0, 0); }

        relQuestionAnswers = new ArvoreBMais_Int_Int(5, "testdata/relationship_question_answers.idx");
        if(relQuestionAnswers.empty()) { relQuestionAnswers.create(0, 0); }

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
                        user = login();
                        logged = (user != null);
                        break;

                    case 2:
                        signup();
                        break;

                    case 3:
                        passwordRecovery();
                        break;
                }
            } else {
                System.out.println("\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO");
                System.out.printf("Olá, %s%s%s!\n\n", Colors.ANSI_CYAN, user.getName(), Colors.ANSI_RESET);
//                System.out.println("1) Consultar dados\n2) Alterar senha\n" + Colors.ANSI_RED + "3) Excluir conta[TODO]" + Colors.ANSI_RESET + "\n\n0) Sair\n");
                System.out.println("1) Gerenciamento de perguntas\n2) Consultar/responder perguntas\n3) Notificações: \n\n0) Sair\n");
                System.out.print("Opção: ");

                int option = Integer.parseInt(input.readLine());
                logged = (option != 0);

                switch(option) {
                    case 1:
                        questionsMenu();
                        break;

                    case 2:
                        searchQuestions();
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

    public static Usuario login() throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nLOGIN\n");
        System.out.print("Email: ");
        String email = input.readLine();
        System.out.print("Senha: ");
        String password = input.readLine();

        Usuario tmpUser = usersDatabase.read(email);
        if(tmpUser == null) {
            System.out.println(Colors.ANSI_RED + "\nEmail não cadastrado!\n" + Colors.ANSI_RESET);
            return null;
        }

        if(!Encrypt.checkPassword(password, tmpUser.getPassword())) {
            System.out.println(Colors.ANSI_RED + "\nSenha incorreta!\n" + Colors.ANSI_RESET);
            return null;
        }

        return tmpUser;
    }

    public static void changePassword(Usuario user) throws Exception {
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
        usersDatabase.update(user);
        System.out.println(Colors.ANSI_GREEN + "\nSenha alterada com sucesso!\n" + Colors.ANSI_RESET);
    }

    public static void signup() throws Exception {
        String email;
        Usuario tmpUser;
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nCADASTRO\n");

        do {
            System.out.print("Insira seu email: ");
            email = input.readLine();
            tmpUser = usersDatabase.read(email);
            if (tmpUser != null) {
                System.out.println(Colors.ANSI_RED + "\nUsuário já cadastrado, tente outro email!\n" + Colors.ANSI_RESET);
                sleep();
            }
        } while(tmpUser != null);

        System.out.print("Insira seu nome: ");
        String name = input.readLine();
        System.out.print("Insira sua senha: ");
        String password = Encrypt.getPassword(input.readLine());

        try {
            usersDatabase.create(new Usuario(name, email, password));
        } catch(Exception e) {
            System.out.println(Colors.ANSI_RED + "\nFalha ao cadastrar usuário!\n" + Colors.ANSI_RESET);
            e.printStackTrace();
            return;
        }

        System.out.println(Colors.ANSI_GREEN + "\nUsuário cadastrado com sucesso, faça login para confirmar!" + Colors.ANSI_RESET);
        sleep();
    }

    public static void passwordRecovery() throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nRECUPERAÇÃO DE SENHA\n");
        System.out.print("Email: ");
        String email = input.readLine();
        Usuario tmpUser = usersDatabase.read(email);
        if (tmpUser == null) {
            System.out.println(Colors.ANSI_RED + "\nEmail não cadastrado!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        String tmpPassword = Encrypt.getPassword(UUID.randomUUID().toString().substring(0, 8));
        tmpUser.setPassword(tmpPassword);
        usersDatabase.update(tmpUser);

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

    public static void questionsMenu() throws Exception {
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
                    listQuestions();
                    break;

                case 2:
                    createQuestion();
                    break;

                case 3:
                    updateQuestion();
                    break;

                case 4:
                    archiveQuestion();
                    break;
            }
        }
    }

    private static String normalizeKeywords(String keywords) {
        return Normalizer.normalize(keywords, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
    }

    private static void listQuestions() throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > GERENCIAMENTO DE PERGUNTAS > MINHAS PERGUNTAS\n");
        int[] questionsIDs = relUserQuestions.read(user.getID());
        for(int questionID : questionsIDs) {
            Pergunta question = questionsDatabase.read(questionID);
            System.out.printf("%d. %s\n", questionID, (question.getStatus() ? "" : "(Arquivada)"));
            System.out.println(question.getFormattedDate());
            System.out.printf("Palavras chave: %s\n", question.getKeywords());
            System.out.printf("%s\n", question.getQuestion());
            System.out.printf("%d\n\n", question.getRating());
        }

        sleep(questionsIDs.length * 1.5);
    }

    private static void createQuestion() throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > GERENCIAMENTO DE PERGUNTAS > CRIAÇÂO DE PERGUNTAS\n");
        System.out.print("Insira sua pergunta: ");
        String questionText = input.readLine();
        if(questionText.length() == 0) {
            System.out.println(Colors.ANSI_RED + "\nO texto da pergunta não pode ser vazio!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        System.out.print("Insira as palavras chave da pergunta(separadas por espaço em branco): ");
        String keywords = normalizeKeywords(input.readLine());
        String[] keywordsArray = keywords.split(" ");
        if(keywordsArray.length == 0 || keywords.length() == 0) {
            System.out.println(Colors.ANSI_RED + "\nSua pergunta precisa ter palavras chave válidas!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        System.out.print("Confirmar criação da pergunta?[Y/n]: ");
        String confirm = input.readLine().toLowerCase();
        if(confirm.length() > 0 && confirm.charAt(0) == 'n') {
            System.out.println(Colors.ANSI_RED + "\nNão confirmado!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        System.out.println(Colors.ANSI_GREEN + "Confirmado!" + Colors.ANSI_RESET);

        Pergunta question = new Pergunta(user.getID(), questionText, keywords);
        int ID = questionsDatabase.create(question);
        boolean ok = relUserQuestions.create(user.getID(), ID);
        if(!ok) {
            relUserQuestions.delete(user.getID(), ID);
            questionsDatabase.delete(ID);
            System.out.println(Colors.ANSI_RED + "\nNão foi possível criar a pergunta!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        for(String keyword : keywordsArray) {
            ok = keywordsDatabase.create(keyword, ID);
            if(!ok) {
                keywordsDatabase.delete(keyword, ID);
                relUserQuestions.delete(user.getID(), ID);
                questionsDatabase.delete(ID);
                System.out.println(Colors.ANSI_RED + "\nNão foi possível criar a pergunta!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }
        }

        System.out.println(Colors.ANSI_GREEN + "Pergunta criada!" + Colors.ANSI_RESET);
        sleep();
    }

    private static void updateQuestion() throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > GERENCIAMENTO DE PERGUNTAS > ALTERAÇÃO DE PERGUNTAS\n");
        int[] questionsIDs = relUserQuestions.read(user.getID());
        for(int questionID : questionsIDs) {
            Pergunta question = questionsDatabase.read(questionID);
            if(question.getStatus()) {
                System.out.printf("%d.\n", questionID);
                System.out.println(question.getFormattedDate());
                System.out.printf("Palavras chave: %s\n", question.getKeywords());
                System.out.printf("%s\n", question.getQuestion());
                System.out.printf("%d\n\n", question.getRating());
            }
        }

        System.out.println("0) Retornar\n");
        System.out.print("Insira o ID: ");
        int ID = Integer.parseInt(input.readLine());
        if(ID == 0) {
            return;
        }

        Pergunta question = questionsDatabase.read(ID);
        if(question.getStatus() && question.getUserID() == user.getID()) {
            System.out.printf("%d.\n", ID);
            System.out.println(question.getFormattedDate());
            System.out.printf("%s\n", question.getQuestion());
            System.out.printf("%d\n\n", question.getRating());

            System.out.print("Insira a nova pergunta: ");
            String newQuestionText = input.readLine();
            if(newQuestionText.length() == 0) {
                System.out.println(Colors.ANSI_RED + "\nO texto da pergunta não pode ser vazio!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.print("Insira as novas palavras chave da pergunta(separadas por espaço em branco): ");
            String newKeywords = normalizeKeywords(input.readLine());
            String[] newKeywordsArray = newKeywords.split(" ");
            if(newKeywordsArray.length == 0 || newKeywords.length() == 0) {
                System.out.println(Colors.ANSI_RED + "\nSua pergunta precisa ter palavras chave válidas!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.print("Confirmar alteração da pergunta?[Y/n]: ");
            String confirm = input.readLine().toLowerCase();
            if(confirm.length() > 0 && confirm.charAt(0) == 'n') {
                System.out.println(Colors.ANSI_RED + "\nNão confirmado!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            String[] currentKeywordsArray = question.getKeywords().split(" ");
            for(String keyword : newKeywordsArray) {
                if(!Arrays.asList(currentKeywordsArray).contains(keyword)) {
                    boolean ok = keywordsDatabase.create(keyword, ID);
                    if(!ok) {
                        keywordsDatabase.delete(keyword, ID);
                        System.out.println(Colors.ANSI_RED + "\nNão foi possível criar a pergunta!\n" + Colors.ANSI_RESET);
                        sleep();
                        return;
                    }
                }
            }

            for(String keyword : currentKeywordsArray) {
                if(!Arrays.asList(newKeywordsArray).contains(keyword)) {
                    keywordsDatabase.delete(keyword, ID);
                }
            }

            System.out.println(Colors.ANSI_GREEN + "Confirmado!" + Colors.ANSI_RESET);
            question.setQuestion(newQuestionText);
            question.setKeywords(newKeywords);
            questionsDatabase.update(question);
            System.out.println(Colors.ANSI_GREEN + "Pergunta alterada!" + Colors.ANSI_RESET);
            sleep();
        }
    }

    private static void archiveQuestion() throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > GERENCIAMENTO DE PERGUNTAS > ARQUIVAR PERGUNTAS\n");
        int[] questionsIDs = relUserQuestions.read(user.getID());
        for(int questionID : questionsIDs) {
            Pergunta question = questionsDatabase.read(questionID);
            if(question.getStatus()) {
                System.out.printf("%d.\n", questionID);
                System.out.println(question.getFormattedDate());
                System.out.printf("Palavras chave: %s\n", question.getKeywords());
                System.out.printf("%s\n", question.getQuestion());
                System.out.printf("%d\n\n", question.getRating());
            }
        }

        System.out.println("0) Retornar\n");
        System.out.print("Insira o ID: ");
        int ID = Integer.parseInt(input.readLine());
        if(ID == 0) {
            return;
        }

        Pergunta question = questionsDatabase.read(ID);
        if(question.getStatus() && question.getUserID() == user.getID()) {
            System.out.printf("%d.\n", ID);
            System.out.println(question.getFormattedDate());
            System.out.printf("Palavras chave: %s\n", question.getKeywords());
            System.out.printf("%s\n\n", question.getQuestion());

            System.out.print("Confirmar arquivamento da pergunta? Essa ação não pode ser revertida![Y/n]: ");
            String confirm = input.readLine().toLowerCase();
            if(confirm.length() > 0 && confirm.charAt(0) == 'n') {
                System.out.println(Colors.ANSI_RED + "\nNão confirmado!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.println(Colors.ANSI_GREEN + "Confirmado!" + Colors.ANSI_RESET);
            question.setStatus(false);

            String[] keywordsArray = question.getKeywords().split(" ");
            for(String keyword : keywordsArray) {
                keywordsDatabase.delete(keyword, question.getID());
            }

            question.setKeywords("");
            boolean ok = questionsDatabase.update(question);
            if(!ok) {
                System.out.println(Colors.ANSI_RED + "A pergunta não pôde ser arquivada!" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.println(Colors.ANSI_GREEN + "Pergunta arquivada!" + Colors.ANSI_RESET);
            sleep();
        }
    }

    private static void searchQuestions() throws Exception {
        System.out.println("\n\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > PESQUISA DE PERGUNTAS\n");
        System.out.println("Busque perguntas por palavras chave separadas por espaço");
        System.out.print("Insira as palavras chave: ");
        String[] searchKeywords = normalizeKeywords(input.readLine()).split(" ");

        int[] allIDs = keywordsDatabase.read(searchKeywords[0]);
        for(int i = 1; i < searchKeywords.length; i++) {
            int[] IDs = keywordsDatabase.read(searchKeywords[i]);
            for(int j = 0; j < allIDs.length; j++) {
                if((Arrays.binarySearch(IDs, allIDs[j])) < 0) {
                    allIDs[j] = 0;
                }
            }
        }

        List<Pergunta> allQuestions = new ArrayList<>();
        for(int questionID : allIDs) {
            if(questionID != 0) {
                Pergunta question = questionsDatabase.read(questionID);
                if(question.getStatus()) {
                    allQuestions.add(question);
                }
            }
        }

        while(true) {
            if (!allQuestions.isEmpty()) {
                allQuestions.sort((question2, question1) -> Short.compare(question1.getRating(), question2.getRating()));

                for (Pergunta question : allQuestions) {
                    System.out.printf("\n%d. \n", question.getID());
                    System.out.println(question.getFormattedDate());
                    System.out.printf("%s\n", question.getQuestion());
                    System.out.printf("%d\n", question.getRating());
                }

                System.out.println("\n0) Retornar\n");
                System.out.print("Insira o ID: ");
                int ID = Integer.parseInt(input.readLine());
                if (ID == 0) {
                    break;
                }

                detailQuestion(ID);
            } else {
                System.out.println(Colors.ANSI_RED + "Sem perguntas!" + Colors.ANSI_RESET);
                sleep();
            }
        }
    }

    private static void questionBox(String question) {
        StringBuilder separator = new StringBuilder("+");
        separator.append("-".repeat(Math.max(0, question.length() + 2)));

        separator.append("+");
        System.out.printf("%s\n| %s |\n%s\n", separator.toString(), question, separator.toString());
    }

    private static void detailQuestion(int ID) throws Exception {
        System.out.println("\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > PESQUISA DE PERGUNTAS > PERGUNTA\n");

        Pergunta question = questionsDatabase.read(ID);
        while(true) {
            questionBox(question.getQuestion());
            System.out.printf("Criada em %s por %s\n", question.getHumanizedDate(), (question.getUserID() == user.getID() ? "Você" : usersDatabase.read(question.getUserID()).getName()));
            System.out.printf("Palavras chave: %s\n", question.getKeywords());
            System.out.printf("Nota: %s\n", question.getHumanizedRating());

            System.out.println("\nCOMENTÁRIOS\n-----------");
            // TODO: Comentários
            System.out.println("\nRESPOSTAS\n---------");
            List<Resposta> answers = listQuestionAnswers(question);

            System.out.println("\n1) Gerenciamento de respostas\n2) Gerenciamento de comentários\n3) Avaliar\n\n0) Retornar\n");
            System.out.print("Opção: ");
            int option = Integer.parseInt(input.readLine());
            if(option == 0) {
                return;
            }

            switch(option) {
                case 1:
                    answersMenu(question);
                    break;

                case 3:
                    rate(question, answers);
                    break;
            }
        }
    }

    private static List<Resposta> listQuestionAnswers(Pergunta question) throws Exception {
        int[] IDs = relQuestionAnswers.read(question.getID());
        List<Resposta> answers = new ArrayList<>();
        int count = 1;
        for(int ID : IDs) {
            Resposta answer = answersDatabase.read(ID);
            answers.add(answer);
            System.out.printf("%d.\n", count++);
            System.out.printf("%s\n", answer.getAnswer());
            System.out.printf("Respondido em %s por %s\n", answer.getHumanizedDate(), (answer.getUserID() == user.getID() ? "Você" : usersDatabase.read(answer.getUserID()).getName()));
            System.out.printf("Nota: %s\n\n", answer.getHumanizedRating());
        }

        return answers;
    }

    private static void answersMenu(Pergunta question) throws Exception {
        while(true) {
            System.out.println("\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > PESQUISA DE PERGUNTAS > PERGUNTA > GERENCIAMENTO DE RESPOSTAS\n");
            questionBox(question.getQuestion());
            System.out.printf("Criada em %s por %s\n", question.getHumanizedDate(), usersDatabase.read(question.getUserID()).getName());
            System.out.printf("Palavras chave: %s\n", question.getKeywords());
            System.out.printf("Nota: %s\n", question.getHumanizedRating());

            System.out.println("\n1) Listar\n2) Criar\n3) Alterar\n4) Arquivar \n\n0) Retornar\n");
            System.out.print("Opção: ");

            int option = Integer.parseInt(input.readLine());
            if (option == 0) {
                break;
            }

            switch(option) {
                case 1:
                    listAnswers(question);
                    break;

                case 2:
                    createAnswer(question);
                    break;

                case 3:
                    updateAnswer(question);
                    break;

                case 4:
                    archiveAnswer(question);
                    break;
            }
        }
    }

    private static void listAnswers(Pergunta question) throws Exception {
        System.out.println("\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > PESQUISA DE PERGUNTAS > PERGUNTA > GERENCIAMENTO DE RESPOSTAS > MINHAS RESPOSTAS\n");
        int[] questionAnswersIDs = relQuestionAnswers.read(question.getID());
        int[] userAnswersIDs = relUserAnswers.read(user.getID());

        List<Integer> IDs = new ArrayList<>();
        for(int answerID : questionAnswersIDs) {
            if(Arrays.stream(userAnswersIDs).anyMatch(i -> i == answerID)) {
                IDs.add(answerID);
            }
        }

        int count = 1;
        for(int ID : IDs) {
            Resposta answer = answersDatabase.read(ID);
            System.out.printf("%d. %s\n", count++, (answer.getStatus() ? "" : "(Arquivada)"));
            System.out.println(answer.getFormattedDate());
            System.out.printf("%s\n", answer.getAnswer());
            System.out.printf("%d\n\n", answer.getRating());
        }

        sleep(IDs.size() * 1.5);
    }

    private static void createAnswer(Pergunta question) throws Exception {
        System.out.println("\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > PESQUISA DE PERGUNTAS > PERGUNTA > GERENCIAMENTO DE RESPOSTAS > CRIAÇÃO DE RESPOSTAS\n");
        System.out.print("Insira sua resposta: ");
        String answerText = input.readLine();
        if(answerText.length() == 0) {
            System.out.println(Colors.ANSI_RED + "\nO texto da resposta não pode ser vazio!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        System.out.print("Confirmar criação da resposta?[Y/n]: ");
        String confirm = input.readLine().toLowerCase();
        if(confirm.length() > 0 && confirm.charAt(0) == 'n') {
            System.out.println(Colors.ANSI_RED + "\nNão confirmado!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        System.out.println(Colors.ANSI_GREEN + "Confirmado!" + Colors.ANSI_RESET);

        Resposta answer = new Resposta(user.getID(), question.getID(), answerText);
        int ID = answersDatabase.create(answer);
        boolean ok = relUserAnswers.create(user.getID(), ID);
        if(!ok) {
            relUserAnswers.delete(user.getID(), ID);
            answersDatabase.delete(ID);
            System.out.println(Colors.ANSI_RED + "\nNão foi possível criar a resposta!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        ok = relQuestionAnswers.create(question.getID(), ID);
        if(!ok) {
            relQuestionAnswers.delete(question.getID(), ID);
            relUserAnswers.delete(user.getID(), ID);
            answersDatabase.delete(ID);
            System.out.println(Colors.ANSI_RED + "\nNão foi possível criar a resposta!\n" + Colors.ANSI_RESET);
            sleep();
            return;
        }

        System.out.println(Colors.ANSI_GREEN + "Pergunta criada!" + Colors.ANSI_RESET);
        sleep();
    }

    private static void updateAnswer(Pergunta question) throws Exception {
        System.out.println("\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > PESQUISA DE PERGUNTAS > PERGUNTA > GERENCIAMENTO DE RESPOSTAS > ALTERAÇÃO DE RESPOSTA\n");
        int[] questionAnswersIDs = relQuestionAnswers.read(question.getID());
        int[] userAnswersIDs = relUserAnswers.read(user.getID());

        List<Integer> IDs = new ArrayList<>();
        for(int answerID : questionAnswersIDs) {
            if(Arrays.stream(userAnswersIDs).anyMatch(i -> i == answerID)) {
                IDs.add(answerID);
            }
        }

        for(int ID : IDs) {
            Resposta answer = answersDatabase.read(ID);
            if(answer.getStatus()) {
                System.out.printf("%d.\n", ID);
                System.out.println(answer.getFormattedDate());
                System.out.printf("%s\n", answer.getAnswer());
                System.out.printf("%d\n\n", answer.getRating());
            }
        }

        System.out.println("0) Retornar\n");
        System.out.print("Insira o ID: ");
        int ID = Integer.parseInt(input.readLine());
        if(ID == 0) {
            return;
        }

        Resposta answer = answersDatabase.read(ID);
        if(answer.getStatus() && answer.getUserID() == user.getID()) {
            System.out.printf("%d.\n", ID);
            System.out.println(answer.getFormattedDate());
            System.out.printf("%s\n\n", answer.getAnswer());
            System.out.printf("%d\n\n", answer.getRating());

            System.out.print("Insira a nova resposta: ");
            String newAnswerText = input.readLine();
            if(newAnswerText.length() == 0) {
                System.out.println(Colors.ANSI_RED + "\nO texto da resposta não pode ser vazio!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.print("Confirmar alteração da resposta?[Y/n]: ");
            String confirm = input.readLine().toLowerCase();
            if(confirm.length() > 0 && confirm.charAt(0) == 'n') {
                System.out.println(Colors.ANSI_RED + "\nNão confirmado!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.println(Colors.ANSI_GREEN + "Confirmado!" + Colors.ANSI_RESET);
            answer.setAnswer(newAnswerText);
            answersDatabase.update(answer);
            System.out.println(Colors.ANSI_GREEN + "Resposta alterada!" + Colors.ANSI_RESET);
            sleep();
        }
    }

    private static void archiveAnswer(Pergunta question) throws Exception {
        System.out.println("\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > PESQUISA DE PERGUNTAS > PERGUNTA > GERENCIAMENTO DE RESPOSTAS > ARQUIVAMENTO DE RESPOSTA\n");
        int[] questionAnswersIDs = relQuestionAnswers.read(question.getID());
        int[] userAnswersIDs = relUserAnswers.read(user.getID());

        List<Integer> IDs = new ArrayList<>();
        for(int answerID : questionAnswersIDs) {
            if(Arrays.stream(userAnswersIDs).anyMatch(i -> i == answerID)) {
                IDs.add(answerID);
            }
        }

        for(int ID : IDs) {
            Resposta answer = answersDatabase.read(ID);
            if(answer.getStatus()) {
                System.out.printf("%d.\n", ID);
                System.out.println(answer.getFormattedDate());
                System.out.printf("%s\n", answer.getAnswer());
                System.out.printf("%d\n\n", answer.getRating());
            }
        }

        System.out.println("0) Retornar\n");
        System.out.print("Insira o ID: ");
        int ID = Integer.parseInt(input.readLine());
        if(ID == 0) {
            return;
        }

        Resposta answer = answersDatabase.read(ID);
        if(answer.getStatus() && answer.getUserID() == user.getID()) {
            System.out.printf("%d.\n", ID);
            System.out.println(answer.getFormattedDate());
            System.out.printf("%s\n\n", answer.getAnswer());

            System.out.print("Confirmar arquivamento da resposta? Essa ação não pode ser revertida![Y/n]: ");
            String confirm = input.readLine().toLowerCase();
            if(confirm.length() > 0 && confirm.charAt(0) == 'n') {
                System.out.println(Colors.ANSI_RED + "\nNão confirmado!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.println(Colors.ANSI_GREEN + "Confirmado!" + Colors.ANSI_RESET);
            answer.setStatus(false);

            boolean ok = answersDatabase.update(answer);
            if(!ok) {
                System.out.println(Colors.ANSI_RED + "A resposta não pôde ser arquivada!" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            ok = relQuestionAnswers.delete(question.getID(), ID) && relUserAnswers.delete(user.getID(), ID);
            if(!ok) {
                System.out.println(Colors.ANSI_RED + "A resposta não pôde ser arquivada!" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.println(Colors.ANSI_GREEN + "Resposta arquivada!" + Colors.ANSI_RESET);
            sleep();
        }
    }

    private static void rate(Pergunta question, List<Resposta> answers) throws Exception {
        System.out.println("\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > PESQUISA DE PERGUNTAS > PERGUNTA > AVALIAÇÃO\n");

        System.out.println("1) Avaliar pergunta\n2) Avaliar resposta\n\n0) Retornar\n");
        System.out.print("Opção: ");
        int option = Integer.parseInt(input.readLine());
        if(option == 0) {
            return;
        }

        switch(option) {
            case 1:
                rateQuestion(question);
                break;

            case 2:
                rateAnswer(answers);
                break;
        }
    }

    private static void rateQuestion(Pergunta question) throws Exception {
        System.out.println("\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > PESQUISA DE PERGUNTAS > PERGUNTA > AVALIAÇÃO > PERGUNTA\n");

        if(question.getUserID() != user.getID()) {
            questionBox(question.getQuestion());
            System.out.printf("Criada em %s por %s\n", question.getHumanizedDate(), (question.getUserID() == user.getID() ? "Você" : usersDatabase.read(question.getUserID()).getName()));
            System.out.printf("Palavras chave: %s\n", question.getKeywords());
            System.out.printf("Nota: %s\n", question.getHumanizedRating());

            Voto rating = votesDatabase.read(user.getID() + "|" + (byte) 'P' + "|" + question.getID());
            if (rating != null) {
                System.out.println(Colors.ANSI_RED + "\nVocê já votou nessa pergunta!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            int inputVote;
            do {
                System.out.print("\n1) Upvote\n2) Downvote\n\nInsira o voto: ");
                inputVote = Integer.parseInt(input.readLine());
            } while (inputVote != 1 && inputVote != 2);

            boolean vote = (inputVote == 1);

            System.out.print("Confirmar voto?[Y/n]: ");
            String confirm = input.readLine().toLowerCase();
            if (confirm.length() > 0 && confirm.charAt(0) == 'n') {
                System.out.println(Colors.ANSI_RED + "\nNão confirmado!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.println(Colors.ANSI_GREEN + "Confirmado!" + Colors.ANSI_RESET);
            rating = new Voto(user.getID(), question.getID(), (byte) 'R', vote);
            votesDatabase.create(rating);
            question.updateRating(vote);
            questionsDatabase.update(question);
            return;
        }

        System.out.println(Colors.ANSI_RED + "\nVocê não pode votar na sua pergunta!\n" + Colors.ANSI_RESET);
        sleep();
    }

    private static void rateAnswer(List<Resposta> answers) throws Exception {
        System.out.println("\n\nPERGUNTAS [Alpha]\n=================\n\nINÍCIO > PESQUISA DE PERGUNTAS > PERGUNTA > AVALIAÇÃO > RESPOSTA\n");

        for(Resposta answer : answers) {
            if(answer.getUserID() != user.getID()) {
                System.out.printf("%d.\n", answer.getID());
                System.out.printf("%s\n", answer.getAnswer());
                System.out.printf("Respondido em %s por %s\n", answer.getHumanizedDate(), (answer.getUserID() == user.getID() ? "Você" : usersDatabase.read(answer.getUserID()).getName()));
                System.out.printf("Nota: %s\n\n", answer.getHumanizedRating());
            }
        }

        System.out.println("0) Retornar\n");
        System.out.print("Insira o ID: ");
        int ID = Integer.parseInt(input.readLine());
        if(ID == 0) {
            return;
        }

        Resposta answer = answersDatabase.read(ID);
        if(answer.getUserID() != user.getID()) {
            Voto rating = votesDatabase.read(user.getID() + "|" + (byte) 'R' + "|" + answer.getID());
            if (rating != null) {
                System.out.println(Colors.ANSI_RED + "\nVocê já votou nessa resposta!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            int inputVote;
            do {
                System.out.print("\n1) Upvote\n2) Downvote\n\nInsira o voto: ");
                inputVote = Integer.parseInt(input.readLine());
            } while (inputVote != 1 && inputVote != 2);

            boolean vote = (inputVote == 1);

            System.out.print("Confirmar voto?[Y/n]: ");
            String confirm = input.readLine().toLowerCase();
            if (confirm.length() > 0 && confirm.charAt(0) == 'n') {
                System.out.println(Colors.ANSI_RED + "\nNão confirmado!\n" + Colors.ANSI_RESET);
                sleep();
                return;
            }

            System.out.println(Colors.ANSI_GREEN + "Confirmado!" + Colors.ANSI_RESET);
            rating = new Voto(user.getID(), answer.getID(), (byte) 'R', vote);
            votesDatabase.create(rating);
            answer.updateRating(vote);
            answersDatabase.update(answer);
            return;
        }

        System.out.println(Colors.ANSI_RED + "\nVocê não pode votar na sua resposta!\n" + Colors.ANSI_RESET);
        sleep();
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
