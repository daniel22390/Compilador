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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Daniel
 */
public class AnaliseSintatica {

    LinkedHashMap<Integer, ArrayList<Lexema>> lexemas;
    ArrayList<Lexema> comandos = new ArrayList<>();
    ArvoreBinaria<Lexema> arvoreCond;
    Lexema l = null;
    int cont = 0;

    static BufferedReader in = new BufferedReader(
            new InputStreamReader(System.in));

    public AnaliseSintatica(LinkedHashMap<Integer, ArrayList<Lexema>> listao) {
        this.lexemas = listao;
    }

    public void verificaParametro(ArrayList<Lexema> token) throws IOException {
        ArrayList<Lexema> tokenCond = new ArrayList<Lexema>();
        boolean isCondicao = false;
        if (token.get(0).getTipo().equals(",")) {
            System.out.println("Erro: token , não esperado na linha " + token.get(0).getLinha());
            in.readLine();
        }
        for (int i = 0; i < token.size(); i++) {
            if ((i + 1) < token.size() && token.get(i).getTipo().equals(",") && token.get(i + 1).getTipo().equals(",")) {
                System.out.println("Erro: era esperado uma condição entre ,, na linha " + token.get(i).getLinha());
                in.readLine();
            }
            if (token.get(i).getTipo().equals(",")) {
                verificaCondicao(tokenCond);
                verificaCondicao2(tokenCond);
                tokenCond.clear();
            } else {
                tokenCond.add(token.get(i));
            }
        }
        if (!token.get(token.size() - 1).getTipo().equals(",")) {
            verificaCondicao(tokenCond);
            verificaCondicao2(tokenCond);
            tokenCond.clear();
        } else {
            System.out.println("Erro: token , não esperado na linha " + token.get(token.size() - 1).getLinha());
            in.readLine();
        }

    }

