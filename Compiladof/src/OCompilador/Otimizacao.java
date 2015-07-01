package OCompilador;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class Otimizacao {

    ArrayList<Lexema> TabTokensProg;
    ArrayList<Lexema> TabTokensFun;
    ArrayList<ArvoreBinaria<Lexema>> arvore;
    ArrayList<ArrayList<Lexema>> linhas;
    ArrayList<Lexema> variaveis;
    Stack<Integer> varDisponiveis;
    ArrayList<ArrayList<Lexema>> variaveisAlocadas;
    LinkedHashMap<Integer, ArrayList<Lexema>> lexemas;

    public Otimizacao(ArrayList<Lexema> TabTokensProg, ArrayList<Lexema> TabTokensFun, ArrayList<ArvoreBinaria<Lexema>> arvore, LinkedHashMap<Integer, ArrayList<Lexema>> listao) throws IOException {
        this.TabTokensFun = TabTokensFun;
        this.TabTokensProg = TabTokensProg;
        this.arvore = arvore;
        linhas = new ArrayList<>();
        variaveis = new ArrayList<>();
        varDisponiveis = new Stack<>();
        variaveisAlocadas = new ArrayList<>();
        this.lexemas = listao;
    }

    public Lexema PesquisaToken(Lexema token) {
        return token;
    }

    public ArrayList<Lexema> CriaExpr(Lexema esq, ArrayList<Lexema> dir) {
        ArrayList<Lexema> linha = new ArrayList<>();
        Lexema atrib = new Lexema();
        atrib.setNome("=");
        atrib.setTipo("atrib");
        linha.add(esq);
        linha.add(atrib);
        if (dir.size() == 1) {
            linha.add(dir.get(0));
        } else if (dir.size() == 3) {
            linha.add(dir.get(0));
            linha.add(dir.get(1));
            linha.add(dir.get(2));
        }
        return linha;
    }

    public boolean Operador(Lexema token) {
        return token.getTipo().equals("sum") || token.getTipo().equals("sub") || token.getTipo().equals("mult")
                || token.getTipo().equals("div") || token.getTipo().equals("gte") || token.getTipo().equals("gt")
                || token.getTipo().equals("lt") || token.getTipo().equals("lte") || token.getTipo().equals("eq")
                || token.getTipo().equals("neq");
    }

    public Lexema PesquisaListaTokens(Lexema lex) {
        for (Lexema TabTokensProg1 : TabTokensProg) {
            if (TabTokensProg1.getNome() != null && TabTokensProg1.getNome().equals(lex.getNome())) {
                return TabTokensProg1;
            }
        }
        return null;
    }

    public Lexema DesalocaVariavel(Lexema lex) {
        int i = lex.getLinha();
        Lexema newLex = PesquisaListaTokens(lex);

        for (int j = 0; j < variaveisAlocadas.size(); j++) {
            if (!Operador(lex) && (newLex.getEscopo().size() < variaveisAlocadas.get(j).get(0).getEscopo().size())) {
                Lexema retorno = variaveisAlocadas.get(j).get(1);
                variaveisAlocadas.remove(j);
                return retorno;
            } else if ((!Operador(lex)) && (newLex.getEscopo().size() == variaveisAlocadas.get(j).get(0).getEscopo().size())
                    && (!Objects.equals(newLex.getEscopo().get(newLex.getEscopo().size() - 1), variaveisAlocadas.get(j).get(0).getEscopo().get(variaveisAlocadas.get(j).get(0).getEscopo().size() - 1)))) {
                Lexema retorno = variaveisAlocadas.get(j).get(1);
                variaveisAlocadas.remove(j);
                return retorno;
            } else if (variaveisAlocadas.get(j).get(0).getLinhaAtual() < i) {
                if (variaveisAlocadas.get(j).get(0).getTipo().equals("aux")) {
                    Lexema retorno = variaveisAlocadas.get(j).get(0);
                    variaveisAlocadas.remove(j);
                    return retorno;
                } else {
                    Lexema retorno = variaveisAlocadas.get(j).get(1);
                    variaveisAlocadas.remove(j);
                    return retorno;
                }
            } else if (variaveisAlocadas.get(j).get(0).getTipo().equals("aux")) {
                Lexema retorno = variaveisAlocadas.get(j).get(0);
                variaveisAlocadas.remove(j);
                return retorno;
            }
        }
        System.out.println("Erro: variáveis superiores as disponiveis no programa. Linha " + lex.getLinha());
        System.exit(0);
        return null;
    }

    public Lexema LiberaVariavel(Lexema lex) {
        Lexema retorno;
        ArrayList<Lexema> lista = new ArrayList<>();
        if (lex.getTipo().equals("id")) {
            for (ArrayList<Lexema> variavel : variaveisAlocadas) {
                if (variavel.get(0).getNome().equals(lex.getNome())) {
                    return variavel.get(1);
                }
            }
            if (!variaveis.isEmpty()) {
                Lexema token = PesquisaListaTokens(lex);
                lista.add(token);
                retorno = variaveis.remove(0);
                lista.add(retorno);
                variaveisAlocadas.add(lista);
                return retorno;
            }
        }
        if (variaveis.isEmpty()) {
            retorno = DesalocaVariavel(lex);
            Lexema token = PesquisaListaTokens(lex);
            lista.add(token);
            lista.add(retorno);
            variaveisAlocadas.add(lista);
            return retorno;
        }
        retorno = variaveis.remove(0);
        Lexema aux = lex;
        aux.setNome(retorno.getNome());
        aux.setTipo("aux");
        lista.add(aux);
        variaveisAlocadas.add(lista);
        return retorno;
    }

    public ArrayList<Lexema> Expr(ArvoreBinaria<Lexema> arvore) {
        ArrayList<Lexema> exp = new ArrayList<>();
        ArrayList<Lexema> linha;
        Lexema varA;
        Lexema varB;

        if (arvore.getEsq() != null) {
            Lexema esq = arvore.getEsq().getNodo();
            if (esq.getTipo().equals("id")) {
                Lexema token = PesquisaListaTokens(esq);
                varA = LiberaVariavel(esq);
                exp.add(varA);
            } else if (Operador(esq)) {
                varA = LiberaVariavel(esq);
                exp.add(varA);
                linha = CriaExpr(varA, Expr(arvore.getEsq()));
                linhas.add(linha);
            } else if (esq.getTipo().equals("vetor")) {
                Lexema vetor = esq;
                vetor.setNome(esq.getNomeVar());
                exp.add(vetor);
            } else {
                exp.add(esq);
            }
        }
        if (Operador(arvore.getNodo())) {
            exp.add(arvore.getNodo());
        } else if (arvore.getNodo().getTipo().equals("id")) {
            exp.add(LiberaVariavel(arvore.getNodo()));
        } else {
            exp.add(arvore.getNodo());
        }
        if (arvore.getDir() != null) {
            Lexema dir = arvore.getDir().getNodo();
            if (dir.getTipo().equals("id")) {
                Lexema token = PesquisaListaTokens(dir);
                varA = LiberaVariavel(dir);
                exp.add(varA);
            } else if (Operador(dir)) {
                varB = LiberaVariavel(dir);
                exp.add(varB);
                linha = CriaExpr(varB, Expr(arvore.getDir()));
                linhas.add(linha);
            } else if (dir.getTipo().equals("vetor")) {
                Lexema vetor = dir;
                vetor.setNome(dir.getNomeVar());
                exp.add(vetor);
            } else {
                exp.add(dir);
            }
        }
        return exp;
    }

    public ArrayList<ArrayList<Lexema>> Otimiza(ArvoreBinaria<Lexema> arvore) {
        Lexema a = new Lexema();
        ArrayList<Lexema> linha;
        if (arvore.getEsq().getNodo().getTipo().equals("vet")) {
            a = arvore.getEsq().getNodo();
        } else {
            a = LiberaVariavel(arvore.getEsq().getNodo());
        }
        a.setLinha(arvore.getEsq().getNodo().getLinha());
        linhas.add(CriaExpr(a, Expr(arvore.getDir())));
        return linhas;
    }

    // verifica se akela linha contem uma atribução
    public boolean ContemAtrib(ArrayList<Lexema> tokens) {
        for (Lexema token : tokens) {
            if (token.getTipo().equals("atrib")) {
                return true;
            }
        }
        return false;
    }

    // retorna o nome criado para o id lex
    public Lexema retornaId(Lexema lex) {
        for (ArrayList<Lexema> var : variaveisAlocadas) {
            if (var.get(0).getNome().equals(lex.getNome())) {
                return var.get(1);
            }
        }
        return null;
    }

    // escreve o nome criado do Id
    public void analisaId(Lexema lex, PrintWriter printArq) {
        if (lex.getTipo().equals("id") && !lex.getNome().equals("fim")) {
            Lexema lex2 = retornaId(lex);
            if (lex2 != null) {
                printArq.print(lex.getNome() + " ");
            }
        }
    }
    
    // escreve o simbolo de operação em javascript
    public boolean AnalisaSimboloOper(Lexema lex, PrintWriter printArq){
        switch (lex.getTipo()) {
            case "mult":
                printArq.print(" * ");
                return true;
            case "or":
                printArq.print(" || ");
                return true;
            case "and":
                printArq.print(" && ");
                return true;
            case "div":
                printArq.print(" / ");
                return true;
            case "lte":
                printArq.print(" <= ");
                return true;
            case "gte":
                printArq.print(" >= ");
                return true;
            case "true":
                printArq.print("true");
            return true;
            case "false":
                printArq.print("false");
            return true;
        }
        return false;
    }
    
    // analisa uma condicao
    public void Condicao(ArrayList<Lexema> tokens, PrintWriter printArq){
        for (Lexema token : tokens) {
            // se for id, busca o nome criado para ela
            if(token.getNovoTipo().equals("id")){
                Lexema lex = retornaId(token);
                printArq.print(lex.getNome());
            }
            // se for mult, <=, etc, ele escreve o mesmo
            if(AnalisaSimboloOper(token, printArq)){
                
            } else {
                printArq.print(token.getNome());
            }
        }
    }

    // quando for um se
    public void linhaSe(ArrayList<Lexema> tokens, PrintWriter printArq) {
        printArq.print("if (");
        ArrayList<Lexema> condicao = new ArrayList<>();
        for (int i = 1; !tokens.get(i).getTipo().equals("initcond"); i++) {
            condicao.add(tokens.get(i));
        }
        Condicao(condicao, printArq);
        printArq.print("){");
        printArq.println("");
    }

    // se for o fim de um bloco, retorna true
    public boolean isFim(Lexema lex) {
        return lex.getNome().equals("fim") || lex.getTipo().equals("endcond")
                || lex.getTipo().equals("endwhileloop") || lex.getTipo().equals("endforloop")
                || lex.getTipo().equals("endfunction");
    }

    public void GeraCodigo() throws IOException {

        for (int i = 1;
                i < 11; i++) {
            Lexema lex = new Lexema();
            lex.setNome("a" + i);
            variaveis.add(lex);
        }

        varDisponiveis.push(10);
        int cont = 0;
        try (FileWriter arq = new FileWriter("codigo.txt")) {
            PrintWriter printArq = new PrintWriter(arq);
            for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : lexemas.entrySet()) {
                Integer key = entrySet.getKey();
                ArrayList<Lexema> value = entrySet.getValue();
                // se a linha contem atribuição, entao otimiza e da break na verificação
                if (ContemAtrib(value)) {
                    for (ArrayList<Lexema> value1 : Otimiza(arvore.get(cont))) {
                        for (Lexema value11 : value1) {
                            printArq.print(value11.getNome() + " ");
                        }
                        printArq.println("");
                    }
                    linhas.clear();
                    cont++;
                } else {
                    for (int i = 0; i < value.size(); i++) {
                        // se for um se, ele escreve apenas a linha da assinatura
                        if (value.get(i).getTipo().equals("cond")) {
                            linhaSe(value, printArq);
                            break;
                        } // se for um fim-se, fim-enquanto, fim-para, fim-funcao, ele coloca { 
                        else if (isFim(value.get(i))) {
                            printArq.print("}");
                            printArq.println();
                            break;
                        }
                    }
                }
            }
            arq.close();
        }

        for (ArrayList<Lexema> linha : linhas) {
            for (Lexema linha1 : linha) {
                System.out.print(linha1.getNome());
            }
            System.out.println("");
        }
    }
}
