package com.botzap.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A SeguinteMensagem.
 */
@Table("seguinte_mensagem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SeguinteMensagem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("descricao")
    private String descricao;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "fluxo" }, allowSetters = true)
    private Set<Mensagem> destinos = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SeguinteMensagem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public SeguinteMensagem descricao(String descricao) {
        this.setDescricao(descricao);
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Set<Mensagem> getDestinos() {
        return this.destinos;
    }

    public void setDestinos(Set<Mensagem> mensagems) {
        if (this.destinos != null) {
            this.destinos.forEach(i -> i.setFluxo(null));
        }
        if (mensagems != null) {
            mensagems.forEach(i -> i.setFluxo(this));
        }
        this.destinos = mensagems;
    }

    public SeguinteMensagem destinos(Set<Mensagem> mensagems) {
        this.setDestinos(mensagems);
        return this;
    }

    public SeguinteMensagem addDestino(Mensagem mensagem) {
        this.destinos.add(mensagem);
        mensagem.setFluxo(this);
        return this;
    }

    public SeguinteMensagem removeDestino(Mensagem mensagem) {
        this.destinos.remove(mensagem);
        mensagem.setFluxo(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SeguinteMensagem)) {
            return false;
        }
        return getId() != null && getId().equals(((SeguinteMensagem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SeguinteMensagem{" +
            "id=" + getId() +
            ", descricao='" + getDescricao() + "'" +
            "}";
    }
}
