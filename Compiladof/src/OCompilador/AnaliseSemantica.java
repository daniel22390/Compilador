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
    Stack<ArrayList<ArvoreBinaria>> varEscopo = new Stack<>();
    Stack<ArrayList<String>> vetEscopo = new Stack<>();
    Stack<ArrayList<String>> matEscopo = new Stack<>();
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

    public void escopoProg(ArrayList<Lexema> escopo) {
        ArrayList<ArvoreBinaria> varProg = new ArrayList<>();
        ArrayList<String> vetProg = new ArrayList<>();
        ArrayList<String> matProg = new ArrayList<>();
        for (int i = 0; i < escopo.size(); i++) {
            if (escopo.get(i).getTipo().equals("atrib") || escopo.get(i).getTipo().equals("vet")) {
                if (escopo.get(i).getTipo().equals("atrib")) {
                    for (ArvoreBinaria<Lexema> arvore1 : arvore) {
                        if (arvore1.getEsq().getNodo().getLinha() == escopo.get(i).getLinha()) {
                            arvore1.getEsq().getNodo().setLinha(i);
                            varProg.add(arvore1);
                        }
                    }
                } else {
                    String vetor = escopo.get(i + 1).getNome();
                    while (!escopo.get(i).getTipo().equals("]")) {
                        i++;
                    }
                    if ((i + 1) < escopo.size() && escopo.get(i + 1).getTipo().equals("[")) {
                        matProg.add(vetor);
                    } else {
                        vetProg.add(vetor);
                    }
                }
            } else if (IsLaco(escopo.get(i))) {
                varEscopo.add(varProg);
                vetEscopo.add(vetProg);
                matEscopo.add(matProg);
                i = AnalisaLaco(i, escopo);
            } else if (IsId(escopo.get(i)) && !((i + 1) < escopo.size() && escopo.get(i + 1).getTipo().equals("atrib"))) {
                boolean declarado = false;
                boolean declaradoVet = false;
                boolean vet = false;
                String id = escopo.get(i).getNome();
                if ((i + 1) < escopo.size() && escopo.get(i + 1).getTipo().equals("[")) {
                    while (!escopo.get(i).getTipo().equals("]")) {
                        i++;
                    }
                    if ((i + 1) < escopo.size() && escopo.get(i + 1).getTipo().equals("[")) {
                        for (String matProg1 : matProg) {
                            if (matProg1.contains(id)) {
                                declaradoVet = true;
                            }
                        }
                        if (declaradoVet == false) {
                            for (ArrayList<String> matEscopo1 : matEscopo) {
                                for (String matEscopo2 : matEscopo1) {
                                    if (id.contains(matEscopo2)) {
                                        declarado = true;
                                    }
                                }
                            }
                        }
                    } else {
                        for (String vetProg1 : vetProg) {
                            if (vetProg1.contains(id)) {
                                declaradoVet = true;
                            }
                        }
                        if (declaradoVet == false) {
                            for (ArrayList<String> vetEscopo1 : vetEscopo) {
                                for (String varEscopo2 : vetEscopo1) {
                                    if (id.contains(varEscopo2)) {
                                        declarado = true;
                                    }
                                }
                            }
                        }
                    }
                    if (declaradoVet == false) {
                        System.out.println("Erro: vetor " + id + " não declarado na linha " + escopo.get(i).getLinha());
                        System.exit(0);
                    }
                }
                if (vet == false) {
                    for (ArvoreBinaria<Lexema> varProg1 : varProg) {
                        if (varProg1.getEsq().getNodo().getNome().equals(id)) {
                            declarado = true;
                            varProg1.getEsq().getNodo().setLinha(i);
                        }
                    }
                    if (declarado == false) {
                        for (ArrayList<ArvoreBinaria> varEscopo1 : varEscopo) {
                            for (ArvoreBinaria<Lexema> varEscopo2 : varEscopo1) {
                                if (varEscopo2.getEsq().getNodo().getNome().equals(id)) {
                                    declarado = true;
                                    varEscopo2.getEsq().getNodo().setLinha(i);
                                }
                            }
                        }
                        if (declarado == false) {
                            System.out.println("Erro: token " + id + " não inicializado na linha " + escopo.get(i).getLinha());
                            System.exit(0);
                        }
                    }
                }
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
