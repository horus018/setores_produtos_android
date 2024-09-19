package com.example.produtos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.setores.R;

import java.util.List;

public class ProdutoAdapter extends BaseAdapter {
    private Context context;
    private List<Produto> produtos;
    private int selectedPosition = -1;

    public ProdutoAdapter(Context context, List<Produto> produtos) {
        this.context = context;
        this.produtos = produtos;
    }

    @Override
    public int getCount() {
        return produtos.size();
    }

    @Override
    public Object getItem(int position) {
        return produtos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_produto, parent, false);
        }

        RadioButton radioButton = convertView.findViewById(R.id.radio_produto);
        TextView textView = convertView.findViewById(R.id.text_produto);

        Produto produto = produtos.get(position);
        textView.setText(produto.getId() + " - " + produto.getDescricao() + " - " +
                String.format("R$ %.2f", produto.getPreco()) + " - Estoque: " + produto.getEstoque()
        +" (Setor: "+ produto.getSetor()+")");

        radioButton.setChecked(position == selectedPosition);

        convertView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });

        return convertView;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
