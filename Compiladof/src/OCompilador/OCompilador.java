/*---------- O Senhor é o meu Pastor e nada me faltará. Sal(23:1)-------------*/
package OCompilador;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

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
