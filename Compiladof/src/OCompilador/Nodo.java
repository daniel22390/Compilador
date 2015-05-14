package OCompilador;


import OCompilador.Lexema;

public class Nodo {
	public Lexema token;//atributo que guarda token
	public Nodo dir;//atributo que aponta para o no esquerdo
	public Nodo esq;//atributo que aponta para o no direito
	public Nodo pai;//atributo que aponta para o pai do no
	
	
	public Nodo(Lexema t){
		this.token = t;
		this.dir = null;
		this.esq = null;
		this.pai = null;
	}
	public Nodo(){}

	public Nodo(Lexema t,Nodo esq,Nodo dir){
		this.token = t;
		this.dir = dir;
		this.esq = esq;
		this.pai = null;
		this.dir.pai = this;
		this.esq.pai =this;

	}
        
	public Nodo elemento(){
		return this;	
	}
}
