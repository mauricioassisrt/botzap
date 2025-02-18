import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUsuario } from 'app/entities/usuario/usuario.model';
import { UsuarioService } from 'app/entities/usuario/service/usuario.service';
import { IMensagem } from 'app/entities/mensagem/mensagem.model';
import { MensagemService } from 'app/entities/mensagem/service/mensagem.service';
import { InteracaoService } from '../service/interacao.service';
import { IInteracao } from '../interacao.model';
import { InteracaoFormGroup, InteracaoFormService } from './interacao-form.service';

@Component({
  standalone: true,
  selector: 'jhi-interacao-update',
  templateUrl: './interacao-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class InteracaoUpdateComponent implements OnInit {
  isSaving = false;
  interacao: IInteracao | null = null;

  usuariosSharedCollection: IUsuario[] = [];
  mensagemsSharedCollection: IMensagem[] = [];

  protected interacaoService = inject(InteracaoService);
  protected interacaoFormService = inject(InteracaoFormService);
  protected usuarioService = inject(UsuarioService);
  protected mensagemService = inject(MensagemService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: InteracaoFormGroup = this.interacaoFormService.createInteracaoFormGroup();

  compareUsuario = (o1: IUsuario | null, o2: IUsuario | null): boolean => this.usuarioService.compareUsuario(o1, o2);

  compareMensagem = (o1: IMensagem | null, o2: IMensagem | null): boolean => this.mensagemService.compareMensagem(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ interacao }) => {
      this.interacao = interacao;
      if (interacao) {
        this.updateForm(interacao);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const interacao = this.interacaoFormService.getInteracao(this.editForm);
    if (interacao.id !== null) {
      this.subscribeToSaveResponse(this.interacaoService.update(interacao));
    } else {
      this.subscribeToSaveResponse(this.interacaoService.create(interacao));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInteracao>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(interacao: IInteracao): void {
    this.interacao = interacao;
    this.interacaoFormService.resetForm(this.editForm, interacao);

    this.usuariosSharedCollection = this.usuarioService.addUsuarioToCollectionIfMissing<IUsuario>(
      this.usuariosSharedCollection,
      interacao.usuario,
    );
    this.mensagemsSharedCollection = this.mensagemService.addMensagemToCollectionIfMissing<IMensagem>(
      this.mensagemsSharedCollection,
      interacao.mensagem,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.usuarioService
      .query()
      .pipe(map((res: HttpResponse<IUsuario[]>) => res.body ?? []))
      .pipe(map((usuarios: IUsuario[]) => this.usuarioService.addUsuarioToCollectionIfMissing<IUsuario>(usuarios, this.interacao?.usuario)))
      .subscribe((usuarios: IUsuario[]) => (this.usuariosSharedCollection = usuarios));

    this.mensagemService
      .query()
      .pipe(map((res: HttpResponse<IMensagem[]>) => res.body ?? []))
      .pipe(
        map((mensagems: IMensagem[]) =>
          this.mensagemService.addMensagemToCollectionIfMissing<IMensagem>(mensagems, this.interacao?.mensagem),
        ),
      )
      .subscribe((mensagems: IMensagem[]) => (this.mensagemsSharedCollection = mensagems));
  }
}
