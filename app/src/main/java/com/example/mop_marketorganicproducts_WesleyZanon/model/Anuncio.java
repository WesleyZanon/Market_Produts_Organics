package com.example.mop_marketorganicproducts_WesleyZanon.model;

import com.example.mop_marketorganicproducts_WesleyZanon.helper.configuracaoFireBase;
import com.google.firebase.database.DatabaseReference;

import java.util.List;


public class Anuncio {

    private String idAnuncio;
    private String estado;
    private String cidade;
    private String categoria;
    private String titulo;
    private String valor;
    private String descricao;
    private List<String> fotos;




    public Anuncio() {

        //criar referencia de fotos
        DatabaseReference anuncioRef = configuracaoFireBase.getFirebase().child("meus_anuncios");
        setIdAnuncio(anuncioRef.push().getKey());
    }

    public void salvar(){

        //id do usuario
        String idUsuario = configuracaoFireBase.getIdUsuario();

        //recuperar referencia
        DatabaseReference anuncioRef = configuracaoFireBase.getFirebase().child("meus_anuncios");

        //salvar anuncio atraves da idUsuario e idAnuncio
        anuncioRef.child(idUsuario).child(getIdAnuncio()).setValue(this);

        //salvar anuncios para publico
        salvarPublico();

    }

    public void salvarPublico(){


        //recuperar referencia
        DatabaseReference anuncioRef = configuracaoFireBase.getFirebase().child("anuncios");

        //recuperar id do anuncio
        anuncioRef.child(getEstado())
                .child(getCidade())
                .child(getCategoria())
                .child(getIdAnuncio())
                .setValue(this);

    }

    public void removerAnuncio(){

        //anuncio usuario
        //id do usuario
        String idUsuario = configuracaoFireBase.getIdUsuario();

        //recuperar referencia
        DatabaseReference anuncioRef = configuracaoFireBase.getFirebase().child("meus_anuncios")
                .child(idUsuario).child(getIdAnuncio());

        anuncioRef.removeValue();

        removerAnuncioPublico();

    }

    public void removerAnuncioPublico(){

        //anuncio publico

        //recuperar referencia
        DatabaseReference anuncioRef = configuracaoFireBase.getFirebase().child("anuncios").child(getEstado()).child(getCidade()).child(getCategoria()).child(getIdAnuncio());

        anuncioRef.removeValue();

    }


    //get e set de todos os objetos
    public String getIdAnuncio() {
        return idAnuncio;
    }
    public void setIdAnuncio(String idAnuncio) {
        this.idAnuncio = idAnuncio;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getValor() {
        return valor;
    }
    public void setValor(String valor) {
        this.valor = valor;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public List<String> getFotos() {
        return fotos;
    }
    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }



}
