import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISeguinteMensagem } from 'app/entities/seguinte-mensagem/seguinte-mensagem.model';
import { SeguinteMensagemService } from 'app/entities/seguinte-mensagem/service/seguinte-mensagem.service';
import { IMensagem } from '../mensagem.model';
import { MensagemService } from '../service/mensagem.service';
import { MensagemFormGroup, MensagemFormService } from './mensagem-form.service';

@Component({
  standalone: true,
  selector: 'jhi-mensagem-update',
  templateUrl: './mensagem-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MensagemUpdateComponent implements OnInit {
  isSaving = false;
  mensagem: IMensagem | null = null;

  seguinteMensagemsSharedCollection: ISeguinteMensagem[] = [];

  protected mensagemService = inject(MensagemService);
  protected mensagemFormService = inject(MensagemFormService);
  protected seguinteMensagemService = inject(SeguinteMensagemService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MensagemFormGroup = this.mensagemFormService.createMensagemFormGroup();

  compareSeguinteMensagem = (o1: ISeguinteMensagem | null, o2: ISeguinteMensagem | null): boolean =>
    this.seguinteMensagemService.compareSeguinteMensagem(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mensagem }) => {
      this.mensagem = mensagem;
      if (mensagem) {
        this.updateForm(mensagem);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const mensagem = this.mensagemFormService.getMensagem(this.editForm);
    if (mensagem.id !== null) {
      this.subscribeToSaveResponse(this.mensagemService.update(mensagem));
    } else {
      this.subscribeToSaveResponse(this.mensagemService.create(mensagem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMensagem>>): void {
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

  protected updateForm(mensagem: IMensagem): void {
    this.mensagem = mensagem;
    this.mensagemFormService.resetForm(this.editForm, mensagem);

    this.seguinteMensagemsSharedCollection = this.seguinteMensagemService.addSeguinteMensagemToCollectionIfMissing<ISeguinteMensagem>(
      this.seguinteMensagemsSharedCollection,
      mensagem.fluxo,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.seguinteMensagemService
      .query()
      .pipe(map((res: HttpResponse<ISeguinteMensagem[]>) => res.body ?? []))
      .pipe(
        map((seguinteMensagems: ISeguinteMensagem[]) =>
          this.seguinteMensagemService.addSeguinteMensagemToCollectionIfMissing<ISeguinteMensagem>(seguinteMensagems, this.mensagem?.fluxo),
        ),
      )
      .subscribe((seguinteMensagems: ISeguinteMensagem[]) => (this.seguinteMensagemsSharedCollection = seguinteMensagems));
  }
}
