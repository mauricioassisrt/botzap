import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMensagem } from '../mensagem.model';
import { MensagemService } from '../service/mensagem.service';

@Component({
  standalone: true,
  templateUrl: './mensagem-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MensagemDeleteDialogComponent {
  mensagem?: IMensagem;

  protected mensagemService = inject(MensagemService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.mensagemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
