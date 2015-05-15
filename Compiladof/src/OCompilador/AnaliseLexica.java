/*
 * To change this license header, choose License Headers in Project Properties.
 hh * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OCompilador;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author Daniel
 */
public class AnaliseLexica {

    HashMap<String, String> lexema = new HashMap<>();
    LinkedHashMap<Integer, ArrayList<Lexema>> tokens = new LinkedHashMap<>();
    ArrayList<Lexema> tokenList = null;
    Lexema simbolos = new Lexema();
    Stack pilha = new Stack();
    Scanner scanner = null;
    String codigo = "";

    public AnaliseLexica(String txt) throws FileNotFoundException {
        //Lendo o arquivo teste
        FileReader teste = new FileReader(txt);
        scanner = new Scanner(teste);
        //Adicionando em uma tabela tds os lexemas conhecidos
        lexema.put("+", "sum");
        lexema.put("-", "sub");
        lexema.put("*", "mult");
        lexema.put("/", "div");
        lexema.put(":", "div");
        lexema.put("[", "[");
        lexema.put("]", "]");
        lexema.put("(", "(");
        lexema.put(")", ")");
        lexema.put(",", ",");
        lexema.put(">", "gt");
        lexema.put(">=", "gte");
        lexema.put("=>", "gte");
        lexema.put("<", "lt");
        lexema.put("=<", "lte");
        lexema.put("<=", "lte");
        lexema.put("==", "eq");
        lexema.put("!=", "neq");
        lexema.put("=", "atrib");
        lexema.put("int", "int");
        lexema.put("float", "float");
        lexema.put("str", "str");
        lexema.put("var", "id");
        lexema.put("var", "id");
        lexema.put("vetor", "vet");
        lexema.put("se", "cond");
        lexema.put("então", "initcond");
        lexema.put("senão", "altcond");
        lexema.put("fim-se", "endcond");
        lexema.put("e", "and");
        lexema.put("para", "forloop");
        lexema.put("de", "rng1forloop");
        lexema.put("até", "rng2forloop");
        lexema.put("faça", "initforloop");
        lexema.put("fim-para", "endforloop");
        lexema.put("enquanto", "whileloop");
        lexema.put("fim-enquanto", "endwhileloop");
    }

    public boolean ValidaLetra(char letra) {
        return (Character.isLetter(letra));
    }

    public boolean ValidaHifen(char simbolo, int indice) {
        return codigo.charAt(indice) == '-' && ValidaLetra(codigo.charAt(indice - 1)) && ValidaLetra(codigo.charAt(indice + 1));
    }

    public boolean ValidaNumero(char simbolo) {
        return Character.isDigit(simbolo);
    }

