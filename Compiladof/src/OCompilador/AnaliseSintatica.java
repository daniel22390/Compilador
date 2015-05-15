/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OCompilador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import OCompilador.ArvoreBinaria;

/**
 *
 * @author Daniel
 */
public class AnaliseSintatica {

    LinkedHashMap<Integer, ArrayList<Lexema>> lexemas;
    Stack<String> pilha = new Stack<>();
    ArrayList<Lexema> comandos = new ArrayList<Lexema>();
    ArvoreBinaria arvore = new ArvoreBinaria();
    Lexema l = null;
    int cont = 0;

    public AnaliseSintatica(LinkedHashMap<Integer, ArrayList<Lexema>> listao) {
        this.lexemas = listao;
    }

    public boolean verificaCondicao(ArrayList<Lexema> token) {
        return true;
    }

    //Analisa comandos
    public void verificaComandos(ArrayList<Lexema> token) {
        ArrayList<Lexema> tokencond = new ArrayList<Lexema>();
        ArrayList<Lexema> tokencomandos = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenEnquanto = new ArrayList<Lexema>();
        boolean eCondicao = false;
        //percorre array com comandos
        for (int i = 0; i < token.size(); i++) {
            //verifica o comando se
            if (token.get(i).getTipo().equals("cond")) {
                //le ate achar um entao
                i++;
                while ((i < token.size()) && (!token.get(i).getTipo().equals("altcond"))) {
                    tokencond.add(token.get(i));
                    i++;
                }
                verificaCondicao(tokencond);
                i++;
                //le ate axar o fim-se
                while ((i < token.size()) && (!token.get(i).getTipo().equals("endcond"))) {
                    tokencomandos.add(token.get(i));
                    i++;
                }
                verificaComandos(tokencomandos);
                i++;
            }
            
            //le ate achar um enquanto
            else if(token.get(i).getTipo().equals("whileloop")){
                i++;
                while ((i < token.size()) && (!token.get(i).getTipo().equals("initforloop"))) {
                    tokenEnquanto.add(token.get(i));
                    i++;
                }
                verificaCondicao(tokenEnquanto);
                i++;
            }
        }
    }

    public void Analisa() {
        pilha.push("programa");
        for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : lexemas.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Lexema> value = entrySet.getValue();
            for (Lexema value1 : value) {
                if (value1.getNome().equals("fim")) {
                    verificaComandos(comandos);
                } else {
                    comandos.add(value1);
                    cont++;
                }
            }
        }

    }
}
