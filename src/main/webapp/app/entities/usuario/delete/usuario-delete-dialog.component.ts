import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IUsuario } from '../usuario.model';
import { UsuarioService } from '../service/usuario.service';

@Component({
  standalone: true,
  templateUrl: './usuario-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class UsuarioDeleteDialogComponent {
  usuario?: IUsuario;

  protected usuarioService = inject(UsuarioService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.usuarioService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