    public void Analisar() throws IOException {
        String token = "";
        int linha = 0;
        boolean comentario = false;
        boolean funcao = false;
        boolean and = false;
        
        //Coloco toda a linha na variavel codigo
        while (scanner.hasNext()) {
            tokenList = new ArrayList<Lexema>();
            linha++;
            codigo = scanner.nextLine();
            
            //Analiso caracter por caracter da linha
            for (int i = 0; i < codigo.length(); i++) {
                String simbolo = "";
                simbolo = simbolo + codigo.charAt(i);
                
                //Retira comentarios
                if (codigo.charAt(i) == '#') {
                    if (comentario == false) {
                        comentario = true;
                    } else {
                        comentario = false;
                    }
                }
                //Analisa Aspas
                else if (codigo.charAt(i) == '"') {
                    token = "";
                    i++;
                    while(i<codigo.length() && codigo.charAt(i)!='"'){
                        token = token+codigo.charAt(i);
                        i++;
                        if(i==codigo.length() && scanner.hasNext()){
                            linha++;
                            codigo = scanner.nextLine();
                            i=0;
                        }        
                    }
                    if(comentario==false){
                        simbolos.setTipo("String");
                    simbolos.setNome(token);
                    tokenList.add(simbolos);
                    }
                    token = "";
                    simbolo = "";
                } //Analisa comparadores e atrbuição
                else if ((codigo.charAt(i) == '=' || codigo.charAt(i) == '<' || codigo.charAt(i) == '>' || codigo.charAt(i) == '!') && comentario == false) {
                    //Analisa qual o proximo simbolo para saber se pode ser <=,=<,...
                    String simboloProximo = "";
                    if ((i + 1) < codigo.length() && (codigo.charAt(i + 1) == '<' || codigo.charAt(i + 1) == '>' || codigo.charAt(i + 1) == '=')) {
                        simboloProximo = simboloProximo + codigo.charAt(i) + codigo.charAt(i + 1);
                        if (lexema.containsKey(simboloProximo)) {
                            token = token + simboloProximo;
                            i++;
                        } else {
                            token = token + codigo.charAt(i);
                        }
                    } else {
                        token = token + codigo.charAt(i);
                    }
                    simbolos.setTipo(lexema.get(token));
                    simbolos.setNome(token);
                    tokenList.add(simbolos);
                    token = "";
                    simbolo = "";
                    simboloProximo = "";
                } //Identifica se x eh multiplicação
                else if (codigo.charAt(i) == 'x' && comentario == false && i > 0 && (i + 1) < codigo.length() && ((codigo.charAt(i - 1) == ' ' && codigo.charAt(i + 1) == ' ') || (ValidaNumero(codigo.charAt(i - 1)) && ValidaNumero(codigo.charAt(i + 1))) || (codigo.charAt(i + 1) == '(' && (codigo.charAt(i - 1) == ' ' || ValidaNumero(codigo.charAt(i - 1)))) || (codigo.charAt(i - 1) == ')' && (codigo.charAt(i + 1) == ' ' || ValidaNumero(codigo.charAt(i + 1)) || codigo.charAt(i + 1) == '(')) || (codigo.charAt(i + 1) == '(' && (codigo.charAt(i - 1) == ' ' || ValidaNumero(codigo.charAt(i - 1)))))) {
                    simbolos.setTipo("mult");
                    simbolos.setNome("x");
                    tokenList.add(simbolos);
                    token = "";
                }// se x isolado, entao eh uma multiplicação
                else if (((codigo.length() == 1 && codigo.charAt(i)=='x') || ((i==0) && codigo.length() >= 2 && codigo.charAt(i)=='x' && codigo.charAt(i+1)==' ') || ((i>0) && codigo.length() == (i+1) && codigo.charAt(i)=='x' && codigo.charAt(i-1)==' ')) && comentario == false) {
                simbolos.setTipo("mult");
                simbolos.setNome("x");
                tokenList.add(simbolos);
                token = "";
                }
                else if(i==0 && codigo.charAt(i)=='x' && (i+1)<codigo.length() && codigo.charAt(i+1)=='(' && comentario==false){
                    simbolos.setTipo("mult");
                    simbolos.setNome("x");
                    tokenList.add(simbolos);
                    token = "";
                }
                  //Analise numeros e pontos flutuantes, verificando se a frente do ponto ou virgula
                  //tem numero
                else if (ValidaNumero(codigo.charAt(i)) && comentario == false) {
                    boolean inteiro = true;
                    do {
                        token = token + codigo.charAt(i);
                        i++;
                        if (i < codigo.length() && (codigo.charAt(i) == '.' || codigo.charAt(i) == ',') && (i + 1) < codigo.length() && codigo.charAt(i + 1) >= 48 && codigo.charAt(i + 1) <= 57 && inteiro == true) {
                            if (((funcao == true || ValidaLetra(token.charAt(0))) && codigo.charAt(i) == ',') || (ValidaLetra(token.charAt(0)) && codigo.charAt(i)=='.')) {}
                            else {
                                token = token + codigo.charAt(i);
                                i++;
                                inteiro = false;
                            }
                        }
                    } while (i < codigo.length() && ValidaNumero(codigo.charAt(i)));
                    i--;
                    //se o token que tiver palavra começar com letra e tiver como proximo caracter letra
                    //entao eu leio enquanto for letra ou numero
                    if (!token.isEmpty() && ValidaLetra(token.charAt(0)) && (i+1)<codigo.length() && ValidaLetra(codigo.charAt(i+1))) {
                        i++;
                        while(i<codigo.length() && (ValidaLetra(codigo.charAt(i)) || ValidaNumero(codigo.charAt(i)))){
                            token = token + codigo.charAt(i);
                            i++;
                        }
                        i--;
                        simbolos.setTipo("id");
                        simbolos.setNome(token);
                        tokenList.add(simbolos);
                        token = "";
                    } //se começa com letra e ta no final da linha ou começa com letra e proximo
                      //caracter nao eh letra ou numero, entao adiciona como id
                    else if(((i+1)==codigo.length() && ValidaLetra(token.charAt(0))) || (((i+1)<codigo.length() && !ValidaLetra(codigo.charAt(i+1)) && !ValidaNumero(codigo.charAt(i+1)))  && ValidaLetra(token.charAt(0)))){
                        simbolos.setTipo("id");
                        simbolos.setNome(token);
                        tokenList.add(simbolos);
                        token = "";
                    }//se numero inteiro, entao adiciona como int
                    else if (inteiro == true) {
                        simbolos.setTipo("Int");
                        simbolos.setNome(token);
                        tokenList.add(simbolos);
                        token = "";
                    }//se numero eh float, entao adiciona como float
                    else {
                        simbolos.setTipo("Float");
                        simbolos.setNome(token);
                        tokenList.add(simbolos);
                        token = "";
                    }
                } //Analisa se - é um hifen de palavra reservada
                else if (codigo.charAt(i) == '-' && (i + 1) < codigo.length() && token.equals("fim") && comentario == false) {
                    token = token + codigo.charAt(i);
                } //Analisa o´. de concatenação
                else if (codigo.charAt(i) == '.' && comentario == false) {
                    simbolos.setTipo(".");
                    simbolos.setNome(".");
                    tokenList.add(simbolos);
                    token = "";
                }//Se ler (, ele verifica se o ultimo lexema eh um id, se for adiciona seu tipo
                 //como fun e seta funcao como true, senao, adiciona funcao como false
                else if (codigo.charAt(i) == '(' && i > 0 && comentario == false) {
                    int k = i - 1;
                    while (k > 0 && codigo.charAt(k) == ' ') {
                        k--;
                    }
                    if (!ValidaLetra(codigo.charAt(k)) && !ValidaNumero(codigo.charAt(k))) {
                        funcao = false;
                        pilha.push("(");
                    } else if (!tokenList.isEmpty() && tokenList.get(tokenList.size() - 1).getTipo() == "id") {
                        tokenList.get(tokenList.size() - 1).setTipo("fun");
                        pilha.push("((");
                        funcao = true;
                    } else{
                        pilha.push("(");
                        funcao = false;
                    }
                    simbolos.setTipo("(");
                    simbolos.setNome("(");
                    tokenList.add(simbolos);
                    token = "";
                } //Analisa se é um ) e se o antepenultimo ( da pilha eh d uma funcao, entao
                  //a funcao eh setada como true
                else if (codigo.charAt(i) == ')' && pilha.size() >= 2 && pilha.get(pilha.size() - 2).equals("((") && comentario == false) {
                    pilha.pop();
                    simbolos.setTipo(")");
                    simbolos.setNome(")");
                    tokenList.add(simbolos);
                    token = "";
                    funcao = true;
                }//Analisa se é um ) e se so existe um elemento na pilha, entao a funcao é setada como false
                else if (codigo.charAt(i) == ')' && pilha.size() == 1 && comentario == false) {
                    pilha.pop();
                    simbolos.setTipo(")");
                    simbolos.setNome(")");
                    tokenList.add(simbolos);
                    token = "";
                    funcao = false;
                } //Analisa qualquer simbolo unico na tabela de lexemas
                else if (codigo.charAt(i) != 'e' && codigo.charAt(i) != 'x' && lexema.containsKey(simbolo) && comentario == false) {
                    //se for (, entao a funcao é setada como falsa
                    if (codigo.charAt(i) == '(') {
                        pilha.push("(");
                        simbolos.setTipo("(");
                        simbolos.setNome("(");
                        tokenList.add(simbolos);
                        token = "";
                        funcao = false;
                    } //se é ), retira um elemento da pilha
                    else if (codigo.charAt(i) == ')') {
                        if (!pilha.isEmpty()) {
                            pilha.pop();
                        }
                        simbolos.setTipo(")");
                        simbolos.setNome(")");
                        tokenList.add(simbolos);
                        token = "";
                        funcao=false;
                    } else {
                        simbolos.setTipo(lexema.get(simbolo));
                        simbolos.setNome(simbolo);
                        tokenList.add(simbolos);
                        token = "";
                    }
                }//Analisa palavras reservadas da linguagem como: se, para, então...
                else if ((ValidaLetra(codigo.charAt(i)) || ValidaNumero(codigo.charAt(i))) && comentario == false) {
                    token = token + codigo.charAt(i);
                    //Analisa se a palavra esta na tabela de lexemas e se o proximo token
                    //é um caracterer especial
                    if (lexema.containsKey(token) && ((codigo.length() > (i + 1) && ((!ValidaLetra(codigo.charAt(i + 1)))) && !ValidaNumero(codigo.charAt(i+1))) || (codigo.length() == (i + 1)))) {
                        simbolos.setTipo(lexema.get(token));
                        simbolos.setNome(token);
                        tokenList.add(simbolos);
                        token = "";
                    }
                    String hifen = "";
                    //Analisa se a acabou a palavra e se ela é um var
                    //se proximo token nao eh letra e nem numero, adiciono-o
                    if (!token.equals(" ") && !token.equals("") && (i + 1) < codigo.length() && !ValidaLetra(codigo.charAt(i + 1)) && !ValidaNumero(codigo.charAt(i + 1))) {
                        //se token eh fim, e o proximo eh -, entao eu verifico se eh uma palavra reservada
                        //se nao for, eu adiciono fim como id
                        if(token.equals("fim") && codigo.charAt(i+1)=='-'){
                            int k=i+2;
                            while(k<codigo.length() && (ValidaLetra(codigo.charAt(k)) || ValidaNumero(codigo.charAt(k)))){
                                hifen = hifen + codigo.charAt(k);
                                k++;
                            }
                            k--;
                            if(lexema.containsKey(token+'-'+hifen) && (((k+1)<codigo.length() && (!ValidaLetra(codigo.charAt(k+1)) || !ValidaNumero(codigo.charAt(k+1)))) || (k+1)==codigo.length())){
                                simbolos.setTipo(lexema.get(token+'-'+hifen));
                                simbolos.setNome(token+'-'+hifen);
                                tokenList.add(simbolos);
                                token = "";
                                i=k;
                            } else{
                                simbolos.setTipo("id");
                                simbolos.setNome(token);
                                tokenList.add(simbolos);
                                token = "";
                            }
                        } else{
                        simbolos.setTipo("id");
                        simbolos.setNome(token);
                        tokenList.add(simbolos);
                        token = "";
                        }
                    }
                    //Analisa se for a ultima palavra, entao eh adicionada como variavel
                    if (!token.equals(" ") && !token.equals("") && (i + 1) == codigo.length()) {
                        simbolos.setTipo("id");
                        simbolos.setNome(token);
                        tokenList.add(simbolos);
                        token = "";
                    }
                }//se o caracter nao eh numero e nem letra e nao eh espaço em branco ou TAB, eu o adiciono
                 //como um simbolo comum
                else if (comentario == false && !ValidaLetra(codigo.charAt(i)) && !ValidaNumero(codigo.charAt(i)) && codigo.charAt(i) != ' ' && codigo.charAt(i)!=9) {
                    if (!(linha == 1 && i == 0)) {
                        simbolos.setTipo(simbolo);
                        simbolos.setNome(simbolo);
                        tokenList.add(simbolos);
                        token = "";
                    }
                } else {
                }
                simbolos = new Lexema();
            }
            tokens.put(linha, tokenList);
        }
        //for para exibir tokens em terminal
        for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : tokens.entrySet()) {
            Integer key = entrySet.getKey();
            ArrayList<Lexema> value = entrySet.getValue();
            System.out.print(key);
            for (Lexema value1 : value) {
                System.out.print(" <" + value1.getTipo() + "," + value1.getNome() + ">");
            }
            System.out.println("");
        }
        AnaliseSintatica analisador = new AnaliseSintatica(tokens);
        analisador.Analisa();
        //Escrevendo em arquivo teste3
        /*try (FileWriter arq = new FileWriter("teste3.txt")) {
            PrintWriter gravarArq = new PrintWriter(arq);
            for (Map.Entry<Integer, ArrayList<Lexema>> entrySet : tokens.entrySet()) {
                Integer key = entrySet.getKey();
                ArrayList<Lexema> value = entrySet.getValue();
                gravarArq.print(key);
                for (Lexema value1 : value) {
                    gravarArq.print(" <"/* + value1.getTipo() + "," + value1.getNome() + ">");
                }
                gravarArq.println("");
            }
        }*/
    }
}
