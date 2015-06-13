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
    // todas as variaveis visiveis no escopo
    Stack<ArrayList<Lexema>> varEscopo = new Stack<>();
    // todas as declaracoes de vetores que podem ser acessadas no escopo
    Stack<ArrayList<Lexema>> decVetorEscopo = new Stack<>();
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
        //envio todo escopo do se para ser analisado
        if (escopo.get(posicao).getTipo().equals("cond")) {
            ArrayList<Lexema> escopoSe = new ArrayList<>();
            // le ate achar o fim-se ou senao
            while ((!escopo.get(posicao).getTipo().equals("endcond")) && (!escopo.get(posicao).getTipo().equals("altcond"))) {
                posicao++;
                escopoSe.add(escopo.get(posicao));
            }
            escopoProg(escopoSe);
            escopoSe.clear();
            // se for senao, entao le ate achar fim-se
            if (escopo.get(posicao).getTipo().equals("altcond")) {
                while (!escopo.get(posicao).getTipo().equals("endcond")) {
                    posicao++;
                    escopoSe.add(escopo.get(posicao));
                }
                escopoProg(escopoSe);
            }
            return posicao;
        } //envio todo escopo do enquanto para ser analisado 
        else if (escopo.get(posicao).getTipo().equals("whileloop")) {
            ArrayList<Lexema> escopoEnquanto = new ArrayList<>();
            while (!escopo.get(posicao).getTipo().equals("endwhileloop")) {
                posicao++;
                escopoEnquanto.add(escopo.get(posicao));
            }
            escopoProg(escopoEnquanto);
            return posicao;
        } //envio todo escopo do para para ser analisado 
        else if (escopo.get(posicao).getTipo().equals("forloop")) {
            ArrayList<Lexema> escopoPara = new ArrayList<>();
            while (!escopo.get(posicao).getTipo().equals("endforloop")) {
                posicao++;
                escopoPara.add(escopo.get(posicao));
            }
            escopoProg(escopoPara);
            return posicao;
        } //envio todo escopo da funcao para ser analisado 
        else if (escopo.get(posicao).getTipo().equals("function")) {
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

    //retorna int, float, booleano(posicao contem a localizacao do id)
    public String verificaTipoVariavel(int posicao, ArrayList<Lexema> tokens) {
        return "";
    }

    //retorna vetor, matriz, id, fun(posicao contem a localizacao do id, o ] do vetor, ou id[), retorna vetor, matriz, id, fun
    public String verificaIdentificador(int posicao, ArrayList<Lexema> tokens) {
        return "";
    }

    // retorna o nome do vetor ou matriz (é passado a localização do ] final)
    public String nomeVetor(int posicao, ArrayList<Lexema> tokens) {
        return "";
    }

    public void escopoProg(ArrayList<Lexema> escopo) {
        ArrayList<Lexema> varProg = new ArrayList<>();
        ArrayList<Lexema> decVetProg = new ArrayList<>();
        for (int i = 0; i < escopo.size(); i++) {
            //Se achar um = ou vetor
            if (escopo.get(i).getTipo().equals("atrib") || escopo.get(i).getTipo().equals("vet")) {
                //se achar =
                if (escopo.get(i).getTipo().equals("atrib")) {
                    if (escopo.get(i - 1).getTipo().equals("id") || escopo.get(i - 1).getTipo().equals("]")) {
                        boolean declarada = false;
                        // verifico o tipo da variavel
                        String tipo = verificaTipoVariavel(i - 1, escopo);
                        //verifico o id da variavel (vetor, matriz, id...)
                        String identificador = verificaIdentificador(i - 1, escopo);
                        // Procura na tabela de lexemas do bloco
                        for (int j = 0; j < varProg.size(); j++) {
                            if (varProg.get(j).getNome().equals(escopo.get(i - 1).getNome())) {
                                declarada = true;
                                //se o tipo(int, float, booleano, ...) forem diferentes
                                if (!varProg.get(j).getTipo().equals(tipo)) {
                                    System.out.println("Erro: Tipo não aceito para " + escopo.get(i - 1).getNome() + " na linha " + escopo.get(i).getLinha());
                                    System.exit(0);
                                } //se o tipo(matriz, vetor, id) forem diferentes
                                else if (!(varProg.get(j).getNovoTipo().equals(identificador))) {
                                    System.out.println("Erro: " + identificador + " " + escopo.get(i - 1).getNome() + " não pode ser modificada na linha " + escopo.get(i).getLinha());
                                    System.exit(0);
                                } // senao atualizo a linha onde foi chamado a ultima vez
                                else {
                                    varProg.get(j).setLinhaAtual(i);
                                    break;
                                }
                            }
                        }
                        //se nao encontrar, procuro na pilha de lexemas onde estao todos os tokens declarados ate o bloco ser chamado
                        if (declarada == false) {
                            boolean achou = false;
                            for (ArrayList<Lexema> varEscopo1 : varEscopo) {
                                for (Lexema lexema : varEscopo1) {
                                    if (lexema.getNome().equals(escopo.get(i - 1).getNome())) {
                                        declarada = true;
                                        //se o tipo(int, float, booleano, ...) forem diferentes                     
                                        if (!lexema.getTipo().equals(tipo)) {
                                            System.out.println("Erro: Tipo não aceito para " + escopo.get(i - 1).getNome() + " na linha " + escopo.get(i).getLinha());
                                            System.exit(0);
                                        } //se o tipo(matriz, vetor, id) forem diferentes
                                        else if (!lexema.getNovoTipo().equals(identificador)) {
                                            System.out.println("Erro: " + identificador + " " + escopo.get(i - 1).getNome() + " não pode ser modificada na linha " + escopo.get(i).getLinha());
                                            System.exit(0);
                                        } // senao atualizo a linha onde foi chamado a ultima vez 
                                        else {
                                            lexema.setLinhaAtual(i);
                                            achou = true;
                                            break;
                                        }
                                    }
                                }
                                if (achou == true) {
                                    break;
                                }
                            }
                        }
                        //se for um vetor, verifica se ele foi declarado
                        if (identificador.equals("vetor")) {
                            boolean foiDeclarado = false;
                            String nome = nomeVetor(i - 1, escopo);
                            //procuro na tabela de vetores declarados do escopo
                            for (Lexema decVetProg1 : decVetProg) {
                                if (decVetProg1.getNome().equals(nome)) {
                                    foiDeclarado = true;
                                    // se os tipos(vet, matriz) forem diferentes, nao foi declarada como akele tipo
                                    if (!decVetProg1.getNovoTipo().equals(identificador)) {
                                        System.out.println("Erro: Variavel " + nome + "nao foi declarada como " + identificador + "na linha " + escopo.get(i).getLinha());
                                        System.exit(0);
                                    }
                                }
                            }
                            //procuro na tabela de vetores declarados do programa visiveis
                            if (foiDeclarado == false) {
                                for (ArrayList<Lexema> decVetEsc : decVetorEscopo) {
                                    for (Lexema decVetEsc1 : decVetEsc) {
                                        if (decVetEsc1.getNome().equals(nome)) {
                                            foiDeclarado = true;
                                            //se os tipos forem diferentes
                                            if (!decVetEsc1.getNovoTipo().equals(identificador)) {
                                                System.out.println("Erro: Variavel " + nome + "nao foi declarada como " + identificador + "na linha " + escopo.get(i).getLinha());
                                                System.exit(0);
                                            }
                                        }
                                    }
                                }
                            }
                            if (foiDeclarado == false) {
                                System.out.println("Erro: " + identificador + " " + nome + " nao foi declarado na linha " + escopo.get(i).getLinha());
                                System.exit(0);
                            }
                        }
                        // se nao encontrar, insiro na lista de lexemas do escopo
                        if (declarada == false) {
                            Lexema var = new Lexema();
                            var.setNome(escopo.get(i - 1).getNome());
                            var.setLinha(escopo.get(i).getLinha());
                            var.setNovoTipo(identificador);
                            var.setTipo(tipo);
                            varProg.add(var);
                        }
                    }
                } //quando acha uma declaração de vetor 
                else {
                    boolean vetDeclarado = false;
                    for (int j = 0; j < decVetProg.size(); j++) {
                        // se o vetor ja foi declarado na lista de declaracoes do escopo
                        if (decVetProg.get(j).getNome().equals(escopo.get(i + 1).getNome())) {
                            vetDeclarado = true;
                            System.out.println("Erro: variavel ja foi declarada. linha " + escopo.get(i).getLinha());
                            System.exit(0);
                        }
                    }
                    if (vetDeclarado == false) {
                        for (ArrayList<Lexema> decVetEsc : decVetorEscopo) {
                            for (Lexema decVetEsc1 : decVetEsc) {
                                // se o vetor ja foi declarado na lista de declaracoes visiveis
                                if (decVetEsc1.getNome().equals(escopo.get(i + 1).getNome())) {
                                    vetDeclarado = true;
                                    System.out.println("Erro: variavel ja foi declarada. linha " + escopo.get(i).getLinha());
                                    System.exit(0);
                                }
                            }
                        }
                    }
                    //se o vetor nao foi declarado, eu o insiro na lista de declaracoes de vetor do escopo
                    if (vetDeclarado == false) {
                        String nomeVet = verificaIdentificador(i + 1, escopo);
                        Lexema declaracao = new Lexema();
                        declaracao.setNome(escopo.get(i + 1).getNome());
                        declaracao.setLinha(escopo.get(i).getLinha());
                        declaracao.setNovoTipo(nomeVet);
                        decVetProg.add(declaracao);
                    }
                }
            } // se encontrar um laço 
            else if (IsLaco(escopo.get(i))) {
                //empilho as variaveis do escopo
                varEscopo.push(varProg);
                decVetorEscopo.push(decVetProg);
                i = AnalisaLaco(i, escopo);
            } // se encontrar um Id ou uma funcao
            else if ((IsId(escopo.get(i)) && !((i + 1) < escopo.size() && escopo.get(i + 1).getTipo().equals("atrib"))) || (escopo.get(i).getTipo().equals("fun"))) {
                boolean encontrou = false;
                //procuro na tabela do escopo
                String identificador = verificaIdentificador(i, escopo);
                for (int j = 0; j < varProg.size(); j++) {
                    if (escopo.get(i).getNome().equals(varProg.get(i).getNome())) {
                        encontrou = true;
                        //se o tipo(matriz, vetor, id) forem diferentes
                        if (!(varProg.get(j).getNovoTipo().equals(identificador))) {
                            System.out.println("Erro: Variavel " + escopo.get(i).getNome() + " declarada como " + varProg.get(j).getNovoTipo() + " na linha " + escopo.get(i).getLinha());
                            System.exit(0);
                        }
                        //coloco onde foi acessado a ultima vez
                        varProg.get(j).setLinhaAtual(i);
                    }
                }
                //procuro na tabela de variaveis visiveis no escopo
                if (encontrou == false) {
                    for (ArrayList<Lexema> varEscopo1 : varEscopo) {
                        for (Lexema varEscopo11 : varEscopo1) {
                            if (varEscopo11.getNome().equals(escopo.get(i).getNome())) {
                                encontrou = true;
                                //se o tipo(matriz, vetor, id) forem diferentes
                                if (!(varEscopo11.getNovoTipo().equals(identificador))) {
                                    System.out.println("Erro: Variavel " + escopo.get(i).getNome() + " declarada como " + varEscopo11.getNovoTipo() + " na linha " + escopo.get(i).getLinha());
                                    System.exit(0);
                                }
                                //coloco onde foi acessado a ultima vez
                                varEscopo11.setLinhaAtual(i);
                            }
                        }
                    }
                }
                // se não achou, entao nao foi declarada
                if (encontrou == false) {
                    System.out.println("Erro: Variável " + escopo.get(i).getNome() + "nao inicializada na linha " + escopo.get(i).getLinha());
                    System.exit(0);
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
