import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ISeguinteMensagem } from '../seguinte-mensagem.model';

@Component({
  standalone: true,
  selector: 'jhi-seguinte-mensagem-detail',
  templateUrl: './seguinte-mensagem-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class SeguinteMensagemDetailComponent {
  seguinteMensagem = input<ISeguinteMensagem | null>(null);

  previousState(): void {
    window.history.back();
  }
}
