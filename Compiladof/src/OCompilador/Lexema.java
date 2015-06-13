/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OCompilador;

/**
 *ggf
 * @author Daniel
 */
public class Lexema {
    // se eh int, float, ...
    private String tipo;
    private String nome;
    private int linha;
    //onde foi acessado pela ultima vez
    private int linhaAtual;
    // se for vetor, matriz. id
    private String novoTipo;

    public Lexema() {
        this.linhaAtual = -1;
        this.novoTipo = "";
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }
    
     /**
     * @return the nome
     */
    public int getLinha() {
        return linha;
    }

    /**
     * @param nome the nome to set
     */
    public void setLinha(int linha) {
        this.linha = linha;
    }

    /**
     * @return the novoTipo
     */
    public String getNovoTipo() {
        return novoTipo;
    }

    /**
     * @param novoTipo the novoTipo to set
     */
    public void setNovoTipo(String novoTipo) {
        this.novoTipo = novoTipo;
    }

    /**
     * @return the linhaAtual
     */
    public int getLinhaAtual() {
        return linhaAtual;
    }

    /**
     * @param linhaAtual the linhaAtual to set
     */
    public void setLinhaAtual(int linhaAtual) {
        this.linhaAtual = linhaAtual;
    }


    
    
}
