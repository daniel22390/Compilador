
package OCompilador;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Daniel
 */
public class OCompilador {

    public static void main(String[] args) throws IOException {
        Scanner leia = new Scanner(System.in);
        //System.out.println("Digite o nome do arquivo: ");
        //String nome = leia.next();
        String nome = "teste.txt";
        AnaliseLexica analisador = new AnaliseLexica(nome);
        analisador.Analisar();
    }
}
