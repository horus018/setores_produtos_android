package com.example.produtos;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProdutoService extends IntentService {

    public static final String ACTION_LISTAR    = "com.example.produtos.action.LISTAR";
    public static final String ACTION_CADASTRAR = "com.example.produtos.action.CADASTRAR";
    public static final String ACTION_REMOVER   = "com.example.produtos.action.REMOVER";
    public static final String ACTION_EDITAR    = "com.example.produtos.action.EDITAR";
    public static final String RESULTADO_LISTA_PRODUTOS = "com.example.produtos.RESULTADO_LISTA_PRODUTOS";
    static final String URL_WS = "http://argo.td.utfpr.edu.br/clients/ws/produto";

    private final Gson gson;

    public ProdutoService() {
        super("ProdutoService");
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
                listar();
                break;
            case ACTION_REMOVER:
                remover(intent);
                break;
            case ACTION_EDITAR:
                editar(intent);
                break;
        }
    }

    private void cadastrar(Intent intent) {
        HttpURLConnection con = null;
        PrintWriter writer = null;
        try {
            Produto produto = (Produto) intent.getSerializableExtra("produto");
            String strProduto = gson.toJson(produto);

            URL url = new URL(URL_WS);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.connect();

            writer = new PrintWriter(con.getOutputStream());
            writer.write(strProduto);
            writer.flush();

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("POST", "Produto cadastrado com sucesso.");
            } else {
                Log.d("POST", "Falha ao cadastrar produto. Código de resposta: " + responseCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private void listar() {
        HttpURLConnection con = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(URL_WS);
            con = (HttpURLConnection) url.openConnection();
            con.connect();

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String linha;
                while ((linha = reader.readLine()) != null) {
                    response.append(linha);
                }
                Produto[] produtos = gson.fromJson(response.toString(), Produto[].class);
                Intent it = new Intent(RESULTADO_LISTA_PRODUTOS);
                it.putExtra("produtos", produtos);
                sendBroadcast(it);
            } else {
                Log.d("GET", "Falha ao listar produtos. Código de resposta: " + con.getResponseCode());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private void remover(Intent intent) {
        HttpURLConnection con = null;
        try {
            Produto produto = (Produto) intent.getSerializableExtra("produto");
            String urlString = URL_WS + "/" + produto.getId();
            URL url = new URL(urlString);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");
            con.connect();

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("DELETE", "Produto removido com sucesso.");
            } else {
                Log.d("DELETE", "Falha ao remover produto. Código de resposta: " + responseCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private void editar(Intent intent) {
        HttpURLConnection con = null;
        PrintWriter writer = null;
        try {
            Produto produto = (Produto) intent.getSerializableExtra("produto");
            String strProduto = gson.toJson(produto);

            String urlString = URL_WS + "/" + produto.getId();
            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.connect();

            writer = new PrintWriter(con.getOutputStream());
            writer.write(strProduto);
            writer.flush();

            if (con.getResponseCode() == 200) {
                Log.d("PUT", "OK");
            } else {
                Log.d("PUT", "Failed: " + con.getResponseMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (con != null) {
                con.disconnect();
            }
        }
    }
}
