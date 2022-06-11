package com.example.mop_marketorganicproducts_WesleyZanon.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


import com.example.mop_marketorganicproducts_WesleyZanon.R;
import com.example.mop_marketorganicproducts_WesleyZanon.adapter.adapterAnuncios;
import com.example.mop_marketorganicproducts_WesleyZanon.helper.configuracaoFireBase;

import com.example.mop_marketorganicproducts_WesleyZanon.model.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth  autenticacao =  configuracaoFireBase.getReferenciaAutenticacao();

    private Button buttonEstado, buttonCidade, buttonCategoria;
    private RecyclerView recyclerAnunciosPublicos;
    private adapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private String filtroEstado = "";
    private String filtroCidade = "";
    private boolean filtrandoPorEstado = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        //autenticacao.signOut();

        anunciosPublicosRef = configuracaoFireBase.getFirebase()
                .child("anuncios");


        inicializarComponentes();

        //Configurar RecyclerView
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new adapterAnuncios(listaAnuncios, this);
        recyclerAnunciosPublicos.setAdapter( adapterAnuncios );

        recuperarAnunciosPublicos();

    }

    public void recuperarAnunciosPublicos(){

        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //percorrer cada no do banco de dados

                for(DataSnapshot estados : snapshot.getChildren()){
                    for (DataSnapshot cidades : estados.getChildren()){
                        for(DataSnapshot categorias : cidades.getChildren()){
                            for(DataSnapshot anuncios : categorias.getChildren()){

                                Anuncio anuncio = anuncios.getValue(Anuncio.class);
                                listaAnuncios.add(anuncio);

                            }
                        }

                    }

                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(autenticacao.getCurrentUser() == null){ //caso usuario nao esteja cadastrado
            menu.setGroupVisible(R.id.group_deslogado, true); //exibir grupo para deslogado

        }else{
            menu.setGroupVisible(R.id.group_logado, true); //exibir grupo de usuario logado
        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_cadastrar:
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;

            case R.id.menu_sair:
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;

            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;

            case R.id.cadastrar_anuncio:
                startActivity(new Intent(getApplicationContext(), CadastrarAnunciosActivity.class));
                break;

           /* case R.id.menu_favoritos:
                startActivity(new Intent(getApplicationContext(), activity_favoritos.class));*/
        }

        return super.onOptionsItemSelected(item);
    }

    public void inicializarComponentes(){

        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);
        buttonEstado = findViewById(R.id.buttonEstado);
        buttonCidade = findViewById(R.id.buttonCidade);


    }

    public void filtrarEstado(View view){

        AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
        dialogEstado.setTitle("Selecione um estado");

        //spinner

        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        final Spinner campoEstado = viewSpinner.findViewById(R.id.spinnerFiltro);


        String[] estados = getResources().getStringArray(R.array.estados);

        ArrayAdapter<String> adapterEstado = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, estados );

        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapterEstado);

        dialogEstado.setView(viewSpinner);

        dialogEstado.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filtroEstado = campoEstado.getSelectedItem().toString();
                recuperarAnunciosPorEstado();
                filtrandoPorEstado = true;
            }
        });

        dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = dialogEstado.create();
        dialog.show();

    }

    public void recuperarAnunciosPorEstado(){

        //Configura nó por estado
        anunciosPublicosRef = configuracaoFireBase.getFirebase()
                .child("anuncios")
                .child(filtroEstado);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaAnuncios.clear();
                for (DataSnapshot categorias: dataSnapshot.getChildren() ){
                    for(DataSnapshot anuncios: categorias.getChildren() ){

                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add( anuncio );

                    }
                }

                Collections.reverse( listaAnuncios );
                adapterAnuncios.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    public void filtrarCidade(View view){

        AlertDialog.Builder dialogCidade = new AlertDialog.Builder(this);
        dialogCidade.setTitle("Selecione um estado");

        //spinner

        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        final Spinner campoCidade = viewSpinner.findViewById(R.id.spinnerFiltro);


        String[] cidades = getResources().getStringArray(R.array.cidades);

        ArrayAdapter<String> adapterCidades = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cidades );

        adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCidade.setAdapter(adapterCidades);


        dialogCidade.setView(viewSpinner);



        dialogCidade.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialogCidade.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filtroCidade = campoCidade.getSelectedItem().toString();
                recuperarAnunciosPorCidade();
            }
        });

        AlertDialog dialog = dialogCidade.create();
        dialog.show();

    }
    public void recuperarAnunciosPorCidade(){

        //Configura nó por estado
        anunciosPublicosRef = configuracaoFireBase.getFirebase()
                .child("anuncios")
                .child(filtroEstado);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaAnuncios.clear();
                for (DataSnapshot categorias: dataSnapshot.getChildren() ){
                    for(DataSnapshot anuncios: categorias.getChildren() ){

                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add( anuncio );

                    }
                }

                Collections.reverse( listaAnuncios );
                adapterAnuncios.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



}