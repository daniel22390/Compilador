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

    public boolean naoComparador(Lexema token) {
        if (token.getTipo().equals("sum") || token.getTipo().equals("sub") || token.getTipo().equals("mult") || token.getTipo().equals("div")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean Comparador(Lexema token) {
        if (token.getTipo().equals("gt") || token.getTipo().equals("gte") || token.getTipo().equals("lt") || token.getTipo().equals("lte") || token.getTipo().equals("eq") || token.getTipo().equals("neq") || token.getTipo().equals("or") || token.getTipo().equals("and")) {
            return true;
        } else {
            return false;
        }
    }

    public int AnalisaLaco(int posicao, ArrayList<Lexema> escopo) {
        // criado esse lexema para colocar no final da Arrayls, para saber qdo o escopo acaba
        Lexema acabou = new Lexema();
        acabou.setNome("acabou");
        acabou.setTipo("acabou");
        Stack<Lexema> pilha = new Stack<Lexema>();
        pilha.push(escopo.get(0));
        //envio todo escopo do se para ser analisado
        if (escopo.get(posicao).getTipo().equals("cond")) {
            posicao++;
            boolean senao = false;
            ArrayList<Lexema> escopoSe = new ArrayList<>();
            // le ate achar o fim-se ou senao
            while (true) {
                if (escopo.get(posicao).getTipo().equals("cond")) {
                    pilha.push(escopo.get(posicao));
                } else if (escopo.get(posicao).getTipo().equals("endcond")) {
                    pilha.pop();
                    if (pilha.isEmpty()) {
                        escopoSe.add(acabou);
                        if (senao) {
                            escopoProg(escopoSe, false, false, false);
                        } else {
                            escopoProg(escopoSe, false, true, false);
                        }
                        break;
                    }
                } else if (escopo.get(posicao).getTipo().equals("altcond")) {
                    if (pilha.size() == 1) {
                        senao = true;
                        escopoSe.add(acabou);
                        escopoProg(escopoSe, false, true, false);
                        escopoSe.clear();
                        posicao++;
                        continue;
                    }
                }
                escopoSe.add(escopo.get(posicao));
                posicao++;
            }
            return posicao;
        } //envio todo escopo do enquanto para ser analisado 
        else if (escopo.get(posicao).getTipo().equals("whileloop")) {
            posicao++;
            ArrayList<Lexema> escopoEnquanto = new ArrayList<>();
            while (true) {
                if (escopo.get(posicao).getTipo().equals("whileloop")) {
                    pilha.push(escopo.get(posicao));
                } else if (escopo.get(posicao).getTipo().equals("endwhileloop")) {
                    pilha.pop();
                    if (pilha.isEmpty()) {
                        escopoEnquanto.add(acabou);
                        escopoProg(escopoEnquanto, false, true, false);
                        break;
                    }
                }
                escopoEnquanto.add(escopo.get(posicao));
                posicao++;
            }
            return posicao;
        } //envio todo escopo do para para ser analisado 
        else if (escopo.get(posicao).getTipo().equals("forloop")) {
            posicao++;
            ArrayList<Lexema> escopoPara = new ArrayList<>();
            while (true) {
                if (escopo.get(posicao).getTipo().equals("forloop")) {
                    pilha.push(escopo.get(posicao));
                } else if (escopo.get(posicao).getTipo().equals("endforloop")) {
                    pilha.pop();
                    if (pilha.isEmpty()) {
                        escopoPara.add(acabou);
                        escopoProg(escopoPara, false, false, true);
                        break;
                    }
                }
                escopoPara.add(escopo.get(posicao));
                posicao++;
            }
            return posicao;
        } //envio todo escopo da funcao para ser analisado 
        else if (escopo.get(posicao).getTipo().equals("function")) {
            posicao++;
            ArrayList<Lexema> escopoFuncao = new ArrayList<>();
            while (true) {
                if (escopo.get(posicao).getTipo().equals("function")) {
                    pilha.push(escopo.get(posicao));
                } else if (escopo.get(posicao).getTipo().equals("endfunction")) {
                    pilha.pop();
                    if (pilha.isEmpty()) {
                        escopoFuncao.add(acabou);
                        escopoFuncao(escopoFuncao);
                        break;
                    }
                }
                escopoFuncao.add(escopo.get(posicao));
                posicao++;
            }
            return posicao;
        } else {
            return -1;
        }
    }

    public void escopoFuncao(ArrayList<Lexema> escopo) {
        // nao esquecer de  inserir o tipo do retorno na pilha
        Lexema nomeFun = new Lexema();
        nomeFun.setNome(escopo.get(0).getNome());
        nomeFun.setLinha(escopo.get(0).getLinha());
        nomeFun.setNovoTipo("fun");
        for (ArrayList<Lexema> esc : varEscopo) {
            for (Lexema escopo1 : esc) {
                if (escopo1.getNome().equals(escopo.get(0).getNome())) {
                    System.out.println("Erro: variavel " + escopo.get(0).getNome() + " inicializada como " + escopo1.getNovoTipo() + ". Linha  " + escopo.get(0).getLinha());
                    System.exit(0);
                }
            }
        }
        verificaFuncao(1, escopo);
        varEscopo.peek().add(nomeFun);
        escopoProg(escopo, true, false, false);
    }

    public int verificaFuncao(int posicao, ArrayList<Lexema> tokens) {
        ArrayList<Lexema> newTokens = new ArrayList<>();
        Stack<Lexema> pilha = new Stack<>();
        pilha.push(tokens.get(posicao));
        Lexema barraN = new Lexema();
        barraN.setNome("|n");
        barraN.setTipo("|n");
        posicao++;
        while (true) {
            if (tokens.get(posicao).getTipo().equals("(")) {
                pilha.push(tokens.get(posicao));
                newTokens.add(tokens.get(posicao));
            } else if (tokens.get(posicao).getTipo().equals(")")) {
                pilha.pop();
                if (pilha.isEmpty()) {
                    break;
                }
                newTokens.add(tokens.get(posicao));
            } else if (tokens.get(posicao).getTipo().equals(",")) {
                newTokens.add(barraN);
                verificaTipoVariavel(newTokens);
                newTokens.clear();
            }
            newTokens.add(tokens.get(posicao));
            posicao++;
        }
        return posicao;
    }

    //recebe uma posicao do id do vetor, e envia o token de condicao para verificaTipoVariavel
    public int analisaVetor(int posicao, ArrayList<Lexema> tokens) {
        if (tokens.get(posicao).getTipo().equals("]")) {
            Stack<Lexema> pilha2 = new Stack<>();
            while (true) {
                if (tokens.get(posicao).getTipo().equals("]")) {
                    pilha2.push(tokens.get(posicao));
                } else if (tokens.get(posicao).getTipo().equals("[")) {
                    pilha2.pop();
                    if (pilha2.isEmpty()) {
                        break;
                    }
                }
                posicao--;
            }
            if ((tokens.get(posicao - 1).getTipo().equals("]"))) {
                pilha2.clear();
                pilha2.push(tokens.get(posicao));
                posicao = posicao - 2;
                while (true) {
                    if (tokens.get(posicao).getTipo().equals("]")) {
                        pilha2.push(tokens.get(posicao));
                    } else if (tokens.get(posicao).getTipo().equals("[")) {
                        pilha2.pop();
                        if (pilha2.isEmpty()) {
                            break;
                        }
                    }
                    posicao--;
                }
            }
        }
        int i = posicao;
        Stack<Lexema> pilha = new Stack<>();
        pilha.push(tokens.get(posicao));

        posicao = posicao + 1;
        Lexema barraN = new Lexema();
        barraN.setTipo("|n");
        barraN.setNome("|n");
        ArrayList<Lexema> lista = new ArrayList<>();
        while (true) {
            if (tokens.get(posicao).getTipo().equals("[")) {
                pilha.push(tokens.get(posicao));
            } else if (tokens.get(posicao).getTipo().equals("]")) {
                pilha.pop();
                if (pilha.isEmpty()) {
                    break;
                }
            }
            lista.add(tokens.get(posicao));
            posicao++;
        }
        lista.add(barraN);
        String tipo = verificaTipoVariavel(lista);
        if (!tipo.equals("Int")) {
            System.out.println("Erro: O vetor " + tokens.get(i - 1).getNome() + " nao possui inteiro como posicao. Linha  " + tokens.get(i).getLinha());
            System.exit(0);
        }
        pilha.clear();
        if ((posicao + 1) < tokens.size() && tokens.get(posicao + 1).getTipo().equals("[")) {
            pilha.push(tokens.get(posicao + 1));
            posicao = posicao + 2;
            lista.clear();
            while (true) {
                if (tokens.get(posicao).getTipo().equals("[")) {
                    pilha.push(tokens.get(posicao));
                } else if (tokens.get(posicao).getTipo().equals("]")) {
                    pilha.pop();
                    if (pilha.isEmpty()) {
                        break;
                    }
                }
                lista.add(tokens.get(posicao));
                posicao++;
            }
            lista.add(barraN);
            tipo = verificaTipoVariavel(lista);
            if (!tipo.equals("Int")) {
                System.out.println("Erro: O vetor " + tokens.get(i - 1).getNome() + " nao possui inteiro como posicao. Linha  " + tokens.get(i).getLinha());
                System.exit(0);
            }
        }

        return posicao;
    }

    // fazer com que <, >, ... seja apenas entre condicionadores
    //retorna int, float, booleano(posicao contem a localizacao do id)
    public String verificaTipoVariavel(ArrayList<Lexema> tokens) {
        int i = 0;
        // pego o tipo do primeiro elemento
        String tipoInicial = "";
        boolean achou = false;
        // passo todos os (
        while (tokens.get(i).getTipo().equals("(") || tokens.get(i).getTipo().equals("sub")) {
            i++;
        }
        // se o primeiro elemento for id, ou fun
        if (tokens.get(i).getTipo().equals("id") || tokens.get(i).getTipo().equals("fun")) {
            for (ArrayList<Lexema> lista : varEscopo) {
                for (Lexema lista1 : lista) {
                    if (tokens.get(i).getNome().equals(lista1.getNome())) {
                        if (lista1.getTipo().equals("")) {
                            System.out.println("Erro: variavel" + lista1.getNome() + "ainda nao foi inicializada. Linha  " + tokens.get(i).getLinha());
                            System.exit(0);
                        }
                        if (!lista1.getNovoTipo().equals("fun") && tokens.get(i).getTipo().equals("fun")) {
                            System.out.println("Erro: Tipo de variavel " + tokens.get(i).getNome() + " nao pode ser modificada. Linha  " + tokens.get(i).getLinha());
                            System.exit(0);
                        } else if (!verificaIdentificador(i, tokens).equals(lista1.getNovoTipo())) {
                            System.out.println("Erro: variavel " + tokens.get(i).getNome() + " inicializada como outro Id. Linha  " + tokens.get(i).getLinha());
                            System.exit(0);
                        }
                        tipoInicial = lista1.getTipo();
                        achou = true;
                        break;
                    }
                }
                if (achou == true) {
                    break;
                }
            }
        } // se o primeiro elemento for string
        else if (tokens.get(i).getTipo().equals("String")) {
            tipoInicial = tokens.get(i).getTipo();
        } // se o primeiro elemento for booleano
        else if (tokens.get(i).getTipo().equals("true") || tokens.get(i).getTipo().equals("false")) {
            tipoInicial = "booleano";
        } // se for float ou int, inicializo como num
        else if (tokens.get(i).getTipo().equals("Int") || tokens.get(i).getTipo().equals("Float")) {
            tipoInicial = tokens.get(i).getTipo();
        }
        i++;
        if (tipoInicial.equals("")) {
            System.out.println("Erro: Variavel " + tokens.get(i - 1).getNome() + " nao inicializada. Linha  " + tokens.get(i - 1).getLinha());
            System.exit(0);
        }
        boolean Booleano = false;
        boolean Float = false;
        boolean mais = false;
        // analiso os prozimos tokens
        while (!("|n").equals(tokens.get(i).getTipo())) {
            boolean controlePilha = false;
            // nao esquecer, que tem q excluir todos os parametros de funcao e o que esta dentro das chaves do vetor
            // se for booleano e o tipo inicial nao for booleano, entao dara erro
            if (tokens.get(i).getTipo().equals("true") || tokens.get(i).getTipo().equals("false")) {
                if (!tipoInicial.equals("booleano")) {
                    System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                    System.exit(0);
                }
            }// se for string e o tipo inicial nao for string, entao dara erro 
            else if (tokens.get(i).getTipo().equals("String")) {
                if (!tipoInicial.equals("String") && !(mais && (tipoInicial.equals("Int") || tipoInicial.equals("Float")))) {
                    System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                    System.exit(0);
                } else if (mais && (tipoInicial.equals("Int") || tipoInicial.equals("Float"))) {
                    tipoInicial = "String";
                }
            }// se for int e o tipo inicial nao for int ou float, entao dara erro 
            else if (tokens.get(i).getTipo().equals("Int")) {
                if (!tipoInicial.equals("Int") && !tipoInicial.equals("Float") && !(mais && (tipoInicial.equals("String")))) {
                    System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                    System.exit(0);
                } else if (mais && (tipoInicial.equals("String"))) {
                    tipoInicial = "String";
                }
            }// se for float e o tipo inicial nao for int ou float, entao dara erro 
            else if (tokens.get(i).getTipo().equals("Float")) {
                if (!tipoInicial.equals("Int") && !tipoInicial.equals("Float") && !(mais && (tipoInicial.equals("String")))) {
                    System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                    System.exit(0);
                } else if (mais && (tipoInicial.equals("String"))) {
                    tipoInicial = "String";
                } else {
                    tipoInicial = "Float";
                }
            }//se for id ou fun, verifica o tipo delas na tabela de escopo, se for diferente de tipoInicial, entao sera erro
            else if (tokens.get(i).getTipo().equals("id") || tokens.get(i).getTipo().equals("fun")) {
                for (ArrayList<Lexema> varEsc : varEscopo) {
                    for (Lexema varEsc1 : varEsc) {
                        // se for int e o tipo inicial nao for int ou float, entao erro
                        if (!varEsc1.getNovoTipo().equals("fun") && tokens.get(i).getTipo().equals("fun")) {
                            System.out.println("Erro: Tipo de variavel " + tokens.get(i).getNome() + " nao pode ser modificada. Linha  " + tokens.get(i).getLinha());
                            System.exit(0);
                        } else if (tokens.get(i).getNome().equals(varEsc1.getNome())) {
                            if (varEsc1.getTipo().equals("")) {
                                System.out.println("Erro: variavel" + varEsc1.getNome() + "ainda nao foi inicializada. Linha  " + tokens.get(i).getLinha());
                                System.exit(0);
                            } else if (varEsc1.getTipo().equals("Int")) {
                                if (!tipoInicial.equals("Int") && !tipoInicial.equals("Float") && !(mais && (tipoInicial.equals("String")))) {
                                    System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                                    System.exit(0);
                                } else if (mais && (tipoInicial.equals("String"))) {
                                    tipoInicial = "String";
                                }
                            }// se for float e o tipo inicial nnao for int ou float, entao erro
                            else if (varEsc1.getTipo().equals("Float")) {
                                if (!tipoInicial.equals("Int") && !tipoInicial.equals("Float") && !tipoInicial.equals("Float") && !(mais && (tipoInicial.equals("String")))) {
                                    System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                                    System.exit(0);
                                } else if (mais && (tipoInicial.equals("String"))) {
                                    tipoInicial = "String";
                                } else {
                                    tipoInicial = "Float";
                                }
                            } else if (varEsc1.getTipo().equals("String")) {
                                if (!tipoInicial.equals("String") && !(mais && (tipoInicial.equals("Int") || tipoInicial.equals("Float")))) {
                                    System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                                    System.exit(0);
                                } else if (mais && (tipoInicial.equals("Int") || tipoInicial.equals("Float"))) {
                                    tipoInicial = "String";
                                }
                            } // se nao for nem int nem float, entao erro 
                            else if (!varEsc1.getTipo().equals(tipoInicial)) {
                                System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                                System.exit(0);
                            }
                            if (!verificaIdentificador(i, tokens).equals(varEsc1.getNovoTipo())) {
                                System.out.println("Erro: variavel " + tokens.get(i).getNome() + " inicializada como outro id. Linha  " + tokens.get(i).getLinha());
                                System.exit(0);
                            }
                            controlePilha = true;
                            break;
                        }
                    }
                    if (controlePilha) {
                        break;
                    }
                }
                if (controlePilha == false) {
                    System.out.println("Erro: variavel " + tokens.get(i).getNome() + " nao inicializada. Linha  " + tokens.get(i).getLinha());
                    System.exit(0);
                }
                controlePilha = false;
            } else if (tokens.get(i).getTipo().equals("[")) {
                i = analisaVetor(i, tokens);
            } // se for um comparador, entao seto como booleano
            else if (Comparador(tokens.get(i))) {
                Booleano = true;
                mais = false;
            } else if (tokens.get(i).getTipo().equals("div")) {
                Float = true;
                mais = false;
            } else if (tokens.get(i).getTipo().equals("(") && (i - 1) >= 0 && tokens.get(i - 1).getTipo().equals("fun")) {
                i = verificaFuncao(i, tokens);
            } else if (tokens.get(i).getTipo().equals("sum")) {
                mais = true;
            } else if (tokens.get(i).getTipo().equals("sub") || tokens.get(i).getTipo().equals("mult")) {
                mais = false;
            }
            i++;
        } // se for booleano, retorno booleano
        if (Booleano == true) {
            return "booleano";
        } else if (Float == true) {
            return "Float";
        } else {
            return tipoInicial;
        }
    }

    //retorna vetor, matriz, id, fun(posicao contem a localizacao do id, o ] do vetor, ou id[), retorna vetor, matriz, id, fun
    public String verificaIdentificador(int posicao, ArrayList<Lexema> tokens) {
        // se é o final de um vetor
        if (tokens.get(posicao).getTipo().equals("]")) {
            while (!tokens.get(posicao).getNome().equals("[")) {
                posicao--;
            }
            posicao--;
            if (tokens.get(posicao).getTipo().equals("]")) {
                return "matriz";
            } else {
                return "vetor";
            }
        } // se ler um id
        else {
            // retorna vetor ou matriz
            if (tokens.get(posicao + 1).getTipo().equals("[")) {
                posicao++;
                while (!tokens.get(posicao).getTipo().equals("]")) {
                    posicao++;
                }
                posicao++;
                if (tokens.get(posicao).getTipo().equals("[")) {
                    return "matriz";
                } else {
                    return "vetor";
                }
            } //retorna funcao
            else if (tokens.get(posicao).getTipo().equals("fun")) {
                return "fun";
            } //retorna id
            else {
                return "id";
            }
        }
    }

    // retorna o nome do vetor ou matriz (é passado a localização do ] final)
    public String nomeVetor(int posicao, ArrayList<Lexema> tokens) {
        while (!tokens.get(posicao).getNome().equals("[")) {
            posicao--;
        }
        posicao--;
        if (tokens.get(posicao).getTipo().equals("]")) {
            while (!tokens.get(posicao).getNome().equals("[")) {
                posicao--;
            }
            posicao--;
        }
        return tokens.get(posicao).getNome();
    }

    public void escopoProg(ArrayList<Lexema> escopo, boolean isFuncao, boolean isLaco, boolean isFor) {
        ArrayList<Lexema> varProg = new ArrayList<>();
        ArrayList<Lexema> decVetProg = new ArrayList<>();
        // empilho o escopo na pilha de escopos
        varEscopo.push(varProg);
        decVetorEscopo.push(decVetProg);
        int i = 0;
        if (isLaco) {
            ArrayList<Lexema> condicao = new ArrayList<>();
            Lexema BarranN = new Lexema();
            BarranN.setNome("|n");
            BarranN.setTipo("|n");
            for (i = 0; !escopo.get(i).getTipo().equals("initcond") && !escopo.get(i).getTipo().equals("initforloop"); i++) {
                condicao.add(escopo.get(i));
            }
            condicao.add(BarranN);
            if (!verificaTipoVariavel(condicao).equals("booleano")) {
                System.out.println("Erro: Condição nao é booleana. Linha  " + escopo.get(i).getLinha());
                System.exit(0);
            }
        } else if (isFor) {
            boolean criada = false;
            for (ArrayList<Lexema> var : varEscopo) {
                for (Lexema var1 : var) {
                    if (var1.getNome().equals(escopo.get(0).getNome())) {
                        criada = true;
                        if (!var1.getTipo().equals("Int")) {
                            System.out.println("Erro: Variavel " + escopo.get(0).getNome() + " nao foi inicializada como inteiro. Linha  " + escopo.get(0).getLinha());
                            System.exit(0);
                        }
                    }
                }
            }
            if (!criada) {
                Lexema idFor = new Lexema();
                idFor.setNome(escopo.get(0).getNome());
                idFor.setLinha(escopo.get(0).getLinha());
                idFor.setNovoTipo("id");
                idFor.setTipo("Int");
                varEscopo.peek().add(idFor);
            }
            i++;
        }
        while (i < escopo.size()) {
            //Se achar um = ou vetor
            if (escopo.get(i).getTipo().equals("atrib") || escopo.get(i).getTipo().equals("vet")) {
                //se achar =
                if (escopo.get(i).getTipo().equals("atrib")) {
                    if (escopo.get(i - 1).getTipo().equals("id") || escopo.get(i - 1).getTipo().equals("]")) {
                        String nomeVet = "";
                        if (escopo.get(i - 1).getTipo().equals("]")) {
                            nomeVet = nomeVetor(i - 1, escopo);
                            analisaVetor(i - 1, escopo);
                        }
                        boolean declarada = false;
                        ArrayList<Lexema> tipoVar = new ArrayList<>();
                        for (int j = (i + 1); !escopo.get(j).getTipo().equals("|n"); j++) {
                            tipoVar.add(escopo.get(j));
                        }
                        Lexema barraN = new Lexema();
                        barraN.setTipo("|n");
                        barraN.setNome("|n");
                        tipoVar.add(barraN);
                        // verifico o tipo da variavel
                        String tipo = verificaTipoVariavel(tipoVar);
                        //verifico o id da variavel (vetor, matriz, id...)
                        String identificador = verificaIdentificador(i - 1, escopo);
                        // Procura na tabela de lexemas
                        boolean achou = false;
                        // procuro na tabela de variaveis do escopo
                        for (ArrayList<Lexema> varEscopo1 : varEscopo) {
                            for (Lexema lexema : varEscopo1) {
                                if (identificador.equals("vetor") || identificador.equals("matriz")) {
                                    if (lexema.getNome().equals(nomeVet)) {
                                        declarada = true;
                                        if ((lexema.getTipo().equals("Int") || lexema.getTipo().equals("Float")) && !(tipo.equals("Int") || tipo.equals("Float"))) {
                                            System.out.println("Erro: Variavel " + lexema.getNome() + " inicializada como " + lexema.getTipo() + ". Linha  " + escopo.get(i).getLinha());
                                            System.exit(0);
                                        } else if (!lexema.getTipo().equals(tipo) && !((lexema.getTipo().equals("Int") || (lexema.getTipo().equals("Float"))))) {
                                            System.out.println("Erro: Variavel " + lexema.getNome() + " inicializada como " + lexema.getTipo() + ". Linha  " + escopo.get(i).getLinha());
                                            System.exit(0);
                                        } else if (lexema.getTipo().equals("Int") && tipo.equals("Float")) {
                                            System.out.println("Fazer cast");
                                        }
                                        //se o tipo(matriz, vetor, id) forem diferentes
                                        if (!lexema.getNovoTipo().equals(identificador) && !lexema.getTipo().equals("fun")) {
                                            System.out.println("Erro: " + identificador + " " + nomeVet + " não pode ser modificada. Linha  " + escopo.get(i).getLinha());
                                            System.exit(0);
                                        } // senao atualizo a linha onde foi chamado a ultima vez 
                                        else {
                                            lexema.setLinhaAtual(i);
                                            achou = true;
                                            break;
                                        }
                                    }
                                } else if (lexema.getNome().equals(escopo.get(i - 1).getNome())) {
                                    declarada = true;
                                    if ((lexema.getTipo().equals("Int") || lexema.getTipo().equals("Float")) && !(tipo.equals("Int") || tipo.equals("Float"))) {
                                        System.out.println("Erro: Variavel " + lexema.getNome() + " inicializada como " + lexema.getTipo() + ". Linha  " + escopo.get(i).getLinha());
                                        System.exit(0);
                                    } else if (!lexema.getTipo().equals(tipo) && !lexema.getNovoTipo().equals("fun") && !((lexema.getTipo().equals("Int") || (lexema.getTipo().equals("Float"))))) {
                                        System.out.println("Erro: Variavel " + lexema.getNome() + " inicializada como " + lexema.getTipo() + ". Linha  " + escopo.get(i).getLinha());
                                        System.exit(0);
                                    } else if (lexema.getTipo().equals("Int") && tipo.equals("Float")) {
                                        System.out.println("Fazer cast");
                                    }
                                    //se o tipo(matriz, vetor, id) forem diferentes
                                    if (!lexema.getNovoTipo().equals(identificador) && !(lexema.getNovoTipo().equals("fun") && lexema.getTipo().equals(""))) {
                                        System.out.println("Erro: " + identificador + " " + escopo.get(i - 1).getNome() + " não pode ser modificada. Linha  " + escopo.get(i).getLinha());
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
                        //se for um vetor, verifica se ele foi declarado
                        boolean foiDeclarado = false;
                        String nome = "";
                        if (identificador.equals("vetor") || identificador.equals("matriz")) {
                            nome = nomeVetor(i - 1, escopo);
                        }
                        //procuro na tabela de vetores declarados
                        for (ArrayList<Lexema> decVetEsc : decVetorEscopo) {
                            for (Lexema decVetEsc1 : decVetEsc) {
                                if (decVetEsc1.getNome().equals(escopo.get(i - 1).getNome()) || decVetEsc1.getNome().equals(nome)) {
                                    foiDeclarado = true;
                                    //se os tipos forem diferentes
                                    if ((decVetEsc1.getNovoTipo().equals("matriz") && identificador.equals("vetor")) || (decVetEsc1.getNovoTipo().equals("vetor") && identificador.equals("matriz"))) {
                                        System.out.println("Erro: Variavel " + nome + " nao foi declarada como " + identificador + ". Linha  " + escopo.get(i).getLinha());
                                        System.exit(0);
                                    } else if (!decVetEsc1.getNovoTipo().equals(identificador)) {
                                        System.out.println("Erro: Variavel " + escopo.get(i - 1).getNome() + " nao foi declarada como " + identificador + ". Linha  " + escopo.get(i).getLinha());
                                        System.exit(0);
                                    }
                                }
                            }
                        }
                        if ((identificador.equals("vetor") || identificador.equals("matriz")) && foiDeclarado == false) {
                            System.out.println("Variavel " + nomeVet + " nao foi declarada. Linha  " + escopo.get(i).getLinha());
                            System.exit(0);
                        }
                        //}
                        // se nao encontrar, insiro na lista do escopo
                        if (declarada == false) {
                            Lexema var = new Lexema();
                            if (escopo.get(i - 1).getNome().equals("]")) {
                                var.setNome(nomeVet);
                            } else {
                                var.setNome(escopo.get(i - 1).getNome());
                            }
                            var.setLinha(escopo.get(i).getLinha());
                            var.setNovoTipo(identificador);
                            var.setTipo(tipo);
                            varEscopo.peek().add(var);
                        }
                        if (escopo.get(i - 1).getTipo().equals("id") && isFuncao) {
                            for (ArrayList<Lexema> Var1 : varEscopo) {
                                for (Lexema Var11 : Var1) {
                                    if (Var11.getNome().equals(escopo.get(i - 1).getNome()) && Var11.getNovoTipo().equals("fun")) {
                                        Var11.setTipo(tipo);
                                    }
                                }
                            }
                        }
                    }
                } //quando acha uma declaração de vetor 
                else {
                    boolean vetDeclarado = false;
                    // procuro na tabela de vetores declarados
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
                    for (ArrayList<Lexema> array : varEscopo) {
                        for (Lexema array1 : array) {
                            if (array1.getNome().equals(escopo.get(i + 1).getNome())) {
                                System.out.println("Erro: variavel " + array1.getNome() + " ja foi inicializada. Linha: " + escopo.get(i + 1).getLinha());
                                System.exit(0);
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
                        decVetorEscopo.peek().add(declaracao);
                        //i = analisaVetor(i, escopo);
                    }
                }
            } // se encontrar um laço 
            else if (IsLaco(escopo.get(i))) {
                i = AnalisaLaco(i, escopo);
            } // se encontrar o token acabou, entao desempilha o ultimo arraylist da tabela de escopo 
            else if (escopo.get(i).getTipo().equals("acabou")) {
                System.out.println("Escopo nao-principal: ");
                for (ArrayList<Lexema> decVetProg1 : varEscopo) {
                    for (Lexema decVetProg11 : decVetProg1) {
                        System.out.println(decVetProg11.getNome() + " :" + decVetProg11.getTipo());
                    }
                }
                if (isFuncao) {
                    for (ArrayList<Lexema> var : varEscopo) {
                        for (Lexema var1 : var) {
                            if (var1.getNome().equals(escopo.get(0).getNome())) {
                                if (var1.getTipo().equals("")) {
                                    System.out.println("Erro: Funcao nao possui retorno. Linha  " + escopo.get(0).getLinha());
                                    System.exit(0);
                                }
                            }
                        }
                    }
                }
                varEscopo.pop();
                decVetorEscopo.pop();
            }
            i++;
        }

//        System.out.println("Escopo Principal: ");
//        for (ArrayList<Lexema> decVetProg1 : varEscopo) {
//            for (Lexema decVetProg11 : decVetProg1) {
//                System.out.println(decVetProg11.getNome() + " :" + decVetProg11.getTipo());
//            }
//        }
    }

    public void Analisa() {
        ArrayList<Lexema> escopoProg = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : lexemas.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Lexema> value = entrySet.getValue();
            for (Lexema value1 : value) {
                escopoProg.add(value1);
                if (value1.getNome().equals("fim")) {
                    escopoProg(escopoProg, false, false, false);
                }
            }
        }
    }
}
