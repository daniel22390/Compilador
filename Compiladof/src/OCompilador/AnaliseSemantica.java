/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OCompilador;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author Daniel
 */
public class AnaliseSemantica {

    ArrayList<ArvoreBinaria> arvore = new ArrayList<>();
    Stack<ArrayList<Lexema>> varEscopo = new Stack<>();
    LinkedHashMap<Integer, ArrayList<Lexema>> lexemas = new LinkedHashMap<>();

    public AnaliseSemantica(ArrayList<ArvoreBinaria> arvore, LinkedHashMap<Integer, ArrayList<Lexema>> listao) {
        this.arvore = arvore;
        this.lexemas = listao;
    }

    public void MensagemErro(int linha) {
        System.out.println("Erro: variavel nao declarada na linha " + linha);
        System.exit(0);
    }

    public boolean IsId(Lexema token) {
        if (token.getTipo().equals("fun") || (token.getTipo().equals("id") && !token.getNome().equals("fim"))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean IsLaco(Lexema token) {
        if (token.getTipo().equals("cond") || token.getTipo().equals("whileloop") || token.getTipo().equals("forloop") || token.getTipo().equals("function")) {
            return true;
        } else {
            return false;
        }
    }

    public int AnalisaLaco(int posicao, ArrayList<Lexema> escopo) {
        if (escopo.get(posicao).getTipo().equals("cond")) {
            ArrayList<Lexema> escopoSe = new ArrayList<>();
            while (!escopo.get(posicao).getTipo().equals("endcond")) {
                posicao++;
                escopoSe.add(escopo.get(posicao));
            }
            escopoSe(escopoSe);
            return posicao;
        } else if (escopo.get(posicao).getTipo().equals("whileloop")) {
            ArrayList<Lexema> escopoEnquanto = new ArrayList<>();
            while (!escopo.get(posicao).getTipo().equals("endwhileloop")) {
                posicao++;
                escopoEnquanto.add(escopo.get(posicao));
            }
            escopoProg(escopoEnquanto);
            return posicao;
        } else if (escopo.get(posicao).getTipo().equals("forloop")) {
            ArrayList<Lexema> escopoPara = new ArrayList<>();
            while (!escopo.get(posicao).getTipo().equals("endforloop")) {
                posicao++;
                escopoPara.add(escopo.get(posicao));
            }
            escopoProg(escopoPara);
            return posicao;
        } else if (escopo.get(posicao).getTipo().equals("function")) {
            ArrayList<Lexema> escopoFuncao = new ArrayList<>();
            while (!escopo.get(posicao).getTipo().equals("endfunction")) {
                posicao++;
                escopoFuncao.add(escopo.get(posicao));
            }
            escopoFuncao(escopoFuncao);
            return posicao;
        } else {
            return -1;
        }
    }

    public void escopoFuncao(ArrayList<Lexema> escopo) {

    }

    public void escopoSe(ArrayList<Lexema> escopo) {

    }

    public String verificaTipo(int posicao) {
        return "";
    }

    public void escopoProg(ArrayList<Lexema> escopo) {
        ArrayList<Lexema> varProg = new ArrayList<>();
        for (int i = 0; i < escopo.size(); i++) {
            //Se achar um = ou vetor
            if (escopo.get(i).getTipo().equals("atrib") || escopo.get(i).getTipo().equals("vet")) {
                //se achar =
                if (escopo.get(i).getTipo().equals("atrib")) {
                    if (escopo.get(i - 1).getTipo().equals("id")) {
                        boolean declarada = false;
                        String tipo = verificaTipo(i + 1);
                        // Procura na tabela de lexemas do bloco
                        for (int j = 0; j < varProg.size(); j++) {
                            if (varProg.get(j).getNome().equals(escopo.get(i - 1).getNome())) {
                                declarada = true;
                                //se o tipo(matriz, vetor, id) forem diferentes
                                if (!varProg.get(j).getNovoTipo().equals(tipo)) {
                                    System.out.println("Erro: Tipo não aceito para "+escopo.get(i-1).getNome()+" na linha "+escopo.get(i).getLinha());
                                    System.exit(0);
                                } 
                                //se o tipo(int, float, booleano, ...) forem diferentes
                                else if(!(varProg.get(j).getTipo().equals(escopo.get(i - 1).getTipo()))) {
                                    System.out.println("Erro: Variavel " + escopo.get(i - 1).getNome() + " não pode ser modificada na linha " + escopo.get(i).getLinha());
                                    System.exit(0);
                                } 
                                // senao atualizo a linha onde foi chamado a ultima vez
                                else{
                                    varProg.get(j).setLinhaAtual(i);
                                    break;
                                }
                            }
                        }
                        //se nao encontrar, procuro na pilha de lexemas onde estao todos os tokens declarados ate o bloco ser chamado
                        if(declarada == false){
                            //Procura na pilha
                        }
                    }
                } else {
                    //quando for vetor
                }
            } else if (IsLaco(escopo.get(i))) {
                i = AnalisaLaco(i, escopo);
            } else if (IsId(escopo.get(i)) && !((i + 1) < escopo.size() && escopo.get(i + 1).getTipo().equals("atrib"))) {
                // quando encontrar um id
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
                    escopoProg(escopoProg);
                }
            }
        }
    }
}