    public boolean condicoes(Lexema lexema) {
        if (lexema.getTipo().equals("gt") || lexema.getTipo().equals("gte") || lexema.getTipo().equals("lt") || lexema.getTipo().equals("lte") || lexema.getTipo().equals("eq") || lexema.getTipo().equals("neq") || lexema.getTipo().equals("and") || lexema.getTipo().equals("or")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean termos(Lexema lexema) {
        if (lexema.getTipo().equals("id") || lexema.getTipo().equals("vet") || lexema.getTipo().equals("Int") || lexema.getTipo().equals("Float") || lexema.getTipo().equals("true") || lexema.getTipo().equals("false") || lexema.getTipo().equals("String")) {
            return true;
        } else {
            return false;
        }
    }

    public void verificaTermo(ArrayList<Lexema> token) throws IOException {
        ArrayList<Lexema> tokenCond = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenParam = new ArrayList<Lexema>();
        Stack<Lexema> pilha = new Stack<>();
//        for (Lexema pilha1 : token) {
//            System.out.println(pilha1.getNome());
//        }
//        System.out.println("--------");
        if (token.size() > 1) {
            int k;
            if (token.get(0).getTipo().equals("(")) {
                pilha.push(token.get(0));
                for (k = 1; k < token.size(); k++) {
                    if (token.get(k).getTipo().equals(")")) {
                        pilha.pop();
                        if (pilha.isEmpty()) {
                            break;
                        }
                    } else if (token.get(k).getTipo().equals("(")) {
                        pilha.push(token.get(k));
                    }
                    tokenCond.add(token.get(k));
                }
                if (pilha.isEmpty() && k < (token.size() - 1)) {
                    System.out.println("Erro: falta comparador na linha " + token.get(0).getLinha());
                    in.readLine();
                }
                verificaCondicao(tokenCond);
                verificaCondicao2(tokenCond);
                tokenCond.clear();
                pilha.clear();
            } else if (token.get(0).getTipo().equals("fun")) {
                if (token.get(1).getTipo().equals("(")) {
                    pilha.push(token.get(1));
                    for (int i = 2; i < token.size(); i++) {
                        if (token.get(i).getTipo().equals(")")) {
                            pilha.pop();
                            if (pilha.isEmpty()) {
                                break;
                            }
                        } else if (token.get(i).getTipo().equals("(")) {
                            pilha.push(token.get(i));
                        }
                        tokenParam.add(token.get(i));
                    }
                    verificaParametro(tokenParam);
                    tokenParam.clear();
                    pilha.clear();
                }
            } else if (token.get(0).getTipo().equals("id")) {
                //analisa se é uma declaração de vetor
                int i;
                if (token.get(1).getTipo().equals("[")) {
                    for (i = 2; i < token.size() && !token.get(i).getTipo().equals("]"); i++) {
                        tokenCond.add(token.get(i));
                    }
                    if (i >= token.size() || !token.get(i).getNome().equals("]")) {
                        System.out.println("Erro: era esperado ] na linha " + token.get(i - 1).getLinha());
                        in.readLine();
                    }
                    verificaCondicao(tokenCond);
                    verificaCondicao2(tokenCond);
                    tokenCond.clear();
                    if ((i + 1) < token.size() && token.get(i + 1).getTipo().equals("[")) {
                        i = i + 2;
                        while (i < token.size() && !token.get(i).getTipo().equals("]")) {
                            tokenCond.add(token.get(i));
                            i++;
                        }
                        if (i >= token.size() || !token.get(i).getNome().equals("]")) {
                            System.out.println("Erro: era esperado ] na linha " + token.get(i - 1).getLinha());
                            in.readLine();
                        }
                        verificaCondicao(tokenCond);
                        verificaCondicao2(tokenCond);
                        tokenCond.clear();
                    } else if ((i + 1) < token.size() && !token.get(i + 1).getTipo().equals("[")) {
                        System.out.println("Erro: token inesperado na linha " + token.get(i - 1).getLinha());
                        in.readLine();
                    }
                } else {
                    System.out.println("Erro: era esperado [ na linha " + token.get(0).getLinha());
                    in.readLine();
                }
            } else {
                System.out.println("Erro: token inesperado na linha " + token.get(0).getLinha());
                in.readLine();
            }
        } else if (token.size() == 1) {
            if (!termos(token.get(0))) {
                System.out.println("Erro: token " + token.get(0).getNome() + " nao aceito na linha " + token.get(0).getLinha());
                in.readLine();
            }
        }
    }

    public void verificaExpressaoPrec(ArrayList<Lexema> token) throws IOException {
        ArrayList<Lexema> tokenTermo = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenExprPrec = new ArrayList<Lexema>();
        Stack<Lexema> pilha = new Stack<>();
        boolean eTermo = false;
        int posTermo = 0;
//        for (Lexema pilha1 : token) {
//            System.out.println(pilha1.getNome());
//        }
//        System.out.println("-----");
        for (int i = 0; i < token.size(); i++) {
            if (!token.get(i).getTipo().equals("|n")) {
                //empilha
                if (token.get(i).getTipo().equals("(")) {
                    pilha.push(token.get(i));
                } //desempilha
                else if (token.get(i).getTipo().equals(")")) {
                    pilha.pop();
                } //verifica se o *,x,/,: ta no nivel mais para fora
                else if ((token.get(i).getTipo().equals("mult") || token.get(i).getTipo().equals("div")) && pilha.isEmpty()) {
                    eTermo = true;
                    posTermo = i;
                }
            }
        }
        pilha.clear();
        if (eTermo && (posTermo == (token.size() - 1) || posTermo == 0)) {
            System.out.println("Erro: token " + token.get(posTermo).getNome() + " inesperado na linha " + token.get(posTermo).getLinha());
            in.readLine();
        }
        if (eTermo) {
            //antes do operador envia para verificaExpressao
            for (int i = 0; i < posTermo; i++) {
                tokenExprPrec.add(token.get(i));
            }
            verificaExpressao(tokenExprPrec);
            tokenExprPrec.clear();
            //depois do operador envia para verificaTermo
            for (int i = (posTermo + 1); i < token.size(); i++) {
                tokenTermo.add(token.get(i));
            }
            verificaTermo(tokenTermo);
            tokenTermo.clear();
        } //envia td para verificaTermo
        else {
            for (int i = 0; i < token.size(); i++) {
                tokenTermo.add(token.get(i));
            }
            verificaTermo(tokenTermo);
            tokenTermo.clear();
        }
    }

    public void verificaExpressao(ArrayList<Lexema> token) throws IOException {
        ArrayList<Lexema> tokenExpr = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenExprPrec = new ArrayList<Lexema>();
        Stack<Lexema> pilha = new Stack<>();
        boolean eExprPrec = false;
        int posPrec = 0;
//        for (Lexema pilha1 : token) {
//            System.out.println(pilha1.getNome());
//        }
//        System.out.println("------");
        for (int i = 0; i < token.size(); i++) {
            if (!token.get(i).getTipo().equals("|n")) {
                //empilha
                if (token.get(i).getTipo().equals("(")) {
                    pilha.push(token.get(i));
                } //desempilha
                else if (token.get(i).getTipo().equals(")")) {
                    pilha.pop();
                } //verifica se o + ou - ta no nivel mais para fora
                else if ((token.get(i).getTipo().equals("sum") || token.get(i).getTipo().equals("sub")) && pilha.isEmpty()) {
                    eExprPrec = true;
                    posPrec = i;
                }
            }
        }
        pilha.clear();
        if (eExprPrec && (posPrec == (token.size() - 1) || posPrec == 0)) {
            System.out.println("Erro: token " + token.get(posPrec).getNome() + " inesperado na linha " + token.get(posPrec).getLinha());
            in.readLine();
        }
        if (eExprPrec) {
            //antes do operador envia para verificaExpressao
            for (int i = 0; i < posPrec; i++) {
                tokenExpr.add(token.get(i));
            }
            verificaExpressao(tokenExpr);
            tokenExpr.clear();
            //depois do operador envia para verificaExpressaoPrec
            for (int i = (posPrec + 1); i < token.size(); i++) {
                tokenExprPrec.add(token.get(i));
            }
            verificaExpressaoPrec(tokenExprPrec);
            tokenExprPrec.clear();
        } //envia td para ExpressaoPrec
        else {
            for (int i = 0; i < token.size(); i++) {
                tokenExprPrec.add(token.get(i));
            }
            verificaExpressaoPrec(tokenExprPrec);
            tokenExprPrec.clear();
        }
    }

    public void verificaCondicao2(ArrayList<Lexema> token) throws IOException {
        ArrayList<Lexema> tokenExpr = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenCond = new ArrayList<Lexema>();
        Stack<Lexema> pilha = new Stack<>();
        boolean eCondicao = false;
        int posCond = 0;
        int j;
//        for (Lexema pilha1 : token) {
//            System.out.println(pilha1.getNome());
//        }
//        System.out.println("----");
        //Analiso o ArrayList
        for (j = 0; j < token.size(); j++) {
            if (!token.get(j).getTipo().equals("|n")) {
                //empilha
                if (token.get(j).getTipo().equals("(")) {
                    pilha.push(token.get(j));
                } //desempilha
                else if (token.get(j).getTipo().equals(")")) {
                    pilha.pop();
                } //verifica se a condicao ta no nivel mais para fora
                else if (condicoes(token.get(j)) && pilha.isEmpty()) {
                    eCondicao = true;
                    posCond = j;
                }
            }
        }

        if (eCondicao && (posCond == (token.size() - 1) || posCond == 0)) {
            System.out.println("Erro: token " + token.get(posCond).getNome() + " inesperado");
            in.readLine();
        }
        if (eCondicao) {
            //antes da condicao envia para verificaCondicao
            for (int i = 0; i < posCond; i++) {
                tokenCond.add(token.get(i));
            }
            verificaCondicao(tokenCond);
            verificaCondicao2(tokenCond);
            tokenCond.clear();
            //depois da condicao envia para verificaExpressao
            for (int i = (posCond + 1); i < token.size(); i++) {
                tokenExpr.add(token.get(i));
            }
            verificaExpressao(tokenExpr);
            tokenExpr.clear();
        } //envia td para expressao
        else {
            for (int i = 0; i < token.size(); i++) {
                tokenExpr.add(token.get(i));
            }
            verificaExpressao(tokenExpr);
            tokenExpr.clear();
        }
        pilha.clear();
    }

    //Verifica o ArrayList de condicoes
    public void verificaCondicao(ArrayList<Lexema> token) throws IOException {
        ArrayList<Lexema> tokenExpr = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenCond = new ArrayList<Lexema>();
        Stack<Lexema> pilha = new Stack<>();
        boolean empilha = false;
        boolean isCondicao = false;
        int i;
        for (i = 0; i < token.size(); i++) {
            if (!token.get(i).getTipo().equals("|n")) {
                if (condicoes(token.get(i)) && empilha == false) {
                    isCondicao = true;
                    if (i == 0 || i >= (token.size() - 1)) {
                        System.out.println("Erro: comparador " + token.get(i).getNome() + " inesperado na linha " + token.get(i).getLinha());
                        in.readLine();
                    } else {
                        if (token.get(i - 1).getTipo().equals("|n") || token.get(i + 1).getTipo().equals("|n")) {
                            System.out.println("Erro: comparador " + token.get(i).getNome() + " inesperado na linha " + token.get(i).getLinha());
                            in.readLine();
                        }
                    }
                }
                if (empilha == true && !token.get(i).getTipo().equals("(") && !token.get(i).getTipo().equals(")")) {
                    tokenCond.add(token.get(i));
                }
                if (token.get(i).getTipo().equals("(")) {
                    pilha.push(token.get(i));
                    empilha = true;
                }
                if (token.get(i).getTipo().equals(")")) {
                    if (pilha.isEmpty()) {
                        System.out.println("Erro: era esperado ( na linha " + token.get(i).getLinha());
                        in.readLine();
                    } else {
                        pilha.pop();
                        if (pilha.isEmpty()) {
                            verificaCondicao(tokenCond);
                            empilha = false;
                        }
                    }
                }
            }
        }
        tokenCond.clear();
        if (!pilha.isEmpty()) {
            System.out.println("Erro: era esperado ) na linha " + token.get(i - 1).getLinha());
            in.readLine();
        }
        pilha.clear();
    }

    //Analisa comandos
    public void verificaComandos(ArrayList<Lexema> token) throws IOException {
        ArrayList<Lexema> tokencond = new ArrayList<Lexema>();
        ArrayList<Lexema> tokencomandos = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenEnquanto = new ArrayList<Lexema>();
        Stack<Lexema> pilha = new Stack<>();
        boolean eSenao = false;

//        for (Lexema pilha1 : token) {
//            System.out.println(pilha1.getNome());
//        }
//        System.out.println("----");
        //percorre array com comandos
        for (int i = 0; i < token.size(); i++) {
            //verifica o comando se
            if (token.get(i).getTipo().equals("cond")) {
                //le ate achar um entao
                pilha.clear();
                i++;
                while ((i < token.size()) && (!token.get(i).getTipo().equals("initcond")) && (!token.get(i).getNome().equals("|n"))) {
                    if (!token.get(i).getTipo().equals("|n")) {
                        tokencond.add(token.get(i));
                    }
                    i++;
                }
                if (token.get(i).getNome().equals("|n")) {
                    System.out.println("Erro: não contem o então do se na linha " + token.get(i - 1).getLinha());
                    in.readLine();
                }
                verificaCondicao(tokencond);
                verificaCondicao2(tokencond);
                tokencond.clear();
                pilha.push(token.get(i));
                i++;

                //le ate axar o fim-se
                while ((i < token.size())) {
                    if (token.get(i).getTipo().equals("cond")) {
                        pilha.push(token.get(i));
                    } else if (token.get(i).getTipo().equals("endcond")) {
                        pilha.pop();
                        if (pilha.isEmpty()) {
                            break;
                        }
                    } else if (token.get(i).getTipo().equals("altcond")) {
                        if (pilha.size() == 1) {
                            verificaComandos(tokencomandos);
                            tokencomandos.clear();
                            pilha.clear();
                            pilha.push(token.get(i));
                            i++;
                            continue;
                        }
                    }
                    tokencomandos.add(token.get(i));
                    i++;
                }
                if (!pilha.isEmpty()) {
                    System.out.println("Erro: faltou fim-se");
                    in.readLine();
                }
                pilha.clear();
                verificaComandos(tokencomandos);
                tokencomandos.clear();
            } //le ate achar um enquanto
            else if (token.get(i).getTipo().equals("whileloop")) {
                pilha.clear();
                i++;
                //le ate axar um faça
                while ((i < token.size()) && (!token.get(i).getTipo().equals("initforloop")) && (!token.get(i).getNome().equals("|n"))) {
                    if (!token.get(i).getTipo().equals("|n")) {
                        tokenEnquanto.add(token.get(i));
                    }
                    i++;
                }
                if (token.get(i).getNome().equals("|n")) {
                    System.out.println("Erro: não contem o faça do loop na linha " + token.get(i - 1).getLinha());
                    in.readLine();
                }
                verificaCondicao(tokenEnquanto);
                verificaCondicao2(tokenEnquanto);
                tokenEnquanto.clear();
                pilha.push(token.get(i));
                i++;
                //le ate axar o fim-enquanto
                while ((i < token.size())) {
                    if (token.get(i).getTipo().equals("whileloop")) {
                        pilha.push(token.get(i));
                    } else if (token.get(i).getTipo().equals("endwhileloop")) {
                        pilha.pop();
                        if (pilha.isEmpty()) {
                            break;
                        }
                    }
                    tokencomandos.add(token.get(i));
                    i++;
                }
                if (!pilha.isEmpty()) {
                    System.out.println("Erro: faltou fim-enquanto");
                    in.readLine();
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
                    System.out.println("Erro: faltou id na linha " + token.get(i - 1).getLinha());
                    in.readLine();
                } else {
                    i++;
                    //le se proximo elemento é um "de"
                    if ((i) >= token.size() || !token.get(i).getTipo().equals("rng1forloop")) {
                        System.out.println("Erro: faltou token 'de' na linha " + token.get(i - 1).getLinha());
                        in.readLine();
                    } else {
                        i++;
                        //le se proximo elemento é um inteiro
                        if ((i) >= token.size() || !token.get(i).getTipo().equals("Int")) {
                            System.out.println("Erro: tipo nao inteiro no loop para na linha " + token.get(i - 1).getLinha());
                            in.readLine();
                        } else {
                            i++;
                            //le se proximo elemento é "até"
                            if ((i) >= token.size() || !token.get(i).getTipo().equals("rng2forloop")) {
                                System.out.println("Erro: faltou até no loop para na linha " + token.get(i - 1).getLinha());
                                in.readLine();
                            } else {
                                i++;
                                //le se o próximo é um inteiro
                                if ((i) >= token.size() || !token.get(i).getTipo().equals("Int")) {
                                    System.out.println("Erro: tipo nao inteiro no loop para na linha " + token.get(i - 1).getLinha());
                                    in.readLine();
                                } else {
                                    i++;
                                    //le se o próximo é um faça
                                    if ((i) >= token.size() || !token.get(i).getTipo().equals("initforloop")) {
                                        System.out.println("Erro: nao contem token faça na linha " + token.get(i - 1).getLinha());
                                        in.readLine();
                                    } else {
                                        i++;
                                        //le ate axar um fim-para
                                        while ((i < token.size())) {
                                            if (token.get(i).getTipo().equals("forloop")) {
                                                pilha.push(token.get(i));
                                            } else if (token.get(i).getTipo().equals("endforloop")) {
                                                pilha.pop();
                                                if (pilha.isEmpty()) {
                                                    break;
                                                }
                                            }
                                            tokenEnquanto.add(token.get(i));
                                            i++;
                                        }
                                        if (!pilha.isEmpty()) {
                                            System.out.println("Erro: faltou fim-para");
                                            in.readLine();
                                        }
                                        pilha.clear();
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
                        System.out.println("Erro: token esperado ] na linha " + token.get(i - 1).getLinha());
                        in.readLine();
                    }
                    verificaCondicao(tokenEnquanto);
                    verificaCondicao2(tokenEnquanto);
                    tokenEnquanto.clear();

                    i++;
                    //analisa se é uma matriz
                    if ((i < token.size()) && token.get(i).getTipo().equals("[")) {
                        i++;
                        while ((i < token.size()) && !token.get(i).getTipo().equals("|n") && !token.get(i).getTipo().equals("]")) {
                            tokenEnquanto.add(token.get(i));
                            i++;
                        }
                        if ((i >= token.size()) || token.get(i).getTipo().equals("|n")) {
                            System.out.println("Erro: era esperado o token ']' na linha " + token.get(i - 1).getLinha());
                            in.readLine();
                        }
                        verificaCondicao(tokenEnquanto);
                        verificaCondicao2(tokenEnquanto);
                        tokenEnquanto.clear();
                        i++;
                    }

                } //le se proximo elemento é um =
                if ((i) >= token.size() || !token.get(i).getTipo().equals("atrib")) {
                    if (token.get(i).getTipo().equals("|n")) {
                        System.out.println("Erro: nao contem atribuição na linha " + token.get(i - 1).getLinha());
                        in.readLine();
                    } else {
                        System.out.println("Erro: token "+ token.get(i).getNome()+" não aceito na linha "+token.get(i).getLinha());
                        in.readLine();
                    }
                } else {
                    i++;
                    //le ate axar um \n
                    while ((i < token.size()) && (!token.get(i).getTipo().equals("|n"))) {
                        tokenEnquanto.add(token.get(i));
                        i++;
                    }
                    verificaCondicao(tokenEnquanto);
                    verificaCondicao2(tokenEnquanto);
                    tokenEnquanto.clear();
                }
            } //Analisa se é uma função
            else if (token.get(i).getTipo().equals("function")) {
                pilha.clear();
                i++;
                //le se o proximo é um fun
                if ((i) >= token.size() || !token.get(i).getTipo().equals("fun")) {
                    System.out.println("Erro: faltou passar os parâmetros da função na linha " + token.get(i - 1).getLinha());
                    in.readLine();
                } else {
                    i++;
                    if ((i) >= token.size() || !token.get(i).getTipo().equals("(")) {
                        System.out.println("Erro: faltou passar os parâmetros da função na linha " + token.get(i - 1).getLinha());
                        in.readLine();
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
                            System.out.println("Erro: faltou token ) na linha " + token.get(i - 1).getLinha());
                            in.readLine();
                        }
                        pilha.clear();
                        if (i < token.size()) {
                            pilha.push(token.get(i));
                        }
                        i++;
                        verificaParametro(tokenEnquanto);
                        tokenEnquanto.clear();

                        while ((i < token.size())) {
                            if (token.get(i).getTipo().equals("function")) {
                                pilha.push(token.get(i));
                            } else if (token.get(i).getTipo().equals("endfunction")) {
                                pilha.pop();
                                if (pilha.isEmpty()) {
                                    break;
                                }
                            }
                            tokenEnquanto.add(token.get(i));

                            i++;
                        }
                        if (!pilha.isEmpty()) {
                            System.out.println("Erro: Era esperado fim-funcao na linha " + (token.get(i - 3).getLinha() + 1));
                            in.readLine();
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
                        if (i >= token.size() || !token.get(i).getTipo().equals("Int")) {
                            System.out.println("Erro: era esperado [int] na linha " + token.get(i - 1).getLinha());
                            in.readLine();
                        } else {
                            i++;
                            if (i >= token.size() || !token.get(i).getTipo().equals("]")) {
                                System.out.println("Erro: era esperado token ] na linha " + token.get(i - 1).getLinha());
                                in.readLine();
                            } else {
                                i++;
                                //analisa se é declaração de matriz
                                if ((i < token.size()) && token.get(i).getTipo().equals("[")) {
                                    i++;
                                    if (i >= token.size() || !token.get(i).getTipo().equals("Int")) {
                                        System.out.println("Erro: era esperado [int][int] na linha " + token.get(i).getLinha());
                                        in.readLine();
                                    } else {
                                        i++;
                                        if (i >= token.size() || !token.get(i).getTipo().equals("]")) {
                                            System.out.println("Erro: era esperado token ] na linha " + token.get(i - 1).getLinha());
                                            in.readLine();
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("Erro: era esperado id do vetor na linha " + token.get(i - 1).getLinha());
                    in.readLine();
                }
            } else {
                if (!token.get(i).getTipo().equals("|n")) {
                    System.out.println("Erro: token " + token.get(i).getNome() + " inesperado na linha " + token.get(i).getLinha());
                    in.readLine();
                }
            }
        }
    }

    public void Analisa() throws IOException {
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
            System.out.println("Erro: faltou fim na linha " + (comandos.get(comandos.size() - 2).getLinha() + 1));
            in.readLine();
        }

    }
}
