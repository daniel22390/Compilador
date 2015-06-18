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
        //envio todo escopo do se para ser analisado
        if (escopo.get(posicao).getTipo().equals("cond")) {
            ArrayList<Lexema> escopoSe = new ArrayList<>();
            // le ate achar o fim-se ou senao
            while ((!escopo.get(posicao).getTipo().equals("endcond")) && (!escopo.get(posicao).getTipo().equals("altcond"))) {
                posicao++;
                escopoSe.add(escopo.get(posicao));
            }
            escopoSe.add(acabou);
            escopoProg(escopoSe);
            escopoSe.clear();
            // se for senao, entao le ate achar fim-se
            if (escopo.get(posicao).getTipo().equals("altcond")) {
                while (!escopo.get(posicao).getTipo().equals("endcond")) {
                    posicao++;
                    escopoSe.add(escopo.get(posicao));
                }
                escopoSe.add(acabou);
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
            escopoEnquanto.add(acabou);
            escopoProg(escopoEnquanto);
            return posicao;
        } //envio todo escopo do para para ser analisado 
        else if (escopo.get(posicao).getTipo().equals("forloop")) {
            ArrayList<Lexema> escopoPara = new ArrayList<>();
            while (!escopo.get(posicao).getTipo().equals("endforloop")) {
                posicao++;
                escopoPara.add(escopo.get(posicao));
            }
            escopoPara.add(acabou);
            escopoProg(escopoPara);
            return posicao;
        } //envio todo escopo da funcao para ser analisado 
        else if (escopo.get(posicao).getTipo().equals("function")) {
            ArrayList<Lexema> escopoFuncao = new ArrayList<>();
            while (!escopo.get(posicao).getTipo().equals("endfunction")) {
                posicao++;
                escopoFuncao.add(escopo.get(posicao));
            }
            escopoFuncao.add(acabou);
            escopoFuncao(escopoFuncao);
            return posicao;
        } else {
            return -1;
        }
    }

    public void escopoFuncao(ArrayList<Lexema> escopo) {
        // nao esquecer de  inserir o tipo do retorno na pilha
    }

    public int verificaFuncao(int posicao, ArrayList<Lexema> tokens) {
        ArrayList<Lexema> newTokens = new ArrayList<>();
        Stack<Lexema> pilha = new Stack<>();
        Lexema barraN = new Lexema();
        int posicaoVirgula;
        while (true) {
            if (tokens.get(posicao).getTipo().equals("(")) {
                pilha.push(tokens.get(posicao));
                if (pilha.size() > 1) {
                    newTokens.add(tokens.get(posicao));
                }
            } else if (tokens.get(posicao).getTipo().equals(")")) {
                pilha.pop();
                if (pilha.isEmpty()) {
                    break;
                }
                newTokens.add(tokens.get(posicao));
            } else if (tokens.get(posicao).getTipo().equals(",")) {
                newTokens.add(barraN);
                verificaTipoVariavel(newTokens);
            }
            posicao++;
        }
        return (posicao + 1);
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
            pilha2.clear();
            if ((tokens.get(posicao - 1).getTipo().equals("]"))) {
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
            System.out.println("Erro: O vetor " + tokens.get(i - 1).getNome() + " nao possui inteiro como posicao na linha " + tokens.get(i).getLinha());
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
                System.out.println("Erro: O vetor " + tokens.get(i-1).getNome() + " nao possui inteiro como posicao na linha " + tokens.get(i).getLinha());
                System.exit(0);
            }
            posicao++;
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
        while (tokens.get(i).getTipo().equals("(")) {
            i++;
        }
        // se o primeiro elemento for id, ou fun
        if (tokens.get(i).getTipo().equals("id") || tokens.get(i).getTipo().equals("fun")) {
            for (ArrayList<Lexema> lista : varEscopo) {
                for (Lexema lista1 : lista) {
                    if (tokens.get(i).getNome().equals(lista1.getNome())) {
                        if (lista1.getTipo().equals("")) {
                            System.out.println("Erro: variavel" + lista1.getNome() + "ainda nao foi inicializada na linha " + tokens.get(i).getLinha());
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
            System.out.println("Erro: Variavel " + tokens.get(i - 1).getNome() + " nao inicializada na linha " + tokens.get(i).getLinha());
            System.exit(0);
        }
        boolean Booleano = false;
        boolean Float = false;
        // analiso os prozimos tokens
        while (!tokens.get(i).getTipo().equals("|n")) {
            boolean controlePilha = false;
            // nao esquecer, que tem q excluir todos os parametros de funcao e o que esta dentro das chaves do vetor
            // se for booleano e o tipo inicial nao for booleano, entao dara erro
            if (tokens.get(i).getTipo().equals("true") || tokens.get(i).getTipo().equals("false")) {
                if (!tipoInicial.equals("booleano")) {
                    System.out.println("Erro: Tipos diferentes na linha " + tokens.get(i).getLinha());
                    System.exit(0);
                }
            }// se for string e o tipo inicial nao for string, entao dara erro 
            else if (tokens.get(i).getTipo().equals("String")) {
                if (!tipoInicial.equals("String")) {
                    System.out.println("Erro: Tipos diferentes na linha " + tokens.get(i).getLinha());
                    System.exit(0);
                }
            }// se for int e o tipo inicial nao for int ou float, entao dara erro 
            else if (tokens.get(i).getTipo().equals("Int")) {
                if (!tipoInicial.equals("Int") && !tipoInicial.equals("Float")) {
                    System.out.println("Erro: Tipos diferentes na linha " + tokens.get(i).getLinha());
                    System.exit(0);
                }
            }// se for float e o tipo inicial nao for int ou float, entao dara erro 
            else if (tokens.get(i).getTipo().equals("Float")) {
                if (!tipoInicial.equals("Int") && !tipoInicial.equals("Float")) {
                    System.out.println("Erro: Tipos diferentes na linha " + tokens.get(i).getLinha());
                    System.exit(0);
                }
                tipoInicial = "Float";
            }//se for id ou fun, verifica o tipo delas na tabela de escopo, se for diferente de tipoInicial, entao sera erro
            else if (tokens.get(i).getTipo().equals("id") || tokens.get(i).getTipo().equals("fun")) {
                for (ArrayList<Lexema> varEsc : varEscopo) {
                    for (Lexema varEsc1 : varEsc) {
                        // se for int e o tipo inicial nao for int ou float, entao erro
                        if (tokens.get(i).getNome().equals(varEsc1.getNome())) {
                            if (varEsc1.getTipo().equals("")) {
                                System.out.println("Erro: variavel" + varEsc1.getNome() + "ainda nao foi inicializada na linha " + tokens.get(i).getLinha());
                                System.exit(0);
                            } else if (varEsc1.getTipo().equals("Int")) {
                                if (!tipoInicial.equals("Int") && !tipoInicial.equals("Float")) {
                                    System.out.println("Erro: Tipos diferentes na linha " + tokens.get(i).getLinha());
                                    System.exit(0);
                                }
                            }// se for float e o tipo inicial nnao for int ou float, entao erro
                            else if (varEsc1.getTipo().equals("Float")) {
                                if (!tipoInicial.equals("Int") && !tipoInicial.equals("Float")) {
                                    System.out.println("Erro: Tipos diferentes na linha " + tokens.get(i).getLinha());
                                    System.exit(0);
                                }
                                tipoInicial = "Float";
                            }// se nao for nem int nem float, entao erro 
                            else if (!varEsc1.getTipo().equals(tipoInicial)) {
                                System.out.println("Erro: Tipos diferentes na linha " + tokens.get(i).getLinha());
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
                    System.out.println("Erro: variavel " + tokens.get(i).getNome() + " nao inicializada na linha " + tokens.get(i).getLinha());
                    System.exit(0);
                }
                controlePilha = false;
            } else if (tokens.get(i).getTipo().equals("[")) {
                i = analisaVetor(i, tokens);
            } // se for um comparador, entao seto como booleano
            else if (Comparador(tokens.get(i))) {
                Booleano = true;
            } else if (tokens.get(i).getTipo().equals("div")) {
                Float = true;
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

    public void escopoProg(ArrayList<Lexema> escopo) {
        ArrayList<Lexema> varProg = new ArrayList<>();
        ArrayList<Lexema> decVetProg = new ArrayList<>();
        // empilho o escopo na pilha de escopos
        varEscopo.push(varProg);
        decVetorEscopo.push(decVetProg);
        for (int i = 0; i < escopo.size(); i++) {
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
                                            System.out.println("Erro: Variavel " + lexema.getNome() + " inicializada como " + lexema.getTipo() + " na linha " + escopo.get(i).getLinha());
                                            System.exit(0);
                                        } else if (!lexema.getTipo().equals(tipo) && !((lexema.getTipo().equals("Int") || (lexema.getTipo().equals("Float"))))) {
                                            System.out.println("Erro: Variavel " + lexema.getNome() + " inicializada como " + lexema.getTipo() + " na linha " + escopo.get(i).getLinha());
                                            System.exit(0);
                                        } else if (lexema.getTipo().equals("Int") && tipo.equals("Float")) {
                                            System.out.println("Fazer cast");
                                        }
                                        //se o tipo(matriz, vetor, id) forem diferentes
                                        if (!lexema.getNovoTipo().equals(identificador)) {
                                            System.out.println("Erro: " + identificador + " " + nomeVet + " não pode ser modificada na linha " + escopo.get(i).getLinha());
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
                                        System.out.println("Erro: Variavel " + lexema.getNome() + " inicializada como " + lexema.getTipo() + " na linha " + escopo.get(i).getLinha());
                                        System.exit(0);
                                    } else if (!lexema.getTipo().equals(tipo) && !((lexema.getTipo().equals("Int") || (lexema.getTipo().equals("Float"))))) {
                                        System.out.println("Erro: Variavel " + lexema.getNome() + " inicializada como " + lexema.getTipo() + " na linha " + escopo.get(i).getLinha());
                                        System.exit(0);
                                    } else if (lexema.getTipo().equals("Int") && tipo.equals("Float")) {
                                        System.out.println("Fazer cast");
                                    }
                                    //se o tipo(matriz, vetor, id) forem diferentes
                                    if (!lexema.getNovoTipo().equals(identificador)) {
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
                                        System.out.println("Erro: Variavel " + nome + " nao foi declarada como " + identificador + " na linha " + escopo.get(i).getLinha());
                                        System.exit(0);
                                    } else if (!decVetEsc1.getNovoTipo().equals(identificador)) {
                                        System.out.println("Erro: Variavel " + escopo.get(i - 1).getNome() + " nao foi declarada como " + identificador + " na linha " + escopo.get(i).getLinha());
                                        System.exit(0);
                                    }
                                }
                            }
                        }
                        if ((identificador.equals("vetor") || identificador.equals("matriz")) && foiDeclarado == false) {
                            System.out.println("Variavel " + nomeVet + " nao foi declarada na linha " + escopo.get(i).getLinha());
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
                varEscopo.pop();
                decVetorEscopo.pop();
            }
        }
        System.out.println("Escopo: ");
        for (ArrayList<Lexema> decVetProg1 : varEscopo) {
            for (Lexema decVetProg11 : decVetProg1) {
                System.out.println(decVetProg11.getNome() + " :" + decVetProg11.getTipo());
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
