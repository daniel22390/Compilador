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
    ArrayList<String> bloco = null;
    ArvoreBinaria arvore = new ArvoreBinaria();
    Lexema l = null;

    public AnaliseSintatica(LinkedHashMap<Integer, ArrayList<Lexema>> listao) {
        this.lexemas = listao;
    }
    

    public void Analisa(){
        pilha.push("programa");
        for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : lexemas.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Lexema> value = entrySet.getValue();
            for (Lexema value1 : value) {
                if(value1.getNome().equals("fim")){
                    l = value1;
                    arvore.inserirElemento(l, 0);
                }
            }
        }
        
    }
}
