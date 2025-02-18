import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IUsuario } from '../usuario.model';

@Component({
  standalone: true,
  selector: 'jhi-usuario-detail',
  templateUrl: './usuario-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class UsuarioDetailComponent {
  usuario = input<IUsuario | null>(null);

  previousState(): void {
    window.history.back();
  }
}
