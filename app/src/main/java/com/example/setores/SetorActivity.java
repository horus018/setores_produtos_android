package com.example.setores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.main.MainActivity;

import java.util.Arrays;
import java.util.LinkedList;

public class SetorActivity extends AppCompatActivity {

    class SetorServiceObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SetorService.RESULTADO_LISTA_SETORES)) {
                Setor[] sets = (Setor[]) intent.getSerializableExtra("setores");
                setores.clear();
                if (sets != null && sets.length > 0) {
                    setores.addAll(Arrays.asList(sets));
                }
                setorAdapter.notifyDataSetChanged();
            }
        }
    }

    LinkedList<Setor> setores;
    EditText edDescricao, edMargem;
    ListView lista;
    SetorAdapter setorAdapter;
    Setor setorSelecionado;
    boolean editando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setor);

        setores = new LinkedList<>();
        edDescricao = findViewById(R.id.txt_descr_setor);
        edMargem = findViewById(R.id.txt_margem_setor);
        lista = findViewById(R.id.lista_setores);

        setorAdapter = new SetorAdapter(this, setores);
        lista.setAdapter(setorAdapter);

        registerReceiver(new SetorServiceObserver(),
                new IntentFilter(SetorService.RESULTADO_LISTA_SETORES), Context.RECEIVER_EXPORTED);

        buscarSetores();
    }

    protected void buscarSetores() {
        Intent it = new Intent(this, SetorService.class);
        it.setAction(SetorService.ACTION_LISTAR);
        startService(it);
    }

    public void confirmar(View v) {
        String descricao = edDescricao.getText().toString();
        String margem = edMargem.getText().toString();

        if (descricao.isEmpty() || margem.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Setor s = new Setor();
        s.setDescricao(descricao);
        s.setMargem(Double.parseDouble(margem));

        Intent it = new Intent(this, SetorService.class);

        if (editando && setorSelecionado != null) {
            s.setId(setorSelecionado.getId());
            it.setAction(SetorService.ACTION_EDITAR);
            editando = false;
            setorSelecionado = null;
            atualizarInputs();
        } else {
            it.setAction(SetorService.ACTION_CADASTRAR);
        }

        it.putExtra("setor", s);
        startService(it);

        buscarSetores();
        limparCampos();
        setorAdapter.setSelectedPosition(-1);
        setorAdapter.notifyDataSetChanged();
    }

    public void voltar(View v) {
        Intent it = new Intent(this, MainActivity.class);
        startActivity(it);
    }

    public void editar(View v) {
        if (editando) return;
        if (setorAdapter.getSelectedPosition() == -1) {
            Toast.makeText(this, "Selecione um setor para editar", Toast.LENGTH_SHORT).show();
            return;
        }
        setorSelecionado = setores.get(setorAdapter.getSelectedPosition());
        atualizarInputs();
        editando = true;
    }

    public void remover(View v) {
        if (setorAdapter.getSelectedPosition() == -1) {
            Toast.makeText(this, "Selecione um setor para editar", Toast.LENGTH_SHORT).show();
            return;
        }
        setorSelecionado = setores.get(setorAdapter.getSelectedPosition());
        Intent it = new Intent(this, SetorService.class);
        it.setAction(SetorService.ACTION_REMOVER);
        it.putExtra("setor", setorSelecionado);
        startService(it);
        buscarSetores();
        setorSelecionado = null;
    }

    public void atualizarInputs() {
        if (setorSelecionado != null) {
            edDescricao.setText(setorSelecionado.getDescricao());
            edMargem.setText(String.valueOf(setorSelecionado.getMargem()));
            return;
        }
        limparCampos();
    }

    public void limparCampos() {
        edDescricao.setText("");
        edMargem.setText("");
    }
}