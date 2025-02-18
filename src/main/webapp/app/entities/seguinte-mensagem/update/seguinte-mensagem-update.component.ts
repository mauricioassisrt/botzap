import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISeguinteMensagem } from '../seguinte-mensagem.model';
import { SeguinteMensagemService } from '../service/seguinte-mensagem.service';
import { SeguinteMensagemFormGroup, SeguinteMensagemFormService } from './seguinte-mensagem-form.service';

@Component({
  standalone: true,
  selector: 'jhi-seguinte-mensagem-update',
  templateUrl: './seguinte-mensagem-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SeguinteMensagemUpdateComponent implements OnInit {
  isSaving = false;
  seguinteMensagem: ISeguinteMensagem | null = null;

  protected seguinteMensagemService = inject(SeguinteMensagemService);
  protected seguinteMensagemFormService = inject(SeguinteMensagemFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SeguinteMensagemFormGroup = this.seguinteMensagemFormService.createSeguinteMensagemFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ seguinteMensagem }) => {
      this.seguinteMensagem = seguinteMensagem;
      if (seguinteMensagem) {
        this.updateForm(seguinteMensagem);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const seguinteMensagem = this.seguinteMensagemFormService.getSeguinteMensagem(this.editForm);
    if (seguinteMensagem.id !== null) {
      this.subscribeToSaveResponse(this.seguinteMensagemService.update(seguinteMensagem));
    } else {
      this.subscribeToSaveResponse(this.seguinteMensagemService.create(seguinteMensagem));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISeguinteMensagem>>): void {
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

  protected updateForm(seguinteMensagem: ISeguinteMensagem): void {
    this.seguinteMensagem = seguinteMensagem;
    this.seguinteMensagemFormService.resetForm(this.editForm, seguinteMensagem);
  }
}
