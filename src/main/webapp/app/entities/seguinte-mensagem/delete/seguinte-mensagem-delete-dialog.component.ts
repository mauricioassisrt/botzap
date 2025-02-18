import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISeguinteMensagem } from '../seguinte-mensagem.model';
import { SeguinteMensagemService } from '../service/seguinte-mensagem.service';

@Component({
  standalone: true,
  templateUrl: './seguinte-mensagem-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SeguinteMensagemDeleteDialogComponent {
  seguinteMensagem?: ISeguinteMensagem;

  protected seguinteMensagemService = inject(SeguinteMensagemService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.seguinteMensagemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
