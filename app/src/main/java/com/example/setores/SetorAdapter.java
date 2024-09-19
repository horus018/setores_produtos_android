package com.example.setores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

public class SetorAdapter extends BaseAdapter {
    private Context context;
    private List<Setor> setores;
    private int selectedPosition = -1;

    public SetorAdapter(Context context, List<Setor> setores) {
        this.context = context;
        this.setores = setores;
    }

    @Override
    public int getCount() {
        return setores.size();
    }

    @Override
    public Object getItem(int position) {
        return setores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_setor, parent, false);
        }

        RadioButton radioButton = convertView.findViewById(R.id.radio_setor);
        TextView textView = convertView.findViewById(R.id.text_setor);

        textView.setText(setores.get(position).getId() +" - "+ setores.get(position).getDescricao() +" - "+ setores.get(position).getMargem());

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