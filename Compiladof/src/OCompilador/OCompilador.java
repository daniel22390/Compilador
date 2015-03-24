/*---------- O Senhor é o meu Pastor e nada me faltará. Sal(23:1)-------------*/
package OCompilador;

import java.io.IOException;

/**
 *
 * @author Daniel
 */
public class OCompilador {

    public static void main(String[] args) throws IOException {

        AnaliseLexica analisador = new AnaliseLexica("teste.txt");
        analisador.Analisar();
        
 
        //Escrevendo em arquivo txt
//        try (FileWriter arq = new FileWriter("teste2.txt")) {
//            PrintWriter gravarArq = new PrintWriter(arq);
//
//            for (String token1 : tokens) {
//                gravarArq.println(token1);
//            }
//        }

    }
}
