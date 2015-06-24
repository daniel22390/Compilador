/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OCompilador;

import java.util.ArrayList;

/**
 *ggf
 * @author Daniel
 */
public class Lexema {
    // se eh int, float, ...
    private String tipo = "";
    private String nome;
    private String nomeVar;
    private int linha;
    //onde foi acessado pela ultima vez
    private int linhaAtual;
    // se for vetor, matriz. id
    private String novoTipo;
    private ArrayList<Integer> escopo = new ArrayList<>();
    public Lexema() {
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

    /**
     * @return the escopo
     */
    public ArrayList<Integer> getEscopo() {
        return escopo;
    }

    /**
     * @param escopo the escopo to set
     */
    public void setEscopo(ArrayList<Integer> escopo) {
        this.escopo = escopo;
    }

    /**
     * @return the nomeVar
     */
    public String getNomeVar() {
        return nomeVar;
    }

    /**
     * @param nomeVar the nomeVar to set
     */
    public void setNomeVar(String nomeVar) {
        this.nomeVar = nomeVar;
    }


    
    
}
