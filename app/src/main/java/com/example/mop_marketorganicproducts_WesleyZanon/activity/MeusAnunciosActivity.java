package com.example.mop_marketorganicproducts_WesleyZanon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.mop_marketorganicproducts_WesleyZanon.R;

import com.example.mop_marketorganicproducts_WesleyZanon.helper.RecyclerItemClickListener;
import com.example.mop_marketorganicproducts_WesleyZanon.helper.configuracaoFireBase;
import com.example.mop_marketorganicproducts_WesleyZanon.adapter.adapterAnuncios;


import com.example.mop_marketorganicproducts_WesleyZanon.model.Anuncio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private com.example.mop_marketorganicproducts_WesleyZanon.adapter.adapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        //Configurações iniciais
        anuncioUsuarioRef = configuracaoFireBase.getFirebase()
                .child("meus_anuncios")
                .child( configuracaoFireBase.getIdUsuario() );

        inicializarComponentes();


        //Configurar RecyclerView
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios = new adapterAnuncios(anuncios, this);
        recyclerAnuncios.setAdapter( adapterAnuncios );

        //Recupera anúncios para o usuário
        recuperarAnuncios();

        //adicionar evento de click
        recyclerAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerAnuncios, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        //pegar anuncio pela posição
                        Anuncio anuncioSelecionado = anuncios.get(position);
                        anuncioSelecionado.removerAnuncio();

                        adapterAnuncios.notifyDataSetChanged();

                        startActivity(new Intent(getApplicationContext(),AnunciosActivity.class));
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                })
        );

    }

    private void recuperarAnuncios(){

        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                anuncios.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    anuncios.add(ds.getValue(Anuncio.class));
                }

                Collections.reverse(anuncios);
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void inicializarComponentes(){

        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);

    }

    public void CadastrarAnuncio(View view){
        startActivity(new Intent(getApplicationContext(), CadastrarAnunciosActivity.class));
    }

}