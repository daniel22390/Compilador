/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OCompilador;

/**
 *
 * @author Daniel
 */

public class MensagemErro implements Comparable<MensagemErro> {

    private String erro;
    public int linha;

    /**
     * @return the erro
     */
    

    public String getErro() {
        return erro;
    }

    /**
     * @param erro the erro to set
     */
    public void setErro(String erro) {
        this.erro = erro;
    }

    /**
     * @return the linha
     */
    public int getLinha() {
        return linha;
    }

    /**
     * @param linha the linha to set
     */
    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int compareTo(MensagemErro outraMens) {
        if (this.linha < outraMens.linha) {
            return -1;
        }
        if (this.linha > outraMens.linha) {
            return 1;
        }
        return 0;
    }
}
