package com.example.mop_marketorganicproducts_WesleyZanon.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mop_marketorganicproducts_WesleyZanon.R;
import com.example.mop_marketorganicproducts_WesleyZanon.helper.Permissoes;
import com.example.mop_marketorganicproducts_WesleyZanon.helper.configuracaoFireBase;
import com.example.mop_marketorganicproducts_WesleyZanon.model.Anuncio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class CadastrarAnunciosActivity extends AppCompatActivity //chamar a interface de interacao de imagem
        implements View.OnClickListener{

    //objetos globais para as interações de tela
    private EditText campoTitulo, campoDescricao;
    private EditText campoValor;
    private ImageView imagem1, imagem2, imagem3;
    private Spinner campoEstado, campoCidade, campoCategoria;
    //private Anuncio anuncio;
    private StorageReference storage;


    private Anuncio anuncio;
    //Anuncio anuncio = new Anuncio();

    //permissoes de outars classse
    private String[] permissoes = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    //array lista para colocar as imagens e recuperar depois
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncios);

        storage = configuracaoFireBase.getFirebaseStorage();

        //valisar permissoes e configuracoes iniciais
        Permissoes.validarPermissoes(permissoes, this, 1);


        inicializarComponentes();
        carregarListas();

    }


    //salvar anuncio apos a validacao
    public void salvarAnuncio() {

        //variavel para quantidade de fotos


        //variavel de indice para o for
        int i = 0;

        //salvar as imagens no storage

        //percorrer cada imagem
        for (i = 0; i < listaFotosRecuperadas.size(); i++) {

            //acessar as imagens pelo indice
            String urlImagem = listaFotosRecuperadas.get(i);

            int tamanho = listaFotosRecuperadas.size();

            //salvar fotos atraves do metodo
            salvarFotosStorage(urlImagem, tamanho, i);
        }
    }



    private void salvarFotosStorage(String urlimagem, final int totalFotos, int contador){

        //criar referencias

        StorageReference imagemAnuncio = storage.child("imagens").child("anuncios").child(anuncio.getIdAnuncio()).child("imagem"+contador);

        String nomeArq = UUID.randomUUID().toString();

        final StorageReference imagemRef = imagemAnuncio.child(nomeArq + ".jpeg");

        //fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlimagem));
        //retorna o objeto que irá controlar o upload

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //taskSnapshot.getDownloadUrl();
                //imagemRef.getDownloadUrl();


                Uri firebaseUrl = taskSnapshot.getUploadSessionUri();
                String urlConvertida = firebaseUrl.toString();

                        listaUrlFotos.add(urlConvertida);

                        anuncio.setFotos(listaUrlFotos);
                        anuncio.salvar();

                startActivity(new Intent(getApplicationContext(),AnunciosActivity.class));


                //Task<Uri> url = imagemRef.getDownloadUrl();
               //urlConvertida = url.toString();



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mensagemErro("Falha ao fazer Upload");
                Log.i("INFO", "Falha ao fazer Upload: " + e.getMessage());
            }
        });





    }

    public void teste()
    {
        mensagemErro("Entrou");
        Log.i("INFO","Falha ao fazer upload");
    }



        //Criar os dados para todos os anuncios
        public Anuncio configurarAnuncio(){

            //recuperar os dados da tela de cadastrar anuncio e converter como string
            String estado = campoEstado.getSelectedItem().toString();
            String cidade = campoCidade.getSelectedItem().toString();
            String categoria = campoCategoria.getSelectedItem().toString();
            String titulo = campoTitulo.getText().toString();
            String descricao = campoDescricao.getText().toString();
            String valor = campoValor.getText().toString();

            //declarar a classe anuncio
            Anuncio anuncio = new Anuncio();

            //passar os paremetros para a classe anuncio
            anuncio.setEstado( estado );
            anuncio.setCidade( cidade );
            anuncio.setCategoria( categoria );
            anuncio.setTitulo( titulo );
            anuncio.setValor( valor );
            anuncio.setDescricao( descricao );

            return anuncio;

        }




    //metodo para validar os dados do anuncio antes de salvar
    public void validaDadosAnuncio(View view){

        //chamar o metodo para configurar os anuncios
           anuncio = configurarAnuncio();

        //varificar os campos

        //varificar se o usuario carregou uma foto ou mais
        if(listaFotosRecuperadas.size() != 0){
            if(!anuncio.getEstado().isEmpty()){
                if(!anuncio.getCidade().isEmpty()) {
                    if (!anuncio.getCategoria().isEmpty()) {
                        if (!anuncio.getTitulo().isEmpty()) {
                            if (!anuncio.getValor().isEmpty()) {
                                if (!anuncio.getDescricao().isEmpty()) {
                                    salvarAnuncio();

                                }else{
                                    mensagemErro("Preencha descricao");
                                }

                            }else{
                                mensagemErro("Preencha valor");
                            }
                        }else{
                            mensagemErro("Preencha titulo");
                        }
                    }else{
                        mensagemErro("selecione uma categoria");
                    }
                }else{
                    mensagemErro("selecione uma cidade");
                }
            }else{
                mensagemErro("selecione um estado");
            }
        }
        else{
            mensagemErro("Selecione uma foto ou mais!");
        }

    }
    //metodo para exibir msg de erro de validacao de dados
    private void mensagemErro(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();

    }




        //colocar imagens por interoções de click
    @Override
    public void onClick(View view) {
        //listar elementos de imagens paar ser clicados

        switch (view.getId()){  //recuperar id do item clicado
            case R.id.imagemCadastro1:
                escolherImagem(1);
                break;

            case R.id.imagemCadastro2:
                escolherImagem(2);
                break;

            case R.id.imagemCadastro3:
                escolherImagem(3);
                break;

        }
    }

    //metodo para entrar a imagem escolhida
    public void escolherImagem(int RequestCold){
    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(i, RequestCold);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){

            //recuperar imagens
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //configurar imagem no imageView
            if(requestCode == 1) //verificar se a imagem escolhida
            {
                imagem1.setImageURI(imagemSelecionada);
            }else if(requestCode == 2) //verificar se a imagem escolhida
            {
                imagem2.setImageURI(imagemSelecionada);
            }else if(requestCode == 3) //verificar se a imagem escolhida
            {
                imagem3.setImageURI(imagemSelecionada);
            }

            //colocar as imagens na Array list
            listaFotosRecuperadas.add(caminhoImagem);

        }
    }

    private void inicializarComponentes(){

        //inicializar os componentes com os objetos globais das activity
        campoTitulo = findViewById(R.id.EditTituloProduto);
        campoValor = findViewById(R.id.EditValorProduto);
        campoDescricao = findViewById(R.id.EditDescricaoProduto);

        campoEstado = findViewById(R.id.editEstado);
        campoCidade = findViewById(R.id.editCidades);
        campoCategoria = findViewById(R.id.editCategoria);

        imagem1 = findViewById(R.id.imagemCadastro1);
        imagem2 = findViewById(R.id.imagemCadastro2);
        imagem3 = findViewById(R.id.imagemCadastro3);

        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);


        Locale locale = new Locale("pt", "BR");

    }

    private void carregarListas(){

        //metodo muito longo
       /* String estados[] = new String[]{
                "SP"
        };*/

        //metodo direto do xml

        //aary de spinners
        String[] estados = getResources().getStringArray(R.array.estados);

        ArrayAdapter<String> adapterEstado = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, estados );

    adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    campoEstado.setAdapter(adapterEstado);



        String[] cidades = getResources().getStringArray(R.array.cidades);

        ArrayAdapter<String> adapterCidades = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cidades );

        adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCidade.setAdapter(adapterCidades);
        //array de string para os spinners



        String[] categoria = getResources().getStringArray(R.array.categorias);

        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoria );

        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapterCategoria);


    }


    //pegar class de permissao
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){

            //caso o usuario negue a permicao
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }

    }

    private void alertaValidacaoPermissao(){

        //alerta para negacao de permisssoes de camera
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissoes negadas");
        builder.setMessage("Para acessar, aceite as permisssoes!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        //retornar novamente
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}