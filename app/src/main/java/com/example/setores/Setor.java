package com.example.setores;

import java.io.Serializable;
import java.util.Objects;

public class Setor implements Serializable {
    private int id;
    private String descricao;
    private Double margem;

    public Setor() {
    }

    public Setor(int id, String descricao, Double margem) {
        this.id = id;
        this.descricao = descricao;
        this.margem = margem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getMargem() {
        return margem;
    }

    public void setMargem(Double margem) {
        this.margem = margem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Setor)) return false;
        Setor setor = (Setor) o;
        return id == setor.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id)+" - "+descricao+" - "+margem;
    }
}
