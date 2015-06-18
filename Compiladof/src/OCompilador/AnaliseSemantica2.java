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

public class AnaliseSemantica2 {

    LinkedHashMap<Integer, ArrayList<Lexema>> lexemas = new LinkedHashMap<>();
    ArrayList<ArvoreBinaria> listaArvores = new ArrayList<>();
    Stack<ArrayList<Lexema>> varEscopo = new Stack<>();
    Lexema fonte;

    public AnaliseSemantica2(ArrayList<ArvoreBinaria> arvore, LinkedHashMap<Integer, ArrayList<Lexema>> listao) {
        this.listaArvores = arvore;
        this.lexemas = listao;
    }

    public ArvoreBinaria<Lexema> pesquisaArvore(int k) {
        return listaArvores.get(k);
    }

    public String analisaAtribuicao(ArvoreBinaria<Lexema> arvore, String tipo) {
        if (arvore != null) {
            Lexema nodo = arvore.getNodo();
            if (nodo.getTipo().equals("Int")) {
                if (tipo.equals("Float")) {
                    System.out.println("Fazer cast");
                } else if (tipo.equals("")) {
                    tipo = nodo.getTipo();
                } else {
                    System.out.println("Erro: tipos diferentes para " + fonte.getNome() + " na linha " + fonte.getLinha());
                }
            } else if (nodo.getTipo().equals("Float")) {
                if (tipo.equals("Int")) {
                    System.out.println("Fazer cast");
                } else if (tipo.equals("")) {
                    tipo = nodo.getTipo();
                } else {
                    System.out.println("Erro: tipos diferentes para " + fonte.getNome() + " na linha " + fonte.getLinha());
                }
            } else if (nodo.getTipo().equals("true") || nodo.getTipo().equals("false")) {
                if (tipo.equals("")) {
                    tipo = nodo.getTipo();
                } else {
                    System.out.println("Erro: tipos diferentes para " + fonte.getNome() + " na linha " + fonte.getLinha());
                }
            } else if (nodo.getTipo().equals("String")) {
                if (tipo.equals("")) {
                    tipo = nodo.getTipo();
                } else {
                    System.out.println("Erro: tipos diferentes para " + fonte.getNome() + " na linha " + fonte.getLinha());
                }
            }
            analisaAtribuicao(arvore.getEsq(), tipo);
            analisaAtribuicao(arvore.getDir(), tipo);
        }
        return tipo;
    }

    public void analisaArvore(ArvoreBinaria<Lexema> arvore) {
        fonte = arvore.getEsq().getNodo();
        boolean declarado = false;
        String tipo = analisaAtribuicao(arvore.getDir(), "");
        for (ArrayList<Lexema> variaveis : varEscopo) {
            for (Lexema variavel : variaveis) {
                declarado = true;
                if (variavel.getNome().equals(fonte.getNome())) {
                    variavel.setLinhaAtual(fonte.getLinha());
                    break;
                }
            }
            if (declarado == true) {
                break;
            }
        }
        if (declarado == false) {
            fonte.setNovoTipo(tipo);
            varEscopo.peek().add(fonte);
        }
    }

    public void escopo(ArrayList<Lexema> escopo) {
        ArrayList<Lexema> var = new ArrayList<Lexema>();
        varEscopo.push(var);
        for (Lexema var1 : escopo) {
        }
        for (ArrayList<Lexema> escopo1 : varEscopo) {
            for (Lexema escopo11 : escopo1) {
                System.out.println(escopo11.getNome() + " " + escopo11.getNovoTipo());
            }
        }
    }

    public void Analisa() {
        ArrayList<Lexema> escopoProg = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : lexemas.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Lexema> value = entrySet.getValue();
            for (Lexema value1 : value) {
                escopoProg.add(value1);
                if (value1.getNome().equals("fim")) {
                    escopo(escopoProg);
                }
            }
        }
    }
}
