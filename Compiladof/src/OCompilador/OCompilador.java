/*---------- O Senhor é o meu Pastor e nada me faltará. Sal(23:1)-------------*/
package OCompilador;

import java.io.IOException;

/**
 *
 * @author Daniel
 */
public class OCompilador {

    public static void main(String[] args) throws IOException {

        AnaliseLexica analisador = new AnaliseLexica("teste2.txt");
        analisador.Analisar();
    }
}
