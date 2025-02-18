import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IMensagem } from '../mensagem.model';

@Component({
  standalone: true,
  selector: 'jhi-mensagem-detail',
  templateUrl: './mensagem-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class MensagemDetailComponent {
  mensagem = input<IMensagem | null>(null);

  previousState(): void {
    window.history.back();
  }
}
