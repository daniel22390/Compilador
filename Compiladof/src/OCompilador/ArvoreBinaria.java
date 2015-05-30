package OCompilador;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


public class ArvoreBinaria<T> implements Serializable{
    
    private ArvoreBinaria<T> esq, dir;
    private T nodo;
    
    public ArvoreBinaria(T node){
        esq = null;
        dir = null;
        nodo = node;
    }
    
    public void setNodo(T x){
        nodo = x;
    }
    
    public T getNodo(){
        return nodo;
    }

    public ArvoreBinaria<T> getEsq() {
        return esq;
    }

    public void setEsq(ArvoreBinaria<T> esq) {
        this.esq = esq;
    }

    public ArvoreBinaria<T> getDir() {
        return dir;
    }

    public void setDir(ArvoreBinaria<T> dir) {
        this.dir = dir;
    }
    
    public ArrayList<T> inOrdem(){
        ArrayList<T> lista = new ArrayList<>();
        if(esq != null){
            ArrayList<T> x = esq.inOrdem();
            for(T i: x){
                lista.add(i);
            }
        }
        if(nodo != null)
            lista.add(nodo);
        if(dir != null){
            ArrayList<T> x = dir.inOrdem();
            for(T i: x){
                lista.add(i);
            }
        }
        return lista;
    }
    
    @Override
    public String toString(){
        return nodo.toString();
    }
    
    protected LinkedList<ArvoreBinaria<T>> folhas(int altura){
        LinkedList<ArvoreBinaria<T>> fila = new LinkedList<>();
        if(altura == 0)
            fila.add(this);
        else{
            LinkedList<ArvoreBinaria<T>> fAux;
            
            if(esq != null)
                fAux = esq.folhas(altura-1);
            else{
                fAux = new LinkedList<>();
                fAux.add(null);
            }
            for (ArvoreBinaria<T> fila1 : fAux) {
                fila.add(fila1);
            }
            
            if(dir != null)
                fAux = dir.folhas(altura-1);
            else{
                fAux = new LinkedList<>();
                fAux.add(null);
            }
            for (ArvoreBinaria<T> fila1 : fAux) {
                fila.add(fila1);
            }
        }
        return fila;
    }
    
    public int altura(){
        int hEsq = 0, hDir = 0;
        if(esq != null)
            hEsq = esq.altura();
        if(dir != null)
            hDir = dir.altura();
        return 1 + (hEsq > hDir ? hEsq : hDir);
    }
}
