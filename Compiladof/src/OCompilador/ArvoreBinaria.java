package OCompilador;
import OCompilador.Lexema;
import javax.swing.JOptionPane;

public class ArvoreBinaria {

    protected Nodo raiz; // no raiz da arvore
    protected Nodo pt; // no de percurso
    protected Nodo aux;// no auxiliar de percurso (guarda o ultimo no acessado)
    protected int pos; // posicao do no, 1 = esquerda, 2 = direita
    protected int tamanho;//guarda a quantidade de nos na arvore(quando o metodo tamanho() e chamado)
    protected String print; //atributo para impressao da arvore

    public ArvoreBinaria() {
        this.raiz = null;
        this.pt = null;
        this.aux = null;
        this.tamanho = 0;
        this.print = " ";

    }

    public ArvoreBinaria(Lexema token) {
        this.raiz = new Nodo(token);
        this.pt = raiz;
        this.tamanho = 0;
        this.print = " ";
    }
    
    public Nodo raiz() {
        return this.raiz;
    }
    //retorna o pai

    public Nodo pai(Nodo x) {
        if (x == raiz) {
            return null;
        } else {
            return x.pai;
        }
    }
    //retorna numero de filhos 

    public int numFilhos(Nodo p) {
        int f = 0;
        if (p.esq != null) {
            f++;
        }
        if (p.dir != null) {
            f++;
        }
        return f;
    }
    //verifica se no e raiz

    public boolean eRaiz(Nodo n) {
        if (raiz == n) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean eVazia() {
        if (raiz == null) {
            return true;
        } else {
            return false;
        }
    }
    //Retorna numero de pais

    public int profundidade(Nodo x) {
        int prof = 0;
        Nodo p = x;
        while (p.pai != null) {
            prof++;
            p = p.pai;
        }
        return prof;
    }
    //retorna esquerdo

    public Nodo filhoEsquerdo(Nodo p) {
        if (p.esq == null) {
            return null;
        } else {
            return p.esq;
        }
    }
    //retorna direito

    public Nodo filhoDireito(Nodo p) {
        if (p.dir == null) {
            return null;
        } else {
            return p.dir;
        }
    }
    //retorna vizinho de p

    public Nodo irmao(Nodo p) {

        if (p == raiz) {
            return null;
        }
        else if (numFilhos(p.pai) == 1) {
            return null;
        }
        else {
            if (p.pai.dir == p) {
                return p.pai.esq; 
            } else {
                return p.pai.dir;
            }
        }

    }
   
    public Nodo buscarElemento(Lexema t) {
        if (raiz == null) {
            pos = 0;
            return raiz;
        }
        if (pt == null) {
            return null;
        }
        else if (pt.token == t) {
            return pt;
        } 
        else if (pt.esq != null) {
            aux = pt;
            pt = pt.esq;
            aux.esq = pt;
            pos = 1; 
            return buscarElemento(t);
        }
        else {
            aux = pt;
            pt = pt.dir;
            aux.dir = pt;
            pos = 2;
            return buscarElemento(t);
        }
    }
    
    public String exibirArvore(){
		if (raiz == null)return ""; 
		Nodo p = this.raiz; // recebe o no raiz
		print = print + (p.token.getNome())+ " ";
		if (p.esq != null){print = print + "(" + exibirArvore(p.esq)+")";}
		if (p.dir != null){print = print + "(" + exibirArvore(p.dir)+ ")";}
		String pr =print;
		print = " ";
		return pr;
	}
    private String exibirArvore(Nodo p){
		print = print + (p.token.getNome())+ " ";
		if (p.esq != null){print = print + "( " + exibirArvore(p.esq) + ")";}
		if (p.dir != null){print = print + "(" + exibirArvore(p.dir)+ ")";}
		String pr = print;
		print = " ";
		return pr;
	}

    public boolean inserirElemento(Lexema t, int posi) {
        Nodo p = null;
        this.pt = this.raiz;
        p = buscarElemento(t);

        if (p != null) {
            return false;
        }
        else {
            p = new Nodo(t);
            if (pos == 0) {
                this.raiz = p;
            } 
            else if (posi == 1) {
                p.pai = aux;
                aux.esq = p;
            } else if (posi == 2) {
                p.pai = aux;
                aux.dir = p;
            }
        }
        pos = 4;
        pt = raiz; 
        System.out.println(exibirArvore()); 
        return true;
    }
}

