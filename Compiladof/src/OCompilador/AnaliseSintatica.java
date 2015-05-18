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
    ArrayList<Lexema> comandos = new ArrayList<Lexema>();
    ArvoreBinaria arvore = new ArvoreBinaria();
    Lexema l = null;
    int cont = 0;

    public AnaliseSintatica(LinkedHashMap<Integer, ArrayList<Lexema>> listao) {
        this.lexemas = listao;
    }

    public void verificaParametro(ArrayList<Lexema> token) {

    }

    public void verificaCondicao(ArrayList<Lexema> token) {
        ArrayList<Lexema> tokenExpr = new ArrayList<Lexema>();
        for (int i = 0; i < token.size(); i++) {
            
            
        }
    }

    //Analisa comandos
    public void verificaComandos(ArrayList<Lexema> token) {
        ArrayList<Lexema> tokencond = new ArrayList<Lexema>();
        ArrayList<Lexema> tokencomandos = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenEnquanto = new ArrayList<Lexema>();
        Stack<Lexema> pilha = new Stack<>();

        //percorre array com comandos
        for (int i = 0; i < token.size(); i++) {
            //verifica o comando se
            if (token.get(i).getTipo().equals("cond")) {
                //le ate achar um entao
                pilha.clear();
                pilha.push(token.get(i));
                i++;
                while ((i < token.size()) && (!token.get(i).getTipo().equals("initcond"))) {
                    if (!token.get(i).getTipo().equals("|n")) {
                        tokencond.add(token.get(i));
                    }
                    i++;
                }
                verificaCondicao(tokencond);
                tokencond.clear();
                i++;
                //le ate axar o fim-se
                while ((i < token.size())) {
                    if (!token.get(i).getTipo().equals("|n")) {
                        if (token.get(i).getTipo().equals("cond")) {
                            pilha.push(token.get(i));
                        } else if (token.get(i).getTipo().equals("endcond")) {
                            pilha.pop();
                            if (pilha.isEmpty()) {
                                break;
                            }
                        }
                        tokencomandos.add(token.get(i));
                    }
                    i++;
                }
                if (!pilha.isEmpty()) {
                    System.out.println("Erro: faltou fim-se");
                }
                verificaComandos(tokencomandos);
                tokencomandos.clear();
            } //le ate achar um enquanto
            else if (token.get(i).getTipo().equals("whileloop")) {
                pilha.clear();
                pilha.push(token.get(i));
                i++;
                //le ate axar um faça
                while ((i < token.size()) && (!token.get(i).getTipo().equals("initforloop"))) {
                    if (!token.get(i).getTipo().equals("|n")) {
                        tokenEnquanto.add(token.get(i));
                    }
                    i++;
                }
                verificaCondicao(tokenEnquanto);
                tokenEnquanto.clear();
                i++;
                //le ate axar o fim-enquanto
                while ((i < token.size())) {
                    if (!token.get(i).getTipo().equals("|n")) {
                        if (token.get(i).getTipo().equals("whileloop")) {
                            pilha.push(token.get(i));
                        } else if (token.get(i).getTipo().equals("endwhileloop")) {
                            pilha.pop();
                            if (pilha.isEmpty()) {
                                break;
                            }
                        }
                        tokencomandos.add(token.get(i));
                    }
                    i++;
                }
                if (!pilha.isEmpty()) {
                    System.out.println("Erro: faltou fim-enquanto");
                }
                verificaComandos(tokencomandos);
                tokencomandos.clear();
            } //le ate achar um para
            else if (token.get(i).getTipo().equals("forloop")) {
                pilha.clear();
                pilha.push(token.get(i));
                i++;
                //le se proximo elemento é um id
                if ((i) >= token.size() || !token.get(i).getTipo().equals("id")) {
                    System.out.println("Erro: faltou id");
                } else {
                    i++;
                    //le se proximo elemento é um "de"
                    if ((i) >= token.size() || !token.get(i).getTipo().equals("rng1forloop")) {
                        System.out.println("Erro: faltou de");
                    } else {
                        i++;
                        //le se proximo elemento é um inteiro
                        if ((i) >= token.size() || !token.get(i).getTipo().equals("Int")) {
                            System.out.println("Erro: tipo nao inteiro");
                        } else {
                            i++;
                            //le se proximo elemento é "até"
                            if ((i) >= token.size() || !token.get(i).getTipo().equals("rng2forloop")) {
                                System.out.println("Erro: faltou até");
                            } else {
                                i++;
                                //le se o próximo é um inteiro
                                if ((i) >= token.size() || !token.get(i).getTipo().equals("Int")) {
                                    System.out.println("Erro: tipo nao inteiro");
                                } else {
                                    i++;
                                    //le se o próximo é um faça
                                    if ((i) >= token.size() || !token.get(i).getTipo().equals("initforloop")) {
                                        System.out.println("Erro: nao iniciou para");
                                    } else {
                                        i++;
                                        //le ate axar um fim-para
                                        while ((i < token.size())) {
                                            if (!token.get(i).getTipo().equals("|n")) {
                                                if (token.get(i).getTipo().equals("forloop")) {
                                                    pilha.push(token.get(i));
                                                } else if (token.get(i).getTipo().equals("endforloop")) {
                                                    pilha.pop();
                                                    if (pilha.isEmpty()) {
                                                        break;
                                                    }
                                                }
                                                tokenEnquanto.add(token.get(i));
                                            }
                                            i++;
                                        }
                                        if (!pilha.isEmpty()) {
                                            System.out.println("Erro: faltou fim-para");
                                        }
                                        verificaComandos(tokenEnquanto);
                                        tokenEnquanto.clear();
                                    }
                                }
                            }
                        }
                    }
                }
            } //se ler um id
            else if (token.get(i).getTipo().equals("id")) {
                i++;
                //analisa se é um vetor
                if ((i) < token.size() && token.get(i).getTipo().equals("[")) {
                    i++;
                    while ((i < token.size()) && !("|n").equals(token.get(i).getTipo()) && !("]").equals(token.get(i).getTipo())) {
                        tokenEnquanto.add(token.get(i));
                        i++;
                    }
                    if ((i >= token.size()) || token.get(i).getTipo().equals("|n")) {
                        System.out.println("Erro: token esperado ']'");
                    }
                    verificaCondicao(tokenEnquanto);
                    tokenEnquanto.clear();

                    i++;
                    //analisa se é uma matriz
                    if ((i < token.size()) && token.get(i).getTipo().equals("[")) {
                        while ((i < token.size()) && !token.get(i).getTipo().equals("|n") && !token.get(i).getTipo().equals("]")) {
                            tokenEnquanto.add(token.get(i));
                            i++;
                        }
                        if ((i >= token.size()) || token.get(i).getTipo().equals("|n")) {
                            System.out.println("Erro: token esperado ']'");
                        }
                        verificaCondicao(tokenEnquanto);
                        tokenEnquanto.clear();
                        i++;
                        if ((i) >= token.size() || !token.get(i).getTipo().equals("atrib")) {
                            System.out.println("Erro: nao contem atribuição");
                        } else {
                            i++;
                            //le ate axar um \n
                            while ((i < token.size()) && (!token.get(i).getTipo().equals("|n"))) {
                                tokenEnquanto.add(token.get(i));
                                i++;
                            }
                            verificaCondicao(tokenEnquanto);
                            tokenEnquanto.clear();
                        }
                    }

                } //le se proximo elemento é um =
                else if ((i) >= token.size() || !token.get(i).getTipo().equals("atrib")) {
                    System.out.println("Erro: nao contem atribuição");
                } else {
                    i++;
                    //le ate axar um \n
                    while ((i < token.size()) && (!token.get(i).getTipo().equals("|n"))) {
                        tokenEnquanto.add(token.get(i));
                        i++;
                    }
                    verificaCondicao(tokenEnquanto);
                    tokenEnquanto.clear();
                }
            } //Analisa se é uma função
            else if (token.get(i).getTipo().equals("function")) {
                pilha.clear();
                i++;
                //le se o proximo é um fun
                if ((i) >= token.size() || !token.get(i).getTipo().equals("fun")) {
                    System.out.println("Erro: erro em parametros");
                } else {
                    i++;
                    if ((i) >= token.size() || !token.get(i).getTipo().equals("(")) {
                        System.out.println("Erro");
                    } else {
                        pilha.push(token.get(i));
                        i++;
                        while (!token.get(i).getTipo().equals("|n")) {
                            if (token.get(i).getTipo().equals("(")) {
                                pilha.push(token.get(i));
                            } else if (token.get(i).getTipo().equals(")")) {
                                pilha.pop();
                                if (pilha.isEmpty()) {
                                    break;
                                }
                            }
                            tokenEnquanto.add(token.get(i));
                            i++;
                        }
                        if (!pilha.isEmpty()) {
                            System.out.println("Erro");
                        }
                        pilha.clear();
                        if (i < token.size()) {
                            pilha.push(token.get(i));
                        }
                        i++;
                        verificaParametro(tokenEnquanto);
                        tokenEnquanto.clear();

                        while ((i < token.size())) {
                            if (!token.get(i).getTipo().equals("|n")) {
                                if (token.get(i).getTipo().equals("function")) {
                                    pilha.push(token.get(i));
                                } else if (token.get(i).getTipo().equals("endfunction")) {
                                    pilha.pop();
                                    if (pilha.isEmpty()) {
                                        break;
                                    }
                                }
                                tokenEnquanto.add(token.get(i));
                            }
                            i++;
                        }
                        if (!pilha.isEmpty()) {
                            System.out.println("Erro: faltou fim-funcao");
                        }
                        verificaComandos(tokenEnquanto);
                        tokenEnquanto.clear();
                    }
                }
            } //Analisa se eh declaração de vetor
            else if (token.get(i).getTipo().equals("vet")) {
                i++;
                if ((i) < token.size() && token.get(i).getTipo().equals("id")) {
                    i++;
                    //analisa se é uma declaração de vetor
                    if ((i) < token.size() && token.get(i).getTipo().equals("[")) {
                        i++;
                        while ((i < token.size()) && !("|n").equals(token.get(i).getTipo()) && !("]").equals(token.get(i).getTipo())) {
                            tokenEnquanto.add(token.get(i));
                            i++;
                        }
                        if ((i >= token.size()) || token.get(i).getTipo().equals("|n")) {
                            System.out.println("Erro: token esperado ']'");
                        }
                        verificaCondicao(tokenEnquanto);
                        tokenEnquanto.clear();

                        i++;
                        //analisa se é declaração de matriz
                        if ((i < token.size()) && token.get(i).getTipo().equals("[")) {
                            while ((i < token.size()) && !token.get(i).getTipo().equals("|n") && !token.get(i).getTipo().equals("]")) {
                                tokenEnquanto.add(token.get(i));
                                i++;
                            }
                            if ((i >= token.size()) || token.get(i).getTipo().equals("|n")) {
                                System.out.println("Erro: token esperado ']'");
                            }
                            verificaCondicao(tokenEnquanto);
                            tokenEnquanto.clear();

                        }
                        i++;
                        if ((i < token.size()) && token.get(i).getTipo().equals("atrib")) {
                            i++;
                            //le ate axar um \n
                            while ((i < token.size()) && (!token.get(i).getTipo().equals("|n"))) {
                                tokenEnquanto.add(token.get(i));
                                i++;
                            }
                            verificaCondicao(tokenEnquanto);
                            tokenEnquanto.clear();
                        }
                        else if((i < token.size()) && !token.get(i).getTipo().equals("|n")){
                            System.out.println("Erro: Token inesperado");
                        }
                    } else {
                        System.out.println("Erro: token [ esperado");
                    }
                } else {
                    System.out.println("Erro: declaração de vetor sem id");
                }
            } else {
                if (!token.get(i).getTipo().equals("|n")) {
                    System.out.println("Erro: token inesperado");
                }
            }
        }
    }

    public void Analisa() {
        boolean programa = false;
        for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : lexemas.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Lexema> value = entrySet.getValue();
            for (Lexema value1 : value) {
                if (value1.getNome().equals("fim")) {
                    verificaComandos(comandos);
                    programa = true;
                } else {
                    comandos.add(value1);
                }
            }
        }
        if (programa == false) {
            System.out.println("Erro: faltou fim");
        }

    }
}
