//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import org.junit.Assert;
//
//class Colors {
//    public static final String ANSI_RESET = "\u001B[0m";
//    public static final String ANSI_RED = "\u001B[31m";
//    public static final String ANSI_GREEN = "\u001B[32m";
//    public static final String ANSI_YELLOW = "\u001B[33m";
//    public static final String ANSI_CYAN = "\u001B[36m";
//}
//
//@Deprecated
//class Teste {
//    public static void main(String[] args) throws Exception {
//        System.out.println(Colors.ANSI_CYAN + "Iniciando testes...\n" + Colors.ANSI_RESET);
//        Test.main(null);
//        System.out.println(Colors.ANSI_CYAN + "\nFim dos testes!");
//    }
//}
//
//@Deprecated
//class Test {
//    public static final String __author__ = "Texugo";
//
//    public static void main(String[] args) throws Exception {
//        // PRÉ-TESTE
//        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//        Date date = format.parse("24/08/1975");
//        Usuario user1 = new Usuario(-1, "batatinha", "senhaforte", "Ludosvaldo", "lulu.dosvaldo@gmail.com", date);
//        date = format.parse("12/05/1987");
//        Usuario user2 = new Usuario(-1, "coxinha", "senhaultraforte", "Irineu", "iri.neu@gmail.com", date);
//        date = format.parse("06/02/200");
//        Usuario user3 = new Usuario(-1, "sucrilhos", "senhafraca", "Clodovil", "clo.dovilo@gmail.com", date);
//
//        Runtime.getRuntime().exec("rm -rf testdata/");
//        CRUD<Usuario> database = new CRUD<>(Usuario.class.getConstructor(), "users");
//
//
//        // TESTES
//        // OBS.: As mensagens dentro dos asserts são apenas mensagens de erro! Só aparecem quando a asserção não se valida!
//
//        // INSERÇÃO NO ARQUIVO VAZIO
//        System.out.println(Colors.ANSI_YELLOW + "[TESTING]" + Colors.ANSI_CYAN + " INSERÇÃO");
//
//        System.out.print("\tInserindo user1...");
//        Assert.assertEquals("usuário com ID errado", 1, database.create(user1));
//        System.out.println(Colors.ANSI_GREEN + "OK[ID: " + user1.getID() + ", User: " + user1.secondaryKey() + "]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tInserindo user2...");
//        Assert.assertEquals("usuário com ID errado", 2, database.create(user2));
//        System.out.println(Colors.ANSI_GREEN + "OK[ID: " + user2.getID() + ", User: " + user2.secondaryKey() + "]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tInserindo user3...");
//        Assert.assertEquals("usuário com ID errado", 3, database.create(user3));
//        System.out.println(Colors.ANSI_GREEN + "OK[ID: " + user3.getID() + ", User: " + user3.secondaryKey() + "]" + Colors.ANSI_CYAN);
//
//        System.out.println(Colors.ANSI_GREEN + "[PASS]" + Colors.ANSI_CYAN + " INSERÇÃO\n");
//
//        // BUSCA CHAVE PRIMÁRIA NO ARQUIVO
//        System.out.println(Colors.ANSI_YELLOW + "[TESTING]" + Colors.ANSI_CYAN + " BUSCA CHAVE PRIMÁRIA");
//
//        System.out.print("\tBuscando ID 1...");
//        Assert.assertEquals("usuário inserido errado", user1.toString(), database.read(1).toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tBuscando ID 2...");
//        Assert.assertEquals("usuário inserido errado", user2.toString(), database.read(2).toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tBuscando ID 3...");
//        Assert.assertEquals("usuário inserido errado", user3.toString(), database.read(3).toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tBuscando ID 404...");
//        Assert.assertNull("usuário não foi inserido", database.read(404));
//        System.out.println(Colors.ANSI_GREEN + "OK[NÃO ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.println(Colors.ANSI_GREEN + "[PASS]" + Colors.ANSI_CYAN + " BUSCA CHAVE PRIMÁRIA\n");
//
//        // BUSCA CHAVE SECUNDÁRIA NO ARQUIVO
//        System.out.println(Colors.ANSI_YELLOW + "[TESTING]" + Colors.ANSI_CYAN + " BUSCA CHAVE SECUNDÁRIA");
//
//        System.out.print("\tBuscando User batatinha...");
//        Assert.assertEquals("usuário inserido errado", user1.toString(), database.read("batatinha").toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tBuscando User coxinha...");
//        Assert.assertEquals("usuário inserido errado", user2.toString(), database.read("coxinha").toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tBuscando User sucrilhos...");
//        Assert.assertEquals("usuário inserido errado", user3.toString(), database.read("sucrilhos").toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tBuscando User doritos404...");
//        Assert.assertNull("usuário não foi inserido", database.read("doritos404"));
//        System.out.println(Colors.ANSI_GREEN + "OK[NÃO ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.println(Colors.ANSI_GREEN + "[PASS]" + Colors.ANSI_CYAN + " BUSCA CHAVE SECUNDÁRIA\n");
//
//        // DELEÇÃO
//        System.out.println(Colors.ANSI_YELLOW + "[TESTING]" + Colors.ANSI_CYAN + " DELEÇÃO");
//
//        System.out.print("\tDeletando ID 2...");
//        Assert.assertTrue("falha na deleção do usuário", database.delete(2));
//        System.out.println(Colors.ANSI_GREEN + "OK" + Colors.ANSI_CYAN);
//
//        System.out.print("\tBuscando ID 1...");
//        Assert.assertEquals("usuário com ID = 1 está errado", user1.toString(), database.read(1).toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tBuscando ID 2...");
//        Assert.assertNull("usuário com ID = 2 não foi deletado", database.read(2));
//        System.out.println(Colors.ANSI_GREEN + "OK[NÃO ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tBuscando User coxinha...");
//        Assert.assertNull("usuário com User = coxinha não foi deletado", database.read("coxinha"));
//        System.out.println(Colors.ANSI_GREEN + "OK[NÃO ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tBuscando ID 3...");
//        Assert.assertEquals("usuário com ID = 3 está errado", user3.toString(), database.read(3).toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.println(Colors.ANSI_GREEN + "[PASS]" + Colors.ANSI_CYAN + " DELEÇÃO\n");
//
//        // REINSERÇÃO NO ARQUIVO
//        System.out.println(Colors.ANSI_YELLOW + "[TESTING]" + Colors.ANSI_CYAN + " REINSERÇÃO");
//
//        System.out.print("\tReinserindo user2...");
//        Assert.assertEquals("usuário com ID errado", 4, database.create(user2));
//        System.out.println(Colors.ANSI_GREEN + "OK[ID: " + 4 + "]" + Colors.ANSI_CYAN);
//
//        System.out.print("\tBuscando ID 4...");
//        Assert.assertEquals("usuário com ID = 4 está errado", user2.toString(), database.read(4).toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[ENCONTRADO]" + Colors.ANSI_CYAN);
//
//        System.out.println(Colors.ANSI_GREEN + "[PASS]" + Colors.ANSI_CYAN + " REINSERÇÃO\n");
//
//        // ATUALIZAÇÃO
//        System.out.println(Colors.ANSI_YELLOW + "[TESTING]" + Colors.ANSI_CYAN + " ATUALIZAÇÃO");
//
//        // Novo registro com tamanho igual
//        System.out.println(Colors.ANSI_YELLOW + "\t[TESTING]" + Colors.ANSI_CYAN + " ATUALIZAÇÃO/MESMO_TAMANHO");
//
//        int originalSize = user1.toByteArray().length;
//        System.out.print("\t\tAlterando User: [" + user1.secondaryKey() + "] -> [");
//        user1.setUser("batatinho");
//        System.out.println(user1.secondaryKey() + "]");
//        int newSize = user1.toByteArray().length;
//
//        Assert.assertEquals("o tamanho dos dois registros devem ser iguais", originalSize, newSize);
//
//        System.out.print("\t\tAtualizando ID 1...");
//        Assert.assertTrue("falha na atualização do usuário", database.update(user1));
//        System.out.println(Colors.ANSI_GREEN + "OK" + Colors.ANSI_CYAN);
//
//        System.out.print("\t\tChecando alterações do ID 1...");
//        Assert.assertEquals("o campo user não atualizou", "batatinho", database.read(1).getUser());
//        Assert.assertEquals("o usuário não atualizou", user1.toString(), database.read(1).toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[User: " + database.read(1).getUser() + "]" + Colors.ANSI_CYAN);
//
//        System.out.print("\t\tChecando alterações da chave secundária...");
//        Assert.assertNotNull("a chave secundária não atualizou", database.read("batatinho"));
//        Assert.assertEquals("usuário não associado à chave secundária", user1.toString(), database.read("batatinho").toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[User: " + database.read(1).getUser() + "]" + Colors.ANSI_CYAN);
//
//        System.out.println(Colors.ANSI_GREEN + "\t[PASS]" + Colors.ANSI_CYAN + " ATUALIZAÇÃO/MESMO_TAMANHO\n");
//
//        // Novo registro com tamanho menor
//        System.out.println(Colors.ANSI_YELLOW + "\t[TESTING]" + Colors.ANSI_CYAN + " ATUALIZAÇÃO/TAMANHO_MENOR");
//
//        originalSize = user1.toByteArray().length;
//        System.out.print("\t\tAlterando User: [" + user1.secondaryKey() + "] -> [");
//        user1.setUser("bata");
//        System.out.println(user1.secondaryKey() + "]");
//        newSize = user1.toByteArray().length;
//
//        Assert.assertTrue("o tamanho do novo registro deve ser menor que o original", newSize < originalSize);
//
//        System.out.print("\t\tAtualizando ID 1...");
//        Assert.assertTrue("falha na atualização do usuário", database.update(user1));
//        System.out.println(Colors.ANSI_GREEN + "OK" + Colors.ANSI_CYAN);
//
//        System.out.print("\t\tChecando alterações do ID 1...");
//        Assert.assertEquals("o campo user não atualizou", "bata", database.read(1).getUser());
//        Assert.assertEquals("o usuário não atualizou", user1.toString(), database.read(1).toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[User: " + database.read(1).getUser() + "]" + Colors.ANSI_CYAN);
//
//        System.out.print("\t\tChecando alterações da chave secundária...");
//        Assert.assertNotNull("a chave secundária não atualizou", database.read("bata"));
//        Assert.assertEquals("usuário não associado à chave secundária", user1.toString(), database.read("bata").toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[User: " + database.read(1).getUser() + "]" + Colors.ANSI_CYAN);
//
//        System.out.println(Colors.ANSI_GREEN + "\t[PASS]" + Colors.ANSI_CYAN + " ATUALIZAÇÃO/TAMANHO_MENOR\n");
//
//        // Novo registro com tamanho maior
//        System.out.println(Colors.ANSI_YELLOW + "\t[TESTING]" + Colors.ANSI_CYAN + " ATUALIZAÇÃO/TAMANHO_MAIOR");
//
//        originalSize = user1.toByteArray().length;
//        System.out.print("\t\tAlterando User: [" + user1.secondaryKey() + "] -> [");
//        user1.setUser("batatatatatatatata");
//        System.out.println(user1.secondaryKey() + "]");
//        newSize = user1.toByteArray().length;
//
//        Assert.assertTrue("o tamanho do novo registro deve ser maior que o original", newSize > originalSize);
//
//        System.out.print("\t\tAtualizando ID 1...");
//        Assert.assertTrue("falha na atualização do usuário", database.update(user1));
//        System.out.println(Colors.ANSI_GREEN + "OK" + Colors.ANSI_CYAN);
//
//        System.out.print("\t\tChecando alterações do ID 1...");
//        Assert.assertEquals("o campo user não atualizou", "batatatatatatatata", database.read(1).getUser());
//        Assert.assertEquals("o usuário não atualizou", user1.toString(), database.read(1).toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[User: " + database.read(1).getUser() + "]" + Colors.ANSI_CYAN);
//
//        System.out.print("\t\tChecando alterações da chave secundária...");
//        Assert.assertNotNull("a chave secundária não atualizou", database.read("batatatatatatatata"));
//        Assert.assertEquals("usuário não associado à chave secundária", user1.toString(), database.read("batatatatatatatata").toString());
//        System.out.println(Colors.ANSI_GREEN + "OK[User: " + database.read(1).getUser() + "]" + Colors.ANSI_CYAN);
//
//        System.out.println(Colors.ANSI_GREEN + "\t[PASS]" + Colors.ANSI_CYAN + " ATUALIZAÇÃO/TAMANHO_MAIOR");
//
//        System.out.println(Colors.ANSI_GREEN + "[PASS]" + Colors.ANSI_CYAN + " ATUALIZAÇÃO");
//    }
//}
