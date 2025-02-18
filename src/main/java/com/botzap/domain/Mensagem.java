package com.botzap.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Mensagem.
 */
@Table("mensagem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Mensagem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("texto")
    private String texto;

    @Column("opcao")
    private Integer opcao;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "destinos" }, allowSetters = true)
    private SeguinteMensagem fluxo;

    @Column("fluxo_id")
    private Long fluxoId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Mensagem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return this.texto;
    }

    public Mensagem texto(String texto) {
        this.setTexto(texto);
        return this;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Integer getOpcao() {
        return this.opcao;
    }

    public Mensagem opcao(Integer opcao) {
        this.setOpcao(opcao);
        return this;
    }

    public void setOpcao(Integer opcao) {
        this.opcao = opcao;
    }

    public SeguinteMensagem getFluxo() {
        return this.fluxo;
    }

    public void setFluxo(SeguinteMensagem seguinteMensagem) {
        this.fluxo = seguinteMensagem;
        this.fluxoId = seguinteMensagem != null ? seguinteMensagem.getId() : null;
    }

    public Mensagem fluxo(SeguinteMensagem seguinteMensagem) {
        this.setFluxo(seguinteMensagem);
        return this;
    }

    public Long getFluxoId() {
        return this.fluxoId;
    }

    public void setFluxoId(Long seguinteMensagem) {
        this.fluxoId = seguinteMensagem;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mensagem)) {
            return false;
        }
        return getId() != null && getId().equals(((Mensagem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Mensagem{" +
            "id=" + getId() +
            ", texto='" + getTexto() + "'" +
            ", opcao=" + getOpcao() +
            "}";
    }
}
