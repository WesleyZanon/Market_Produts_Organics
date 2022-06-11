package com.example.mop_marketorganicproducts_WesleyZanon.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mop_marketorganicproducts_WesleyZanon.R;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mop_marketorganicproducts_WesleyZanon.R;
import com.example.mop_marketorganicproducts_WesleyZanon.helper.configuracaoFireBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;




public class CadastrarActivity extends AppCompatActivity {


    private Button botaoCadastrar;
    private EditText campoEmail, campoSenha;

    private FirebaseAuth autenticacao = configuracaoFireBase.getReferenciaAutenticacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        inicializarComponentes();


    }


    private void inicializarComponentes(){


        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha1);
        botaoCadastrar = findViewById(R.id.botaoCadastrar);
    }

    public void cadastrarPessoa(View view){

        String email = campoEmail.getText().toString();
        String senha = campoSenha.getText().toString();

        autenticacao.createUserWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ){


                    autenticacao.signInWithEmailAndPassword(
                            email, senha
                    ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {



                        }
                    });

                    Toast.makeText(CadastrarActivity.this,
                            "Cadastro realizado com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));

                }else {

                    String erroExcecao = "";

                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExcecao = "Por favor, digite um e-mail válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = "Este conta já foi cadastrada";
                    } catch (Exception e) {
                        erroExcecao = "ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastrarActivity.this,
                            "Erro: " + erroExcecao ,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });





    }


}