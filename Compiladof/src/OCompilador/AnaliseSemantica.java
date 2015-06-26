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

    ArrayList<ArvoreBinaria<Lexema>> arvore = new ArrayList<>();
    // todas as variaveis visiveis no escopo
    Stack<ArrayList<Lexema>> varEscopo = new Stack<>();
    Stack<ArrayList<Lexema>> funEscopo = new Stack<>();
    ArrayList<ArrayList<Lexema>> funcoes = new ArrayList<ArrayList<Lexema>>();
    // todas as declaracoes de vetores que podem ser acessadas no escopo
    Stack<ArrayList<Lexema>> decVetorEscopo = new Stack<>();
    Stack<ArrayList<Lexema>> decVetFunEscopo = new Stack<>();
    LinkedHashMap<Integer, ArrayList<Lexema>> lexemas = new LinkedHashMap<>();
    ArrayList<Lexema> tabTokensProg = new ArrayList<Lexema>();
    ArrayList<Lexema> tabTokensFun = new ArrayList<Lexema>();
    ArrayList<ArrayList<Integer>> controleTokens = new ArrayList<>();
    int contador = 1;

    public AnaliseSemantica(ArrayList<ArvoreBinaria<Lexema>> arvore, LinkedHashMap<Integer, ArrayList<Lexema>> listao) {
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

    public boolean Comparador(Lexema token) {
        if (token.getTipo().equals("gt") || token.getTipo().equals("gte") || token.getTipo().equals("lt") || token.getTipo().equals("lte") || token.getTipo().equals("eq") || token.getTipo().equals("neq") || token.getTipo().equals("or") || token.getTipo().equals("and")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean AchaVetoresDeclarados(ArrayList<Lexema> tokens, int i, boolean isFunction, Lexema nome, String identificador) {
        boolean foiDeclarado = false;
        Stack<ArrayList<Lexema>> declaracao = new Stack<ArrayList<Lexema>>();
        if (isFunction) {
            declaracao = decVetFunEscopo;
        } else {
            declaracao = decVetorEscopo;
        }
        for (ArrayList<Lexema> decVetEsc : declaracao) {
            for (Lexema decVetEsc1 : decVetEsc) {
                if (decVetEsc1.getNome().equals(tokens.get(i - 1).getNome()) || decVetEsc1.getNome().equals(nome.getNome())) {
                    foiDeclarado = true;
                    //se os tipos forem diferentes
                    if ((decVetEsc1.getNovoTipo().equals("matriz") && identificador.equals("vetor")) || (decVetEsc1.getNovoTipo().equals("vetor") && identificador.equals("matriz"))) {
                        System.out.println("Erro: Variavel " + nome.getNome() + " nao foi declarada como " + identificador + ". Linha  " + tokens.get(i).getLinha());
                        System.exit(0);
                    } else if (!decVetEsc1.getNovoTipo().equals(identificador)) {
                        System.out.println("Erro: Variavel " + tokens.get(i - 1).getNome() + " nao foi declarada como " + identificador + ". Linha  " + tokens.get(i).getLinha());
                        System.exit(0);
                    }
                }
            }
        }
        return foiDeclarado;
    }

    public Lexema PesquisaListaTokens(Lexema token, boolean isFuncao) {
        Lexema retorno = null;
        if (isFuncao) {
            for (ArrayList<Lexema> var : funEscopo) {
                for (Lexema var1 : var) {
                    if (token.getNome().equals(var1.getNome())) {
                        retorno = var1;
                        return retorno;
                    }
                }
            }
        } else {
            for (ArrayList<Lexema> var : varEscopo) {
                for (Lexema var1 : var) {
                    if (token.getNome().equals(var1.getNome())) {
                        retorno = var1;
                        return retorno;
                    }
                }
            }
        }
        return retorno;
    }

    public Lexema PesquisaListaDecla(Lexema token, boolean isFuncao) {
        Lexema retorno = null;
        if (isFuncao) {
            for (ArrayList<Lexema> var : decVetFunEscopo) {
                for (Lexema var1 : var) {
                    if (token.getNome().equals(var1.getNome())) {
                        retorno = var1;
                        return retorno;
                    }
                }
            }
        } else {
            for (ArrayList<Lexema> var : decVetorEscopo) {
                for (Lexema var1 : var) {
                    if (token.getNome().equals(var1.getNome())) {
                        retorno = var1;
                        return retorno;
                    }
                }
            }
        }
        return retorno;
    }

    public void InserirListaTokens(Lexema token, boolean isFuncao) {
        if (isFuncao) {
            if (funEscopo.isEmpty()) {
                ArrayList<Lexema> array = new ArrayList<Lexema>();
                funEscopo.push(array);
            }
            funEscopo.peek().add(token);
        } else {
            varEscopo.peek().add(token);
        }
    }

    public void InserirListaDecla(Lexema token, boolean isFuncao) {
        if (isFuncao) {
            decVetFunEscopo.peek().add(token);
        } else {
            decVetorEscopo.peek().add(token);
        }
    }

    public void AtualizaLinhaToken(Lexema token, boolean isFuncao, Lexema lex) {
        if (isFuncao) {
            for (ArrayList<Lexema> var : funEscopo) {
                for (Lexema var1 : var) {
                    if (token.getNome().equals(var1.getNome())) {
                        var1.setLinhaAtual(lex.getLinha());
                        return;
                    }
                }
            }
        } else {
            for (ArrayList<Lexema> var : varEscopo) {
                for (Lexema var1 : var) {
                    if (token.getNome().equals(var1.getNome())) {
                        var1.setLinhaAtual(lex.getLinha());
                        return;
                    }
                }
            }
        }
    }

    public void inicializaEscopo(boolean isFuncao) {
        ArrayList<Lexema> iniciaEsc = new ArrayList<Lexema>();
        ArrayList<Lexema> iniciaDec = new ArrayList<Lexema>();
        if (!isFuncao) {
            varEscopo.push(iniciaEsc);
            decVetorEscopo.push(iniciaDec);
        } else {
            funEscopo.push(iniciaEsc);
            decVetFunEscopo.push(iniciaDec);
        }
    }

    public void FinalizaEscopo(boolean isFuncao) {
        if (isFuncao) {
            funEscopo.pop();
            decVetFunEscopo.pop();
        } else {
            varEscopo.pop();
            decVetorEscopo.pop();
        }
    }

    public int eLaco(ArrayList<Lexema> tokens, int i, boolean isFuncao) {
        ArrayList<Lexema> condicao = new ArrayList<>();
        Lexema BarranN = new Lexema();
        BarranN.setNome("|n");
        BarranN.setTipo("|n");
        for (i = 0; !tokens.get(i).getTipo().equals("initcond") && !tokens.get(i).getTipo().equals("initforloop"); i++) {
            condicao.add(tokens.get(i));
        }
        condicao.add(BarranN);
        if (!verificaTipoVariavel(condicao, isFuncao).equals("booleano")) {
            System.out.println("Erro: Condição nao é booleana. Linha  " + tokens.get(i).getLinha());
            System.exit(0);
        }
        return i;
    }

    public void eFor(Lexema token, boolean isFuncao) {
        boolean criada = false;
        Lexema var1 = PesquisaListaTokens(token, isFuncao);
        if (var1 != null && var1.getNome().equals(token.getNome())) {
            criada = true;
            if (!var1.getTipo().equals("Int")) {
                System.out.println("Erro: Variavel " + token.getNome() + " nao foi inicializada como inteiro. Linha  " + token.getLinha());
                System.exit(0);
            }
        }
        if (!criada) {
            Lexema idFor = new Lexema();
            idFor.setNome(token.getNome());
            idFor.setLinha(token.getLinha());
            idFor.setNovoTipo("id");
            idFor.setTipo("Int");
//            varEscopo.peek().add(idFor);
            InserirListaTokens(idFor, isFuncao);
        }
    }

    public ArrayList<Lexema> expressao(ArrayList<Lexema> tokens, int i) {
        ArrayList<Lexema> tipoVar = new ArrayList<>();
        for (int j = (i + 1); !tokens.get(j).getTipo().equals("|n"); j++) {
            tipoVar.add(tokens.get(j));
        }
        Lexema barraN = new Lexema();
        barraN.setTipo("|n");
        barraN.setNome("|n");
        tipoVar.add(barraN);
        return tipoVar;
    }

    public int AnalisaLaco(int posicao, ArrayList<Lexema> escopo, boolean isFuncao, ArrayList<Integer> pai, boolean primeiroAcesso) {
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
                            escopoProg(escopoSe, isFuncao, false, false, false, pai, primeiroAcesso);
                        } else {
                            escopoProg(escopoSe, isFuncao, true, false, false, pai, primeiroAcesso);
                        }
                        break;
                    }
                } else if (escopo.get(posicao).getTipo().equals("altcond")) {
                    if (pilha.size() == 1) {
                        senao = true;
                        escopoSe.add(acabou);
                        escopoProg(escopoSe, isFuncao, true, false, false, pai, primeiroAcesso);
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
                        escopoProg(escopoEnquanto, isFuncao, true, false, false, pai, primeiroAcesso);
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
                        escopoProg(escopoPara, isFuncao, false, true, false, pai, primeiroAcesso);
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

    public Lexema escopoFuncao(ArrayList<Lexema> escopo) {
        // nao esquecer de  inserir o tipo do retorno na pilha
        ArrayList<Lexema> iniciaEsc = new ArrayList<Lexema>();
        ArrayList<Lexema> iniciaDec = new ArrayList<Lexema>();
        funEscopo.push(iniciaEsc);
        decVetFunEscopo.push(iniciaDec);
        boolean primeiroAcesso = false;
        if (PesquisaListaTokens(escopo.get(0), false) == null) {
            primeiroAcesso = true;
            Lexema nomeFun = new Lexema();
            nomeFun.setNome(escopo.get(0).getNome());
            nomeFun.setLinha(escopo.get(0).getLinha());
            nomeFun.setNovoTipo("fun");
            nomeFun.setTipo("");
            for (ArrayList<Lexema> esc : varEscopo) {
                for (Lexema escopo1 : esc) {
                    if (escopo1.getNome().equals(escopo.get(0).getNome())) {
                        System.out.println("Erro: variavel " + escopo.get(0).getNome() + " inicializada como " + escopo1.getNovoTipo() + ". Linha  " + escopo.get(0).getLinha());
                        System.exit(0);
                    }
                }
            }
            varEscopo.peek().add(nomeFun);
        }
        verificaFuncao(1, escopo, true);
        ArrayList<Integer> array = new ArrayList<>();
        array.add(contador);
        escopoProg(escopo, true, false, false, true, array, primeiroAcesso);
        contador++;
        Lexema lex = PesquisaListaTokens(escopo.get(0), false);
        return lex;
    }

    public boolean PesquisaFuncao(Lexema token) {
        for (ArrayList<Lexema> fun : funcoes) {
            if (fun.get(0).getNome().equals(token.getNome())) {
                return true;
            }
        }
        return false;
    }

    public int verificaFuncao(int posicao, ArrayList<Lexema> tokens, boolean eAssinatura) {
        ArrayList<Lexema> newTokens = new ArrayList<>();
        Stack<Lexema> pilha = new Stack<>();
        pilha.push(tokens.get(posicao));
        Lexema barraN = new Lexema();
        barraN.setNome("|n");
        barraN.setTipo("|n");
        posicao++;
        if (!eAssinatura) {
            Lexema nomeFun = new Lexema();
            nomeFun.setNome(tokens.get(posicao - 1).getNome());
            nomeFun.setLinha(tokens.get(posicao - 1).getLinha());
            nomeFun.setNovoTipo("fun");
            ArrayList<Lexema> array = new ArrayList<Lexema>();
            array.add(nomeFun);
            funcoes.add(array);
            while (true) {
                if (tokens.get(posicao).getTipo().equals("(")) {
                    pilha.push(tokens.get(posicao));
                    newTokens.add(tokens.get(posicao));
                } else if (tokens.get(posicao).getTipo().equals(")")) {
                    pilha.pop();
                    if (pilha.isEmpty()) {
                        newTokens.add(barraN);
                        String tipo = verificaTipoVariavel(newTokens, false);
                        Lexema lex = new Lexema();
                        lex.setTipo(tipo);
                        funcoes.get(funcoes.size() - 1).add(lex);
                        newTokens.clear();
                        break;
                    }
                    newTokens.add(tokens.get(posicao));
                } else if (tokens.get(posicao).getTipo().equals(",")) {
                    newTokens.add(barraN);
                    String tipo = verificaTipoVariavel(newTokens, false);
                    Lexema lex = new Lexema();
                    lex.setTipo(tipo);
                    funcoes.get(funcoes.size() - 1).add(lex);
                    newTokens.clear();
                    posicao++;
                    continue;
                }
                newTokens.add(tokens.get(posicao));
                posicao++;
            }
        } else {
            int cont = 1;
            while (true) {
                if (tokens.get(posicao).getTipo().equals("(")) {
                    pilha.push(tokens.get(posicao));
                } else if (tokens.get(posicao).getTipo().equals(")")) {
                    pilha.pop();
                    if (pilha.isEmpty()) {
                        break;
                    }
                } else if (tokens.get(posicao).getTipo().equals("id")) {
                    funcoes.get(funcoes.size() - 1).get(cont).setNome(tokens.get(posicao).getNome());
                    funcoes.get(funcoes.size() - 1).get(cont).setNovoTipo("id");
                    funcoes.get(funcoes.size() - 1).get(cont).setLinha(tokens.get(posicao).getLinha());
                    cont++;
                }
                posicao++;
            }
            for (int i = 1; i < funcoes.get(funcoes.size() - 1).size(); i++) {
                funEscopo.peek().add(funcoes.get(funcoes.size() - 1).get(i));
            }
        }
//        System.out.println("Escopo fun: ");
//        for (ArrayList<Lexema> pilha1 : funEscopo) {
//            for (Lexema pilha11 : pilha1) {
//                System.out.println(pilha11.getNome() + " " + pilha11.getTipo());
//            }
//        }
//        System.out.println("--------------------");
        return posicao;
    }

    //recebe uma posicao do id do vetor, e envia o token de condicao para verificaTipoVariavel
    public int analisaVetor(int posicao, ArrayList<Lexema> tokens, boolean isFuncao) {
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
        String tipo = verificaTipoVariavel(lista, isFuncao);
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
            tipo = verificaTipoVariavel(lista, isFuncao);
            if (!tipo.equals("Int")) {
                System.out.println("Erro: O vetor " + tokens.get(i - 1).getNome() + " nao possui inteiro como posicao. Linha  " + tokens.get(i).getLinha());
                System.exit(0);
            }
        }

        return posicao;
    }

    public void AnalisaId(ArrayList<Lexema> tokens, int i, String tipoInicial, boolean isFuncao, boolean mais) {
        Stack<ArrayList<Lexema>> lista = new Stack<ArrayList<Lexema>>();
        if (isFuncao) {
            lista = funEscopo;
        } else {
            lista = varEscopo;
        }
        boolean controlePilha = false;
        for (ArrayList<Lexema> varEsc : lista) {
            for (Lexema varEsc1 : varEsc) {
                // se for int e o tipo inicial nao for int ou float, entao erro
                if (tokens.get(i).getTipo().equals("fun") && !varEsc1.getNovoTipo().equals("fun")) {
                    System.out.println("Erro: Tipo de variavel " + tokens.get(i).getNome() + " nao pode ser modificada. Linha  " + tokens.get(i).getLinha());
                    System.exit(0);
                } else if (tokens.get(i).getNome().equals(varEsc1.getNome())) {
                    AtualizaLinhaToken(tokens.get(i), isFuncao, tokens.get(i));
                    if (varEsc1.getTipo().equals("")) {
                        System.out.println("Erro: variavel " + varEsc1.getNome() + " ainda nao foi inicializada. Linha  " + tokens.get(i).getLinha());
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
    }

    // fazer com que <, >, ... seja apenas entre condicionadores
    //retorna int, float, booleano(posicao contem a localizacao do id)
    public String verificaTipoVariavel(ArrayList<Lexema> tokens, boolean isFuncao) {
        int i = 0;
        // pego o tipo do primeiro elemento
        String tipoInicial = "";
        boolean achou = false;
        // passo todos os (
        while (tokens.get(i).getTipo().equals("(") || tokens.get(i).getTipo().equals("sub")) {
            i++;
        }
        // se o primeiro elemento for id, ou fun
        if (tokens.get(i).getTipo().equals("id")) {
            Lexema lista1 = PesquisaListaTokens(tokens.get(i), isFuncao);
            if (lista1 != null) {
                if (lista1.getTipo().equals("")) {
                    System.out.println("Erro: variavel " + lista1.getNome() + " ainda nao foi inicializada. Linha  " + tokens.get(i).getLinha());
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
                AtualizaLinhaToken(tokens.get(i), isFuncao, tokens.get(i));
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
        if (tipoInicial.equals("") && !tokens.get(i - 1).getTipo().equals("fun")) {
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
            else if (tokens.get(i).getTipo().equals("id")) {
                AnalisaId(tokens, i, tipoInicial, isFuncao, mais);
                controlePilha = false;
            } else if (tokens.get(i).getTipo().equals("[")) {
                i = analisaVetor(i, tokens, isFuncao);
            } // se for um comparador, entao seto como booleano
            else if (Comparador(tokens.get(i))) {
                Booleano = true;
                mais = false;
            } else if (tokens.get(i).getTipo().equals("div")) {
                Float = true;
                mais = false;
            } else if (tokens.get(i).getTipo().equals("(") && (i - 1) >= 0 && tokens.get(i - 1).getTipo().equals("fun")) {
                int k = verificaFuncao(i, tokens, false);
                Lexema acabou = new Lexema();
                acabou.setTipo("acabou");
                ArrayList<Lexema> escopoProg = new ArrayList<Lexema>();
                boolean achaFuncao = false;
                String tipo = "";
                for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : lexemas.entrySet()) {
                    Integer key = entrySet.getKey();
                    ArrayList<Lexema> value = entrySet.getValue();
                    for (int z = 0; z < value.size(); z++) {
                        if (value.get(z).getTipo().equals("endfunction") && achaFuncao) {
                            achaFuncao = false;
                            escopoProg.add(value.get(z));
                            escopoProg.add(acabou);
                            tipo = escopoFuncao(escopoProg).getTipo();
                        } else if (achaFuncao) {
                            escopoProg.add(value.get(z));
                        } else if (value.get(z).getTipo().equals("function") && value.get(z + 1).getNome().equals(tokens.get(i - 1).getNome())) {
                            achaFuncao = true;
                        }
                    }
                }
                if (!tipoInicial.equals("")) {
                    if (tipo.equals("true") || tipo.equals("false")) {
                        if (!tipoInicial.equals("booleano")) {
                            System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                            System.exit(0);
                        }
                    }// se for string e o tipo inicial nao for string, entao dara erro 
                    else if (tipo.equals("String")) {
                        if (!tipoInicial.equals("String") && !(mais && (tipoInicial.equals("Int") || tipoInicial.equals("Float")))) {
                            System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                            System.exit(0);
                        } else if (mais && (tipoInicial.equals("Int") || tipoInicial.equals("Float"))) {
                            tipoInicial = "String";
                        }
                    }// se for int e o tipo inicial nao for int ou float, entao dara erro 
                    else if (tipo.equals("Int")) {
                        if (!tipoInicial.equals("Int") && !tipoInicial.equals("Float") && !(mais && (tipoInicial.equals("String")))) {
                            System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                            System.exit(0);
                        } else if (mais && (tipoInicial.equals("String"))) {
                            tipoInicial = "String";
                        }
                    }// se for float e o tipo inicial nao for int ou float, entao dara erro 
                    else if (tipo.equals("Float")) {
                        if (!tipoInicial.equals("Int") && !tipoInicial.equals("Float") && !(mais && (tipoInicial.equals("String")))) {
                            System.out.println("Erro: Tipos diferentes. Linha  " + tokens.get(i).getLinha());
                            System.exit(0);
                        } else if (mais && (tipoInicial.equals("String"))) {
                            tipoInicial = "String";
                        } else {
                            tipoInicial = "Float";
                        }
                    }
                }
                tipoInicial = tipo;
                i = k;
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
    public Lexema nomeVetor(int posicao, ArrayList<Lexema> tokens) {
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
        return tokens.get(posicao);
    }

    public void salvaTokens(boolean isFunction, ArrayList<Integer> pai) {
        Stack<ArrayList<Lexema>> pilha;
        ArrayList<Integer> paiAux = new ArrayList<>(pai);
        if (isFunction) {
            pilha = funEscopo;
        } else {
            pilha = varEscopo;
        }
        for (Lexema lex : pilha.peek()) {
            lex.setEscopo(paiAux);
            if (isFunction) {
                tabTokensFun.add(lex);
            } else {
                tabTokensProg.add(lex);
            }
        }
    }

    public void escopoProg(ArrayList<Lexema> escopo, boolean isFuncao, boolean isLaco, boolean isFor, boolean primeiraFun, ArrayList<Integer> pai, boolean primAcesso) {
        ArrayList<Lexema> varProg = new ArrayList<>();
        ArrayList<Lexema> decVetProg = new ArrayList<>();
        int cont = 0;
        boolean primChamada = primeiraFun;
        // empilho o escopo na pilha de escopos
        if (!primeiraFun) {
            inicializaEscopo(isFuncao);
        }
        primeiraFun = false;
        int i = 0;
        if (isLaco) {
            i = eLaco(escopo, i, isFuncao);
        } else if (isFor) {
            eFor(escopo.get(0), isFuncao);
            i++;
        }
        while (i < escopo.size()) {
            //Se achar um = ou vetor
            if (escopo.get(i).getTipo().equals("atrib") || escopo.get(i).getTipo().equals("vet")) {
                //se achar =
                if (escopo.get(i).getTipo().equals("atrib")) {
                    if (escopo.get(i - 1).getTipo().equals("id") || escopo.get(i - 1).getTipo().equals("]")) {
                        Lexema nomeVet = new Lexema();
                        if (escopo.get(i - 1).getTipo().equals("]")) {
                            nomeVet = nomeVetor(i - 1, escopo);
                            analisaVetor(i - 1, escopo, isFuncao);
                        }
                        boolean declarada = false;
                        // verifico o tipo da variavel
                        String tipo = verificaTipoVariavel(expressao(escopo, i), isFuncao);
                        //verifico o id da variavel (vetor, matriz, id...)
                        String identificador = verificaIdentificador(i - 1, escopo);
                        // procuro na tabela de variaveis do escopo

                        if (identificador.equals("vetor") || identificador.equals("matriz")) {
                            Lexema lexema = PesquisaListaTokens(nomeVet, isFuncao);
                            if (lexema != null) {
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
                                    AtualizaLinhaToken(lexema, isFuncao, escopo.get(i));
                                }
                            }
                        } else {
                            Lexema lexema = PesquisaListaTokens(escopo.get(i - 1), isFuncao);
                            if (lexema != null) {
                                declarada = true;
                                if ((lexema.getTipo().equals("Int") || lexema.getTipo().equals("Float")) && !(tipo.equals("Int") || tipo.equals("Float"))) {
                                    System.out.println("Erro: Variavel " + lexema.getNome() + " inicializada como " + lexema.getTipo() + ". Linha  " + escopo.get(i - 1).getLinha());
                                    System.exit(0);
                                } else if (!lexema.getTipo().equals(tipo) && !lexema.getNovoTipo().equals("fun") && !((lexema.getTipo().equals("Int") || (lexema.getTipo().equals("Float"))))) {
                                    System.out.println("Erro: Variavel " + lexema.getNome() + " inicializada como " + lexema.getTipo() + ". Linha  " + escopo.get(i - 1).getLinha());
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
                                    AtualizaLinhaToken(lexema, isFuncao, escopo.get(i));
                                }
                            }
                        }
                        //se for um vetor, verifica se ele foi declarado
                        boolean foiDeclarado = false;
                        Lexema nome = new Lexema();
                        if (identificador.equals("vetor") || identificador.equals("matriz")) {
                            nome = nomeVetor(i - 1, escopo);
                        }
                        //procuro na tabela de vetores declarados
                        foiDeclarado = AchaVetoresDeclarados(escopo, i, isFuncao, nome, identificador);
                        if ((identificador.equals("vetor") || identificador.equals("matriz")) && foiDeclarado == false) {
                            System.out.println("Variavel " + nome.getNome() + " nao foi declarada. Linha  " + escopo.get(i).getLinha());
                            System.exit(0);
                        }
                        //}
                        // se nao encontrar, insiro na lista do escopo
                        if (declarada == false) {
                            Lexema var = new Lexema();
                            if (escopo.get(i - 1).getNome().equals("]")) {
                                var.setNome(nome.getNome());
                            } else {
                                var.setNome(escopo.get(i - 1).getNome());
                            }
                            var.setLinha(escopo.get(i).getLinha());
                            var.setNovoTipo(identificador);
                            var.setTipo(tipo);
//                            varEscopo.peek().add(var);
                            InserirListaTokens(var, isFuncao);
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
                    // se o vetor ja foi declarado na lista de declaracoes visiveis
                    Lexema lexema = PesquisaListaDecla(escopo.get(i + 1), isFuncao);
                    if (lexema != null) {
                        vetDeclarado = true;
                        System.out.println("Erro: variavel ja foi declarada. linha " + escopo.get(i).getLinha());
                        System.exit(0);
                    }
                    Lexema lexema2 = PesquisaListaTokens(escopo.get(i + 1), isFuncao);
                    if (lexema2 != null) {
                        System.out.println("Erro: variavel " + lexema2.getNome() + " ja foi inicializada. Linha: " + escopo.get(i + 1).getLinha());
                        System.exit(0);
                    }
                    //se o vetor nao foi declarado, eu o insiro na lista de declaracoes de vetor do escopo
                    if (vetDeclarado == false) {
                        String nomeVet = verificaIdentificador(i + 1, escopo);
                        Lexema declaracao = new Lexema();
                        declaracao.setNome(escopo.get(i + 1).getNome());
                        declaracao.setLinha(escopo.get(i).getLinha());
                        declaracao.setNovoTipo(nomeVet);
                        InserirListaDecla(declaracao, isFuncao);
                    }
                }

            } // se encontrar um laço 
            else if (IsLaco(escopo.get(i))) {
                cont++;
                pai.add(cont);
                i = AnalisaLaco(i, escopo, isFuncao, pai, primAcesso);
                pai.remove(pai.size() - 1);
            } // se encontrar o token acabou, entao desempilha o ultimo arraylist da tabela de escopo 
            else if (escopo.get(i).getTipo().equals("acabou")) {
//                System.out.println("Escopo nao-principal: ");
//                for (ArrayList<Lexema> decVetProg1 : varEscopo) {
//                    for (Lexema decVetProg11 : decVetProg1) {
//                        System.out.println(decVetProg11.getNome() + " "+decVetProg11.getNovoTipo()+":" + decVetProg11.getTipo());
//                    }
//                }
                if ((isFuncao && primAcesso) || (!isFuncao && !primAcesso)) {
                    salvaTokens(isFuncao, pai);
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
                FinalizaEscopo(isFuncao);
            }
            i++;
        }

    }

    public void Analisa() {
        ArrayList<Lexema> escopoProg = new ArrayList<>();
        boolean isFuncao = false;
        boolean existeFuncao = false;
        for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : lexemas.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Lexema> value = entrySet.getValue();
            for (Lexema value1 : value) {
                if (value1.getNome().equals("fim")) {
                    ArrayList<Integer> array = new ArrayList<>();
                    array.add(1);
                    escopoProg(escopoProg, false, false, false, false, array, false);
                    salvaTokens(false, array);
                    escopoProg.clear();
                } else if (value1.getTipo().equals("function")) {
                    existeFuncao = true;
                    isFuncao = true;
                    ArrayList<Integer> array = new ArrayList<>();
                    array.add(1);
                    escopoProg(escopoProg, false, false, false, false, array, false);
                    salvaTokens(false, array);
                    escopoProg.clear();
                } else if (value1.getTipo().equals("endfunction")) {
                    isFuncao = false;
                }
                if (!isFuncao) {
                    escopoProg.add(value1);
                }
            }
        }
        System.out.println("Variaveis Programa");
        for (Lexema escopoProg1 : tabTokensProg) {
            System.out.print("Nome: " + escopoProg1.getNome() + "  Escopo: ");
            for (Integer esc : escopoProg1.getEscopo()) {
                System.out.print(esc + " ");
            }
            System.out.print(" Tipo: " + escopoProg1.getTipo() + " Linha: " + escopoProg1.getLinha() + " -> " + escopoProg1.getLinhaAtual());
            System.out.println("");
        }
        System.out.println("");
        System.out.println("Variaveis Funcao");
        for (Lexema escopoProg1 : tabTokensFun) {
            System.out.print("Nome: " + escopoProg1.getNome() + "  Escopo: ");
            for (Integer esc : escopoProg1.getEscopo()) {
                System.out.print(esc + " ");
            }
            System.out.println(" Tipo: " + escopoProg1.getTipo() + " Linha: " + escopoProg1.getLinha() + " -> " + escopoProg1.getLinhaAtual());
        }

        Otimizacao otim = new Otimizacao(tabTokensProg, tabTokensFun, this.arvore, this.lexemas);
        otim.analisa();
    }
}
