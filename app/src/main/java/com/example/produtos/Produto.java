package com.example.produtos;

import com.example.setores.Setor;

import java.io.Serializable;
import java.util.Objects;

public class Produto implements Serializable {
    private int id;
    private Double estoque;
    private String descricao;
    private Double preco;
    private Setor setor;
    public Produto() {
    }

    public Produto(int id, Double estoque, String descricao, Double preco, Setor setor) {
        this.id = id;
        this.estoque = estoque;
        this.descricao = descricao;
        this.preco = preco;
        this.setor = setor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getEstoque() {
        return estoque;
    }

    public void setEstoque(Double estoque) {
        this.estoque = estoque;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produto)) return false;
        Produto produto = (Produto) o;
        return id == produto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id)+" - "+estoque+" - "+descricao +" - "+ preco + setor;
    }
}