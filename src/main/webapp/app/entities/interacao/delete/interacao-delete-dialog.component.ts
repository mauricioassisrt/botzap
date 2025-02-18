import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IInteracao } from '../interacao.model';
import { InteracaoService } from '../service/interacao.service';

@Component({
  standalone: true,
  templateUrl: './interacao-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class InteracaoDeleteDialogComponent {
  interacao?: IInteracao;

  protected interacaoService = inject(InteracaoService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.interacaoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
