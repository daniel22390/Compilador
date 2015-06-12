package OCompilador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class AnaliseSintatica {

    LinkedHashMap<Integer, ArrayList<Lexema>> lexemas;
    ArrayList<Lexema> comandos = new ArrayList<>();
    ArvoreBinaria<Lexema> arvoreCond;
    Lexema l = null;
    int cont = 0;
    boolean errou = false;

    public void insereErro(String err, int linha) {
        System.out.println(err + linha);
        System.exit(0);
    }

    public AnaliseSintatica(LinkedHashMap<Integer, ArrayList<Lexema>> listao) {
        this.lexemas = listao;
    }

    public void verificaParametro(ArrayList<Lexema> token, int j) throws IOException {
//        for (Lexema token1 : token) {
//            System.out.println(token1.getNome());
//        }
//        System.out.println("--------------------------");
        ArrayList<Lexema> tokenParam = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenCond = new ArrayList<Lexema>();
        Stack<Lexema> pilha = new Stack<>();
        boolean ePara = false;
        int posPara = 0;
        int k;
        if (token.isEmpty()) {
            insereErro("Erro: falta parâmetro na função da linha ", j);
        }
        for (k = 0; k < token.size(); k++) {
            if (!token.get(k).getTipo().equals("|n")) {
                //empilha
                if (token.get(k).getTipo().equals("(")) {
                    pilha.push(token.get(k));
                } //desempilha
                else if (token.get(k).getTipo().equals(")")) {
                    if (pilha.isEmpty()) {
                        insereErro("Erro: era esperado ( na linha ", token.get(k).getLinha());
                    } else {
                        pilha.pop();
                        if (pilha.isEmpty()) {
                            if (k < (token.size() - 1)) {
                                insereErro("Erro: elemento não aceito após ) na linha ", token.get(0).getLinha());
                            }
                            break;
                        }
                    }
                } //verifica se o *,x,/,: ta no nivel mais para fora
                else if ((token.get(k).getTipo().equals(",")) && pilha.isEmpty()) {
                    ePara = true;
                    posPara = k;
                    break;
                }
            }
        }
        if (!pilha.isEmpty()) {
            insereErro("Erro: era esperado ) na linha ", token.get(k - 1).getLinha());

        } else {
            if (ePara && (posPara == (token.size() - 1) || posPara == 0)) {
                insereErro("Erro: token " + token.get(posPara).getNome() + " inesperado no parâmetro da função da linha ", token.get(posPara).getLinha());
            }
            if (ePara) {
                //antes do operador envia para verificaExpressao
                for (int i = 0; i < posPara; i++) {
                    tokenCond.add(token.get(i));
                }
                verificaCondicao(tokenCond, j);
                tokenCond.clear();
                //depois do operador envia para verificaTermo
                for (int i = (posPara + 1); i < token.size(); i++) {
                    tokenParam.add(token.get(i));
                }
                verificaParametro(tokenParam, j);
                tokenParam.clear();
            } //envia td para verificaTermo
            else {
                for (int i = 0; i < token.size(); i++) {
                    tokenCond.add(token.get(i));
                }
                verificaCondicao(tokenCond, j);
                tokenCond.clear();
            }
        }
        pilha.clear();
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
                    insereErro("Erro: falta operador na linha ", token.get(0).getLinha());
                }
                verificaCondicao(tokenCond, token.get(0).getLinha());
                tokenCond.clear();
                pilha.clear();
            } else if (token.get(0).getTipo().equals("fun")) {
                if (token.get(1).getTipo().equals("(")) {
                    pilha.push(token.get(1));
                    for (int i = 2; i < token.size(); i++) {
                        if (token.get(i).getTipo().equals(")")) {
                            pilha.pop();
                            if (pilha.isEmpty()) {
                                if (i < (token.size() - 1)) {
                                    insereErro("Erro: elemento não aceito após ) na linha ", token.get(0).getLinha());
                                }
                                break;
                            }
                        } else if (token.get(i).getTipo().equals("(")) {
                            pilha.push(token.get(i));
                        }
                        tokenParam.add(token.get(i));
                    }
                    verificaParametro(tokenParam, token.get(0).getLinha());
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
                        insereErro("Erro: era esperado ] na linha ", token.get(i - 1).getLinha());
                    } else {
                        verificaCondicao(tokenCond, token.get(i).getLinha());
                        tokenCond.clear();
                        if ((i + 1) < token.size() && token.get(i + 1).getTipo().equals("[")) {
                            i = i + 2;
                            while (i < token.size() && !token.get(i).getTipo().equals("]")) {
                                tokenCond.add(token.get(i));
                                i++;
                            }
                            if (i >= token.size() || !token.get(i).getNome().equals("]")) {
                                insereErro("Erro: era esperado ] na linha ", token.get(i - 1).getLinha());
                            }
                            verificaCondicao(tokenCond, token.get(i).getLinha());
                            tokenCond.clear();
                        } else if ((i + 1) < token.size() && !token.get(i + 1).getTipo().equals("[")) {
                            insereErro("Erro: token inesperado na linha ", token.get(i - 1).getLinha());
                        }
                    }
                } else {
                    if (1 < token.size()) {
                        insereErro("Erro: token " + token.get(1).getNome() + " inesperado na linha ", token.get(0).getLinha());
                    } else {
                        insereErro("Erro: token inesperado na linha ", token.get(0).getLinha());
                    }
                }
            } else {
                insereErro("Erro: token inesperado na linha ", token.get(0).getLinha());
            }
        } else if (token.size() == 1) {
            if (!termos(token.get(0))) {
                insereErro("Erro: token " + token.get(0).getNome() + " nao aceito na linha ", token.get(0).getLinha());
            }
        }
    }

    public void verificaExpressaoPrec(ArrayList<Lexema> token) throws IOException {
        ArrayList<Lexema> tokenTermo = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenExprPrec = new ArrayList<Lexema>();
        Stack<Lexema> pilha = new Stack<>();
        boolean eTermo = false;
        int posTermo = 0;
        int k;
//        for (Lexema pilha1 : token) {
//            System.out.println(pilha1.getNome());
//        }
//        System.out.println("-----");
        for (k = 0; k < token.size(); k++) {
            if (!token.get(k).getTipo().equals("|n")) {
                //empilha
                if (token.get(k).getTipo().equals("(")) {
                    pilha.push(token.get(k));
                } //desempilha
                else if (token.get(k).getTipo().equals(")")) {
                    if (pilha.isEmpty()) {
                        insereErro("Erro: era esperado ( na linha ", token.get(k).getLinha());
                    } else {
                        pilha.pop();
                    }
                } //verifica se o *,x,/,: ta no nivel mais para fora
                else if ((token.get(k).getTipo().equals("mult") || token.get(k).getTipo().equals("div")) && pilha.isEmpty()) {
                    eTermo = true;
                    posTermo = k;
                }
            }
        }
        if (!pilha.isEmpty()) {
            insereErro("Erro: era esperado ) na linha ", token.get(k - 1).getLinha());

        } else {
            if (eTermo && (posTermo == (token.size() - 1) || posTermo == 0)) {
                insereErro("Erro: token " + token.get(posTermo).getNome() + " inesperado na linha ", token.get(posTermo).getLinha());
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
        pilha.clear();
    }

    public void verificaExpressao(ArrayList<Lexema> token) throws IOException {
        ArrayList<Lexema> tokenExpr = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenExprPrec = new ArrayList<Lexema>();
        Stack<Lexema> pilha = new Stack<>();
        boolean eExprPrec = false;
        int posPrec = 0;
        int i;
//        for (Lexema pilha1 : token) {
//            System.out.println(pilha1.getNome());
//        }
//        System.out.println("------");
        for (i = 0; i < token.size(); i++) {
            if (!token.get(i).getTipo().equals("|n")) {
                //empilha
                if (token.get(i).getTipo().equals("(")) {
                    pilha.push(token.get(i));
                } //desempilha
                else if (token.get(i).getTipo().equals(")")) {
                    if (pilha.isEmpty()) {
                        insereErro("Erro: era esperado ( na linha ", token.get(i).getLinha());
                    } else {
                        pilha.pop();
                    }
                } //verifica se o + ou - ta no nivel mais para fora
                else if ((token.get(i).getTipo().equals("sum") || token.get(i).getTipo().equals("sub")) && pilha.isEmpty()) {
                    eExprPrec = true;
                    posPrec = i;
                }
            }
        }
        if (!pilha.isEmpty()) {
            insereErro("Erro: era esperado ) na linha ", token.get(i - 1).getLinha());

        } else {
            if (eExprPrec && (posPrec == (token.size() - 1) || posPrec == 0)) {
                insereErro("Erro: token " + token.get(posPrec).getNome() + " inesperado na linha ", token.get(posPrec).getLinha());
            }
            if (eExprPrec) {
                //antes do operador envia para verificaExpressao
                for (int j = 0; j < posPrec; j++) {
                    tokenExpr.add(token.get(j));
                }
                verificaExpressao(tokenExpr);
                tokenExpr.clear();
                //depois do operador envia para verificaExpressaoPrec
                for (int j = (posPrec + 1); j < token.size(); j++) {
                    tokenExprPrec.add(token.get(j));
                }
                verificaExpressaoPrec(tokenExprPrec);
                tokenExprPrec.clear();
            } //envia td para ExpressaoPrec
            else {
                for (int j = 0; j < token.size(); j++) {
                    tokenExprPrec.add(token.get(j));
                }
                verificaExpressaoPrec(tokenExprPrec);
                tokenExprPrec.clear();
            }
        }
        pilha.clear();
    }

    public void verificaCondicao(ArrayList<Lexema> token, int k) throws IOException {
        ArrayList<Lexema> tokenExpr = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenCond = new ArrayList<Lexema>();
        Stack<Lexema> pilha = new Stack<>();
        boolean eCondicao = false;
        int posCond = 0;
        int j;

//        for (Lexema pilha1 : token) {
//            insereErro(pilha1.getNome());
//        }
//        insereErro("----");
        //Analiso o ArrayList
        if (token.isEmpty()) {
            insereErro("Erro: Não contém condição na linha ", k);

        }
        for (j = 0; j < token.size(); j++) {
            if (!token.get(j).getTipo().equals("|n")) {
                //empilha
                if (token.get(j).getTipo().equals("(")) {
                    pilha.push(token.get(j));
                } //desempilha
                else if (token.get(j).getTipo().equals(")")) {
                    if (pilha.isEmpty()) {
                        insereErro("Erro: era esperado ( na linha ", token.get(j).getLinha());
                    } else {
                        pilha.pop();
                    }
                } //verifica se a condicao ta no nivel mais para fora
                else if (condicoes(token.get(j)) && pilha.isEmpty()) {
                    eCondicao = true;
                    posCond = j;
                }
            }
        }
        if (!pilha.isEmpty()) {
            insereErro("Erro: era esperado ) na linha ", token.get(j - 1).getLinha());

        } else {
            if (eCondicao && (posCond == (token.size() - 1) || posCond == 0)) {
                insereErro("Erro: token " + token.get(posCond).getNome() + " inesperado na linha ", token.get(posCond).getLinha());
            } else {
                if (eCondicao && (token.get(posCond - 1).getTipo().equals("|n") || token.get(posCond + 1).getTipo().equals("|n"))) {
                    insereErro("Erro: condicionador " + token.get(posCond).getNome() + " inesperado na linha ", token.get(posCond).getLinha());

                }
            }

            if (eCondicao) {
                //antes da condicao envia para verificaCondicao
                for (int i = 0; i < posCond; i++) {
                    tokenCond.add(token.get(i));
                }
                verificaCondicao(tokenCond, token.get(posCond).getLinha());
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
//            insereErro(pilha1.getNome());
//        }
//        insereErro("----");
        //percorre array com comandos
        for (int i = 0; i < token.size(); i++) {
            //verifica o comando se
            if (token.get(i).getTipo().equals("cond")) {
                //le ate achar um entao
                pilha.clear();
                pilha.push(token.get(i));
                i++;
                while ((i < token.size()) && (!token.get(i).getTipo().equals("initcond")) && (!token.get(i).getNome().equals("|n"))) {
                    if (!token.get(i).getTipo().equals("|n")) {
                        tokencond.add(token.get(i));
                    }
                    i++;
                }
                if (token.get(i).getNome().equals("|n")) {
                    insereErro("Erro: não contem o então do se na linha ", token.get(i - 1).getLinha());

                }
                verificaCondicao(tokencond, token.get(i - 1).getLinha());
                tokencond.clear();

                i++;

                //le ate axar o fim-se
                while ((i < token.size())) {
                    if (token.get(i).getTipo().equals("cond")) {
                        pilha.push(token.get(i));
                    } else if (token.get(i).getTipo().equals("endcond")) {
                        pilha.pop();
                        if (pilha.isEmpty()) {
                            if ((i + 1) < token.size() && !token.get(i + 1).getTipo().equals("|n")) {
                                insereErro("Erro: deve haver uma quebra de linha na linha ", token.get(i).getLinha());

                            }
                            break;
                        }
                    } else if (token.get(i).getTipo().equals("altcond")) {
                        if (pilha.size() == 1) {
                            verificaComandos(tokencomandos);
                            tokencomandos.clear();
                            i++;
                            continue;
                        }
                    }
                    tokencomandos.add(token.get(i));
                    i++;
                }
                if (!pilha.isEmpty()) {
                    insereErro("Erro: faltou fechamento do " + pilha.peek().getNome() + " da linha ", pilha.peek().getLinha());

                }
                pilha.clear();
                verificaComandos(tokencomandos);
                tokencomandos.clear();
            } //le ate achar um enquanto
            else if (token.get(i).getTipo().equals("whileloop")) {
                pilha.clear();
                pilha.push(token.get(i));
                i++;
                //le ate axar um faça
                while ((i < token.size()) && (!token.get(i).getTipo().equals("initforloop")) && (!token.get(i).getNome().equals("|n"))) {
                    if (!token.get(i).getTipo().equals("|n")) {
                        tokenEnquanto.add(token.get(i));
                    }
                    i++;
                }
                if (token.get(i).getNome().equals("|n")) {
                    insereErro("Erro: não contem o faça do loop na linha ", token.get(i - 1).getLinha());

                }
                verificaCondicao(tokenEnquanto, token.get(i - 1).getLinha());
                tokenEnquanto.clear();

                i++;
                //le ate axar o fim-enquanto
                while ((i < token.size())) {
                    if (token.get(i).getTipo().equals("whileloop")) {
                        pilha.push(token.get(i));
                    } else if (token.get(i).getTipo().equals("endwhileloop")) {
                        pilha.pop();
                        if (pilha.isEmpty()) {
                            if ((i + 1) < token.size() && !token.get(i + 1).getTipo().equals("|n")) {
                                insereErro("Erro: deve haver uma quebra de linha na linha ", token.get(i).getLinha());

                            }
                            break;
                        }
                    }
                    tokencomandos.add(token.get(i));
                    i++;
                }
                if (!pilha.isEmpty()) {
                    insereErro("Erro: faltou fechamento do " + pilha.peek().getNome() + " da linha ", pilha.peek().getLinha());

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
                    insereErro("Erro: faltou id na linha ", token.get(i - 1).getLinha());

                } else {
                    i++;
                    //le se proximo elemento é um "de"
                    if ((i) >= token.size() || !token.get(i).getTipo().equals("rng1forloop")) {
                        insereErro("Erro: faltou token 'de' na linha ", token.get(i - 1).getLinha());

                    } else {
                        i++;
                        //le se proximo elemento é um inteiro
                        if ((i) >= token.size() || !token.get(i).getTipo().equals("Int")) {
                            insereErro("Erro: tipo nao inteiro no loop para na linha ", token.get(i - 1).getLinha());

                        } else {
                            i++;
                            //le se proximo elemento é "até"
                            if ((i) >= token.size() || !token.get(i).getTipo().equals("rng2forloop")) {
                                insereErro("Erro: faltou até no loop para na linha ", token.get(i - 1).getLinha());

                            } else {
                                i++;
                                //le se o próximo é um inteiro
                                if ((i) >= token.size() || !token.get(i).getTipo().equals("Int")) {
                                    insereErro("Erro: tipo nao inteiro no loop para na linha ", token.get(i - 1).getLinha());

                                } else {
                                    i++;
                                    //le se o próximo é um faça
                                    if ((i) >= token.size() || !token.get(i).getTipo().equals("initforloop")) {
                                        insereErro("Erro: nao contem token faça na linha ", token.get(i - 1).getLinha());

                                    } else {
                                        i++;
                                        //le ate axar um fim-para
                                        while ((i < token.size())) {
                                            if (token.get(i).getTipo().equals("forloop")) {
                                                pilha.push(token.get(i));
                                            } else if (token.get(i).getTipo().equals("endforloop")) {
                                                pilha.pop();
                                                if (pilha.isEmpty()) {
                                                    if ((i + 1) < token.size() && !token.get(i + 1).getTipo().equals("|n")) {
                                                        insereErro("Erro: deve haver uma quebra de linha na linha ", token.get(i).getLinha());

                                                    }
                                                    break;
                                                }
                                            }
                                            tokenEnquanto.add(token.get(i));
                                            i++;
                                        }
                                        if (!pilha.isEmpty()) {
                                            insereErro("Erro: faltou fechamento do " + pilha.peek().getNome() + " da linha ", pilha.peek().getLinha());

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
                        insereErro("Erro: token esperado ] na linha ", token.get(i - 1).getLinha());

                    }
                    verificaCondicao(tokenEnquanto, token.get(i - 1).getLinha());
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
                            insereErro("Erro: era esperado o token ']' na linha ", token.get(i - 1).getLinha());

                        }
                        verificaCondicao(tokenEnquanto, token.get(i - 1).getLinha());
                        tokenEnquanto.clear();
                        i++;
                    }

                } //le se proximo elemento é um =
                if ((i) >= token.size() || !token.get(i).getTipo().equals("atrib")) {
                    if (token.get(i).getTipo().equals("|n")) {
                        insereErro("Erro: nao contem atribuição na linha ", token.get(i - 1).getLinha());

                    } else {
                        insereErro("Erro: token " + token.get(i).getNome() + " não aceito na linha ", token.get(i).getLinha());

                    }
                } else {
                    i++;
                    //le ate axar um \n
                    while ((i < token.size()) && (!token.get(i).getTipo().equals("|n"))) {
                        tokenEnquanto.add(token.get(i));
                        i++;
                    }
                    verificaCondicao(tokenEnquanto, token.get(i - 1).getLinha());
                    tokenEnquanto.clear();
                }
            } //Analisa se é uma função
            else if (token.get(i).getTipo().equals("function")) {
                Stack<Lexema> pilha2 = new Stack<>();
                pilha.clear();
                pilha.push(token.get(i));
                i++;
                //le se o proximo é um fun
                if ((i) >= token.size() || !token.get(i).getTipo().equals("fun")) {
                    insereErro("Erro: faltou passar os parâmetros da função na linha ", token.get(i - 1).getLinha());

                } else {
                    i++;
                    if ((i) >= token.size() || !token.get(i).getTipo().equals("(")) {
                        insereErro("Erro: faltou passar os parâmetros da função na linha ", token.get(i - 1).getLinha());

                    } else {
                        pilha2.push(token.get(i));
                        i++;
                        while (!token.get(i).getTipo().equals("|n")) {
                            if (token.get(i).getTipo().equals("(")) {
                                pilha2.push(token.get(i));
                            } else if (token.get(i).getTipo().equals(")")) {
                                pilha2.pop();
                                if (pilha2.isEmpty()) {
                                    break;
                                }
                            }
                            tokenEnquanto.add(token.get(i));
                            i++;
                        }
                        if (!pilha2.isEmpty()) {
                            insereErro("Erro: era esperado ) na linha ", token.get(i - 1).getLinha());
                        } else {
                            i++;
                            verificaParametro(tokenEnquanto, token.get(i - 1).getLinha());
                            tokenEnquanto.clear();

                            while ((i < token.size())) {
                                if (token.get(i).getTipo().equals("function")) {
                                    pilha.push(token.get(i));
                                } else if (token.get(i).getTipo().equals("endfunction")) {
                                    pilha.pop();
                                    if (pilha.isEmpty()) {
                                        if ((i + 1) < token.size() && !token.get(i + 1).getTipo().equals("|n")) {
                                            insereErro("Erro: deve haver uma quebra de linha na linha ", token.get(i).getLinha());

                                        }
                                        break;
                                    }
                                }
                                tokenEnquanto.add(token.get(i));

                                i++;
                            }
                            if (!pilha.isEmpty()) {
                                insereErro("Erro: faltou fechamento da " + pilha.peek().getNome() + " da linha ", pilha.peek().getLinha());

                            }
                            verificaComandos(tokenEnquanto);
                        }
                        pilha2.clear();
                        pilha.clear();
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
                            insereErro("Erro: era esperado [int] na linha ", token.get(i - 1).getLinha());

                        } else {
                            i++;
                            if (i >= token.size() || !token.get(i).getTipo().equals("]")) {
                                insereErro("Erro: era esperado token ] na linha ", token.get(i - 1).getLinha());

                            } else {
                                i++;
                                //analisa se é declaração de matriz
                                if ((i < token.size()) && token.get(i).getTipo().equals("[")) {
                                    i++;
                                    if (i >= token.size() || !token.get(i).getTipo().equals("Int")) {
                                        insereErro("Erro: era esperado [int][int] na linha ", token.get(i - 1).getLinha());

                                    } else {
                                        i++;
                                        if (i >= token.size() || !token.get(i).getTipo().equals("]")) {
                                            insereErro("Erro: era esperado token ] na linha ", token.get(i - 1).getLinha());

                                        } else {
                                            i++;
                                            if (i >= token.size() || !token.get(i).getTipo().equals("|n")) {
                                                insereErro("Erro: deve haver uma quebra de linha na linha ", token.get(i).getLinha());

                                            }
                                        }

                                    }
                                } else if (i >= token.size() || !token.get(i).getTipo().equals("|n")) {
                                    insereErro("Erro: deve haver uma quebra de linha na linha ", token.get(i).getLinha());

                                }
                            }
                        }
                    } else {
                        insereErro("Erro: era esperado [int] do vetor na linha ", token.get(i - 1).getLinha());

                    }
                } else {
                    insereErro("Erro: era esperado id do vetor na linha ", token.get(i - 1).getLinha());

                }
            } else {
                if (!token.get(i).getTipo().equals("|n")) {
                    insereErro("Erro: token " + token.get(i).getNome() + " inesperado na linha ", token.get(i).getLinha());

                }
            }
        }
    }

    public ArvoreBinaria verificaTermoArvore(ArrayList<Lexema> token, int key) {
        ArrayList<Lexema> tokenCond = new ArrayList<Lexema>();
        String tokenParam = "";
        Lexema Parametro = new Lexema();
        String tokenVetor = "";
        Lexema Vetor = new Lexema();
        boolean eExprPrec = false;
        int posExprPrec = 0;
        ArvoreBinaria<Lexema> arvore;
        ArvoreBinaria<Lexema> arvore2;
        ArvoreBinaria<Lexema> arvore3;
        Stack<Lexema> pilha = new Stack<>();
//        for (Lexema pilha1 : token) {
//            System.out.println(pilha1.getNome());
//        }
//        System.out.println("-------------------------");
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
                return verificaCondArvore(tokenCond, key);
            } else if (token.get(0).getTipo().equals("fun")) {
                for (int i = 0; i < token.size(); i++) {
                    if (token.get(i).getTipo().equals(")")) {
                        pilha.pop();
                        tokenParam = (tokenParam + token.get(i).getNome());
                        if (pilha.isEmpty()) {
                            break;
                        }
                    } else if (token.get(i).getTipo().equals("(")) {
                        pilha.push(token.get(i));
                        tokenParam = (tokenParam + token.get(i).getNome());
                    } else {
                        tokenParam = (tokenParam + token.get(i).getNome());
                    }
                }
                Parametro.setNome(tokenParam);
                Parametro.setTipo("fun");
                Parametro.setLinha(key);
                arvore = new ArvoreBinaria<>(Parametro);
                return arvore;
            } else if (token.get(0).getTipo().equals("id")) {
                arvore = new ArvoreBinaria<>(token.get(0));
                int i;
                arvore2 = new ArvoreBinaria<>(token.get(2));
                arvore.setEsq(arvore2);
                if (token.size() > 4) {
                    arvore3 = new ArvoreBinaria<>(token.get(5));
                    arvore.setDir(arvore3);
                }
                return arvore;
            } else {
                return null;
            }
        } else if (token.size() == 1) {
            arvore = new ArvoreBinaria<>(token.get(0));
            return arvore;
        } else {
            return null;
        }
    }

    public ArvoreBinaria verificaExprPrecArvore(ArrayList<Lexema> token, int key) {
        ArrayList<Lexema> termo = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenExprPrec = new ArrayList<Lexema>();
        boolean eExprPrec = false;
        int posExprPrec = 0;
        ArvoreBinaria<Lexema> arvore;
        Stack<Lexema> pilha = new Stack<>();

        for (int j = 0; j < token.size(); j++) {
            if (!token.get(j).getTipo().equals("|n")) {
                //empilha
                if (token.get(j).getTipo().equals("(")) {
                    pilha.push(token.get(j));
                } //desempilha
                else if (token.get(j).getTipo().equals(")")) {
                    if (pilha.isEmpty()) {
                        insereErro("Erro: era esperado ( na linha ", token.get(j).getLinha());
                    } else {
                        pilha.pop();
                    }
                } //verifica se a condicao ta no nivel mais para fora
                else if ((token.get(j).getTipo().equals("mult") || token.get(j).getTipo().equals("div")) && pilha.isEmpty()) {
                    eExprPrec = true;
                    posExprPrec = j;
                }
            }
        }
        if (eExprPrec) {
            arvore = new ArvoreBinaria<>(token.get(posExprPrec));
            for (int k = 0; k < posExprPrec; k++) {
                tokenExprPrec.add(token.get(k));
            }
            arvore.setEsq(verificaExprPrecArvore(tokenExprPrec, key));
            tokenExprPrec.clear();
            //depois da condicao envia para verificaExpressao
            for (int k = (posExprPrec + 1); k < token.size() && !token.get(k).getTipo().equals("|n"); k++) {
                termo.add(token.get(k));
            }
            arvore.setDir(verificaTermoArvore(termo, key));
            termo.clear();
            return arvore;
        } else {
            for (int k = 0; k < token.size() && !token.get(k).getTipo().equals("|n"); k++) {
                termo.add(token.get(k));
            }
            return verificaTermoArvore(termo, key);
        }
    }

    public ArvoreBinaria verificaExprArvore(ArrayList<Lexema> token, int key) {
        ArrayList<Lexema> tokenExpr = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenExprPrec = new ArrayList<Lexema>();
        boolean eExpr = false;
        int posExpr = 0;
        ArvoreBinaria<Lexema> arvore;
        Stack<Lexema> pilha = new Stack<>();

        for (int j = 0; j < token.size(); j++) {
            if (!token.get(j).getTipo().equals("|n")) {
                //empilha
                if (token.get(j).getTipo().equals("(")) {
                    pilha.push(token.get(j));
                } //desempilha
                else if (token.get(j).getTipo().equals(")")) {
                    if (pilha.isEmpty()) {
                        insereErro("Erro: era esperado ( na linha ", token.get(j).getLinha());
                    } else {
                        pilha.pop();
                    }
                } //verifica se a condicao ta no nivel mais para fora
                else if ((token.get(j).getTipo().equals("sum") || token.get(j).getTipo().equals("sub")) && pilha.isEmpty()) {
                    eExpr = true;
                    posExpr = j;
                }
            }
        }
        if (eExpr) {
            arvore = new ArvoreBinaria<>(token.get(posExpr));
            for (int k = 0; k < posExpr; k++) {
                tokenExpr.add(token.get(k));
            }
            arvore.setEsq(verificaExprArvore(tokenExpr, key));
            tokenExpr.clear();
            //depois da condicao envia para verificaExpressao
            for (int k = (posExpr + 1); k < token.size() && !token.get(k).getTipo().equals("|n"); k++) {
                tokenExprPrec.add(token.get(k));
            }
            arvore.setDir(verificaExprPrecArvore(tokenExprPrec, key));
            tokenExprPrec.clear();
            return arvore;
        } else {
            for (int k = 0; k < token.size() && !token.get(k).getTipo().equals("|n"); k++) {
                tokenExprPrec.add(token.get(k));
            }
            return verificaExprPrecArvore(tokenExprPrec, key);
        }
    }

    public ArvoreBinaria verificaCondArvore(ArrayList<Lexema> token, int key) {
        ArrayList<Lexema> tokenCond = new ArrayList<Lexema>();
        ArrayList<Lexema> tokenExpr = new ArrayList<Lexema>();
        boolean eCondicao = false;
        int posCond = 0;
        ArvoreBinaria<Lexema> arvore;
        Stack<Lexema> pilha = new Stack<>();

        for (int j = 0; j < token.size(); j++) {
            if (!token.get(j).getTipo().equals("|n")) {
                //empilha
                if (token.get(j).getTipo().equals("(")) {
                    pilha.push(token.get(j));
                } //desempilha
                else if (token.get(j).getTipo().equals(")")) {
                    if (pilha.isEmpty()) {
                        insereErro("Erro: era esperado ( na linha ", token.get(j).getLinha());
                    } else {
                        pilha.pop();
                    }
                } //verifica se a condicao ta no nivel mais para fora
                else if (condicoes(token.get(j)) && pilha.isEmpty()) {
                    eCondicao = true;
                    posCond = j;
                }
            }
        }
        if (eCondicao) {
            arvore = new ArvoreBinaria<>(token.get(posCond));
            for (int k = 0; k < posCond; k++) {
                tokenCond.add(token.get(k));
            }
            arvore.setEsq(verificaCondArvore(tokenCond, key));
            tokenCond.clear();
            //depois da condicao envia para verificaExpressao
            for (int k = (posCond + 1); k < token.size() && !token.get(k).getTipo().equals("|n"); k++) {
                tokenExpr.add(token.get(k));
            }
            arvore.setDir(verificaExprArvore(tokenExpr, key));
            tokenExpr.clear();
            return arvore;
        } else {
            for (int k = 0; k < token.size() && !token.get(k).getTipo().equals("|n"); k++) {
                tokenExpr.add(token.get(k));
            }
            return verificaExprArvore(tokenExpr, key);
        }
    }

    public void print(ArvoreBinaria arvore) {
        int alt = arvore.altura();
        int esp = (int) (Math.pow(2, alt));
        LinkedList<ArvoreBinaria<Lexema>> fila;
        for (int i = 0; i < alt; i++) {
            fila = arvore.folhas(i);
            for (int j = 0; j < esp / 2 - 1; j++) {
                System.out.print("\t");
            }
            for (Iterator<ArvoreBinaria<Lexema>> iterator = fila.iterator(); iterator.hasNext();) {
                ArvoreBinaria<Lexema> next = iterator.next();
                if (next != null) {
                    System.out.print(next.getNodo().getNome());
                }
                for (int j = 0; j < esp; j++) {
                    System.out.print("\t");
                }
            }
            esp = esp / 2;
            System.out.println("");
        }
    }

    public ArrayList<ArvoreBinaria> geraArvore() {
        ArvoreBinaria<Lexema> arvore;
        ArvoreBinaria<Lexema> arvore2;
        ArrayList<Lexema> tokenAtrib = new ArrayList<Lexema>();
        ArrayList<ArvoreBinaria> arvores = new ArrayList<ArvoreBinaria>();
        for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : lexemas.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Lexema> value = entrySet.getValue();
            for (int i = 0; i < value.size(); i++) {
                if (value.get(i).getTipo().equals("atrib")) {
                    String vetor = "";
                    Lexema vet;
                    int a = 0, b = 0;
                    arvore = new ArvoreBinaria<>(value.get(i));
                    if (value.get(i - 1).getTipo().equals("id")) {
                        arvore2 = new ArvoreBinaria<>(value.get(i - 1));
                    } else {
                        for (int j = (i - 1); !value.get(j).getTipo().equals("["); j--) {
                            a = j;
                        }
                        if (value.get(a - 2).getTipo().equals("]")) {
                            a = a - 2;
                            while (!value.get(a).getTipo().equals("[")) {
                                a--;
                            }
                            a--;
                        }
                        for (int k = a; k < i; k++) {
                            vetor += value.get(k).getNome();
                        }
                        vet = new Lexema();
                        vet.setNome(vetor);
                        vet.setTipo("vet");
                        vet.setLinha(key);
                        arvore2 = new ArvoreBinaria<>(vet);
                    }
                    arvore.setEsq(arvore2);
                    for (int k = (i + 1); k < value.size(); k++) {
                        tokenAtrib.add(value.get(k));
                    }
                    arvore.setDir(verificaCondArvore(tokenAtrib, key));
                    //print(arvore);
                    //System.out.println("----------------------------------------------------------------------------------------------------------");
                    arvores.add(arvore);
                    tokenAtrib.clear();
                }
            }
        }
        return arvores;
    }

    public void Analisa() throws IOException {
        LinkedHashSet<String> mensagens = new LinkedHashSet<String>();
        boolean programa = false;
        ArrayList<ArvoreBinaria> arvores = new ArrayList<ArvoreBinaria>();
        
        System.out.println("Erro Sintáticos: ");
        sair:
        for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : lexemas.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Lexema> value = entrySet.getValue();
            for (Lexema value1 : value) {
                if (value1.getNome().equals("fim")) {
                    verificaComandos(comandos);
                    programa = true;
                } else if (!value1.getNome().equals("|n") && programa == true) {
                    insereErro("Erro: token após o fim do programa na linha ", value1.getLinha());
                    System.exit(0);

                } else {
                    comandos.add(value1);
                }
            }
        }
        if (programa == false) {
            insereErro("Erro: faltou fim do programa iniciado na linha ", 0);
            System.exit(0);

        }
        System.out.println("Sintaticamente correto! ");
        System.out.println("--------------------------------------------------------------------------------------");
        arvores = geraArvore();
        AnaliseSemantica semantico = new AnaliseSemantica(arvores, lexemas);
        semantico.Analisa();
    }
}
