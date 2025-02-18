import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISeguinteMensagem, NewSeguinteMensagem } from '../seguinte-mensagem.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISeguinteMensagem for edit and NewSeguinteMensagemFormGroupInput for create.
 */
type SeguinteMensagemFormGroupInput = ISeguinteMensagem | PartialWithRequiredKeyOf<NewSeguinteMensagem>;

type SeguinteMensagemFormDefaults = Pick<NewSeguinteMensagem, 'id'>;

type SeguinteMensagemFormGroupContent = {
  id: FormControl<ISeguinteMensagem['id'] | NewSeguinteMensagem['id']>;
  descricao: FormControl<ISeguinteMensagem['descricao']>;
};

export type SeguinteMensagemFormGroup = FormGroup<SeguinteMensagemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SeguinteMensagemFormService {
  createSeguinteMensagemFormGroup(seguinteMensagem: SeguinteMensagemFormGroupInput = { id: null }): SeguinteMensagemFormGroup {
    const seguinteMensagemRawValue = {
      ...this.getFormDefaults(),
      ...seguinteMensagem,
    };
    return new FormGroup<SeguinteMensagemFormGroupContent>({
      id: new FormControl(
        { value: seguinteMensagemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      descricao: new FormControl(seguinteMensagemRawValue.descricao, {
        validators: [Validators.required],
      }),
    });
  }

  getSeguinteMensagem(form: SeguinteMensagemFormGroup): ISeguinteMensagem | NewSeguinteMensagem {
    return form.getRawValue() as ISeguinteMensagem | NewSeguinteMensagem;
  }

  resetForm(form: SeguinteMensagemFormGroup, seguinteMensagem: SeguinteMensagemFormGroupInput): void {
    const seguinteMensagemRawValue = { ...this.getFormDefaults(), ...seguinteMensagem };
    form.reset(
      {
        ...seguinteMensagemRawValue,
        id: { value: seguinteMensagemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SeguinteMensagemFormDefaults {
    return {
      id: null,
    };
  }
}
