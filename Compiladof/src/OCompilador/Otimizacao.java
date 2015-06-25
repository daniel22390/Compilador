package OCompilador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class Otimizacao {

    ArrayList<Lexema> TabTokensProg;
    ArrayList<Lexema> TabTokensFun;
    ArrayList<ArvoreBinaria<Lexema>> arvore;
    LinkedHashMap<Integer, ArrayList<Lexema>> linhas;
    ArrayList<Lexema> variaveis;
    Stack<Integer> varDisponiveis;
    ArrayList<ArrayList<Lexema>> variaveisAlocadas;

    public Otimizacao(ArrayList<Lexema> TabTokensProg, ArrayList<Lexema> TabTokensFun, ArrayList<ArvoreBinaria<Lexema>> arvore) {
        this.TabTokensFun = TabTokensFun;
        this.TabTokensProg = TabTokensProg;
        this.arvore = arvore;
        linhas = new LinkedHashMap<>();
        variaveis = new ArrayList<>();
        varDisponiveis = new Stack<>();
        variaveisAlocadas = new ArrayList<>();
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
            if (TabTokensProg1.getNome().equals(lex.getNome())) {
                return TabTokensProg1;
            }
        }
        return null;
    }

    public Lexema DesalocaVariavel(Lexema lex) {
        int i = lex.getLinha();
        Lexema newLex = PesquisaListaTokens(lex);

        for (ArrayList<Lexema> variaveis : variaveisAlocadas) {
            if (variaveis.get(0).getLinhaAtual() < i) {
                Lexema retorno = variaveis.get(1);
                variaveis.remove(this);
                return retorno;
            } else if (variaveis.get(0).getTipo().equals("aux")) {
                Lexema retorno = variaveis.get(0);
                variaveis.remove(this);
                return retorno;
            } else if (!Operador(lex) && (newLex.getEscopo().size() < variaveis.get(0).getEscopo().size())) {
                Lexema retorno = variaveis.get(1);
                variaveis.remove(this);
                return retorno;
            } else if ((!Operador(lex)) && (newLex.getEscopo().size() == variaveis.get(0).getEscopo().size())
                    && (!Objects.equals(newLex.getEscopo().get(newLex.getEscopo().size() - 1), variaveis.get(0).getEscopo().get(variaveis.get(0).getEscopo().size() - 1)))) {
                Lexema retorno = variaveis.get(1);
                variaveis.remove(this);
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
        if (variaveis.isEmpty()) {
            retorno = DesalocaVariavel(lex);
            Lexema token = PesquisaListaTokens(lex);
            lista.add(token);
            lista.add(retorno);
            variaveisAlocadas.add(lista);
            return retorno;
        }
        if (lex.getTipo().equals("id")) {
            for (ArrayList<Lexema> variavel : variaveisAlocadas) {
                if (variavel.get(0).getNome().equals(lex.getNome())) {
                    return variavel.get(1);
                }
            }
            Lexema token = PesquisaListaTokens(lex);
            lista.add(token);
            retorno = variaveis.remove(0);
            lista.add(retorno);
            variaveisAlocadas.add(lista);
            return retorno;
        }
        retorno = variaveis.remove(0);
        Lexema aux = lex;
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
                linhas.put(varA.getLinha(), linha);
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
                linhas.put(varB.getLinha(), linha);
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

    public void Otimiza(ArvoreBinaria<Lexema> arvore) {
        Lexema a = new Lexema();
        if (arvore.getEsq().getNodo().getTipo().equals("vet")) {
            a = arvore.getEsq().getNodo();
        } else {
            a = LiberaVariavel(arvore.getEsq().getNodo());
        }
        a.setLinha(arvore.getEsq().getNodo().getLinha());
        linhas.put(a.getLinha(), CriaExpr(a, Expr(arvore.getDir())));
    }

    public void analisa() {
        for (int i = 1; i < 11; i++) {
            Lexema lex = new Lexema();
            lex.setNome("a" + i);
            variaveis.add(lex);
        }
        varDisponiveis.push(10);
        for (ArvoreBinaria<Lexema> arv : arvore) {
            Otimiza(arv);
        }

        for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : linhas.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Lexema> value = entrySet.getValue();
            for (Lexema value1 : value) {
                System.out.print(value1.getNome());
            }
            System.out.println("");
        }
    }
}
