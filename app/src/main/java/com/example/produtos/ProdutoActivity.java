package com.example.produtos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.main.MainActivity;
import com.example.setores.R;
import com.example.setores.Setor;
import com.example.setores.SetorService;

import java.util.Arrays;
import java.util.LinkedList;

public class ProdutoActivity extends AppCompatActivity {

    private static final String STATE_DESCRICAO = "descricao";
    private static final String STATE_PRECO = "preco";
    private static final String STATE_ESTOQUE = "estoque";
    private static final String STATE_SETOR_POS = "setor_pos";

    private class ProdutoServiceObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ProdutoService.RESULTADO_LISTA_PRODUTOS)) {
                Produto[] prods = (Produto[]) intent.getSerializableExtra("produtos");
                produtos.clear();
                if (prods != null && prods.length > 0) {
                    produtos.addAll(Arrays.asList(prods));
                }
                produtoAdapter.notifyDataSetChanged();
            }
        }
    }

    private class SetorServiceObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SetorService.RESULTADO_LISTA_SETORES)) {
                Setor[] setoresArray = (Setor[]) intent.getSerializableExtra("setores");
                setores.clear();
                if (setoresArray != null && setoresArray.length > 0) {
                    setores.addAll(Arrays.asList(setoresArray));
                }
                setorAdapter.notifyDataSetChanged();
            }
        }
    }

    private LinkedList<Produto> produtos;
    private LinkedList<Setor> setores;
    private EditText edDescricao, edPreco, edEstoque;
    private Spinner spinnerSetor;
    private ListView lista;
    private ProdutoAdapter produtoAdapter;
    private ArrayAdapter<Setor> setorAdapter;
    private ProdutoServiceObserver produtoServiceObserver;
    private SetorServiceObserver setorServiceObserver;
    boolean editando = false;
    private Produto produtoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);

        produtos = new LinkedList<>();
        setores = new LinkedList<>();

        edDescricao = findViewById(R.id.txt_descr_produto);
        edPreco = findViewById(R.id.txt_preco_produto);
        edEstoque = findViewById(R.id.txt_estoque_produto);
        spinnerSetor = findViewById(R.id.spinner_setor_produto);
        lista = findViewById(R.id.lista_produtos);

        produtoAdapter = new ProdutoAdapter(this, produtos);
        lista.setAdapter(produtoAdapter);

        setorAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, setores);
        setorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSetor.setAdapter(setorAdapter);

        produtoServiceObserver = new ProdutoServiceObserver();
        registerReceiver(produtoServiceObserver,
                new IntentFilter(ProdutoService.RESULTADO_LISTA_PRODUTOS),
                Context.RECEIVER_EXPORTED);

        setorServiceObserver = new SetorServiceObserver();
        registerReceiver(setorServiceObserver,
                new IntentFilter(SetorService.RESULTADO_LISTA_SETORES),
                Context.RECEIVER_EXPORTED);

        if (savedInstanceState != null) {
            edDescricao.setText(savedInstanceState.getString(STATE_DESCRICAO));
            edPreco.setText(savedInstanceState.getString(STATE_PRECO));
            edEstoque.setText(savedInstanceState.getString(STATE_ESTOQUE));
            spinnerSetor.setSelection(savedInstanceState.getInt(STATE_SETOR_POS, 0));
        }

        buscarProdutos();
        buscarSetores();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(produtoServiceObserver);
        unregisterReceiver(setorServiceObserver);
    }

    private void buscarProdutos() {
        Intent it = new Intent(this, ProdutoService.class);
        it.setAction(ProdutoService.ACTION_LISTAR);
        startService(it);
    }

    private void buscarSetores() {
        Intent it = new Intent(this, SetorService.class);
        it.setAction(SetorService.ACTION_LISTAR);
        startService(it);
    }

    public void confirmar(View v) {
        String descricao = edDescricao.getText().toString();
        String preco = edPreco.getText().toString();
        String estoque = edEstoque.getText().toString();
        Setor setorSelecionado = (Setor) spinnerSetor.getSelectedItem();

        if (descricao.isEmpty() || preco.isEmpty() || estoque.isEmpty() || setorSelecionado == null) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        Produto p = new Produto();
        p.setDescricao(descricao);
        p.setPreco(Double.parseDouble(preco));
        p.setEstoque(Double.parseDouble(estoque));
        p.setSetor(setorSelecionado);

        Intent it = new Intent(this, ProdutoService.class);

        if (editando && produtoSelecionado != null) {
            p.setId(produtoSelecionado.getId());
            it.setAction(ProdutoService.ACTION_EDITAR);
            editando = false;
            produtoSelecionado = null;
            atualizarInputs();
        } else {
            it.setAction(ProdutoService.ACTION_CADASTRAR);
        }

        it.putExtra("produto", p);
        startService(it);

        buscarProdutos();
        limparCampos();
        produtoAdapter.setSelectedPosition(-1);
        produtoAdapter.notifyDataSetChanged();
    }

    public void editar(View v) {
        if (editando) return;
        int pos = produtoAdapter.getSelectedPosition();
        if (pos < 0) {
            Toast.makeText(this, "Selecione um produto para editar!", Toast.LENGTH_SHORT).show();
            return;
        }
        produtoSelecionado = produtos.get(pos);
        atualizarInputs();
        editando = true;
    }

    public void remover(View v) {
        int pos = produtoAdapter.getSelectedPosition();
        if (pos < 0) {
            Toast.makeText(this, "Selecione um produto para remover!", Toast.LENGTH_SHORT).show();
            return;
        }

        Produto produtoARemover = produtos.get(pos);

        Intent it = new Intent(this, ProdutoService.class);
        it.setAction("com.example.produtos.action.REMOVER");
        it.putExtra("produto", produtoARemover);
        startService(it);

        produtos.remove(pos);
        produtoAdapter.notifyDataSetChanged();
    }

    public void voltar(View v) {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

    private void limparCampos() {
        edDescricao.setText("");
        edPreco.setText("");
        edEstoque.setText("");
        spinnerSetor.setSelection(0);
    }

    private void atualizarInputs() {
        if (produtoSelecionado != null) {
            edDescricao.setText(produtoSelecionado.getDescricao());
            edPreco.setText(String.valueOf(produtoSelecionado.getPreco()));
            edEstoque.setText(String.valueOf(produtoSelecionado.getEstoque()));

            for (int i = 0; i < setores.size(); i++) {
                if (setores.get(i).getId() == produtoSelecionado.getSetor().getId()) {
                    spinnerSetor.setSelection(i);
                    break;
                }
            }
            return;
        }
        limparCampos();
    }
}