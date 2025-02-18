package com.botzap.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Interacao.
 */
@Table("interacao")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Interacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("data_hora")
    private Instant dataHora;

    @org.springframework.data.annotation.Transient
    private Usuario usuario;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "fluxo" }, allowSetters = true)
    private Mensagem mensagem;

    @Column("usuario_id")
    private Long usuarioId;

    @Column("mensagem_id")
    private Long mensagemId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Interacao id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDataHora() {
        return this.dataHora;
    }

    public Interacao dataHora(Instant dataHora) {
        this.setDataHora(dataHora);
        return this;
    }

    public void setDataHora(Instant dataHora) {
        this.dataHora = dataHora;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.usuarioId = usuario != null ? usuario.getId() : null;
    }

    public Interacao usuario(Usuario usuario) {
        this.setUsuario(usuario);
        return this;
    }

    public Mensagem getMensagem() {
        return this.mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
        this.mensagemId = mensagem != null ? mensagem.getId() : null;
    }

    public Interacao mensagem(Mensagem mensagem) {
        this.setMensagem(mensagem);
        return this;
    }

    public Long getUsuarioId() {
        return this.usuarioId;
    }

    public void setUsuarioId(Long usuario) {
        this.usuarioId = usuario;
    }

    public Long getMensagemId() {
        return this.mensagemId;
    }

    public void setMensagemId(Long mensagem) {
        this.mensagemId = mensagem;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Interacao)) {
            return false;
        }
        return getId() != null && getId().equals(((Interacao) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Interacao{" +
            "id=" + getId() +
            ", dataHora='" + getDataHora() + "'" +
            "}";
    }
}
