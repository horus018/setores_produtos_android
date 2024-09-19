package com.example.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.produtos.ProdutoActivity;
import com.example.setores.R;
import com.example.setores.SetorActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSetorActivity(View v) {
        Intent intent = new Intent(this, SetorActivity.class);
        startActivity(intent);
    }

    public void openProdutoActivity(View v) {
        Intent intent = new Intent(this, ProdutoActivity.class);
        startActivity(intent);
    }

    public void voltar(View view) {
    }
}
