package com.example.setores;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SetorService extends IntentService {

    public static final String ACTION_LISTAR = "com.example.setores.action.LISTAR";
    public static final String ACTION_CADASTRAR = "com.example.setores.action.CADASTRAR";
    public static final String ACTION_EDITAR = "com.example.setores.action.EDITAR";
    public static final String ACTION_REMOVER = "com.example.setores.action.REMOVER";
    public static final String RESULTADO_LISTA_SETORES = "com.example.setores.RESULTADO_LISTA_SETORES";
    static final String URL_WS = "http://argo.td.utfpr.edu.br/clients/ws/setor";

    Gson gson;

    public SetorService() {
        super("SetorService");
        gson = new GsonBuilder().create();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null)
            return;
        switch (intent.getAction()) {
            case ACTION_CADASTRAR:
                cadastrar(intent);
                break;
            case ACTION_LISTAR:
                listar(intent);
                break;
            case ACTION_EDITAR:
                editar(intent);
                break;
            case ACTION_REMOVER:
                remover(intent);
                break;
        }
    }

    private void cadastrar(Intent intent) {
        HttpURLConnection con = null;
        try {
            Setor set = (Setor) intent.getSerializableExtra("setor");
            String strSetor = gson.toJson(set);

            URL url = new URL(URL_WS);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"))) {
                writer.println(strSetor);
                writer.flush();
            }

            if (con.getResponseCode() == 200) {
                Log.d("POST", "OK");
            } else {
                Log.d("POST", "Failed: " + con.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private void listar(Intent intent) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(URL_WS);
            con = (HttpURLConnection) url.openConnection();
            con.connect();

            if (con.getResponseCode() == 200) {
                StringBuilder bld = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))) {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        bld.append(linha);
                    }
                }
                Setor[] setores = gson.fromJson(bld.toString(), Setor[].class);
                Intent it = new Intent(RESULTADO_LISTA_SETORES);
                it.putExtra("setores", setores);
                sendBroadcast(it);
            } else {
                Log.d("GET", "Failed: " + con.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private void editar(Intent intent) {
        HttpURLConnection con = null;
        try {
            Setor set = (Setor) intent.getSerializableExtra("setor");
            String strSetor = gson.toJson(set);

            URL url = new URL(URL_WS + "/" + set.getId());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"))) {
                writer.println(strSetor);
                writer.flush();
            }

            if (con.getResponseCode() == 200) {
                Log.d("PUT", "OK");
            } else {
                Log.d("PUT", "Failed: " + con.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private void remover(Intent intent) {
        HttpURLConnection con = null;
        try {
            Setor set = (Setor) intent.getSerializableExtra("setor");

            URL url = new URL(URL_WS + "/" + set.getId());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            if (con.getResponseCode() == 200) {
                Log.d("DELETE", "OK");
            } else if(con.getResponseCode() == 500){
                Toast.makeText(this, "Não é possível deletar este setor pois ele possui produtos associados", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

}
