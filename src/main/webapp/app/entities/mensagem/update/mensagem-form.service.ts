import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMensagem, NewMensagem } from '../mensagem.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMensagem for edit and NewMensagemFormGroupInput for create.
 */
type MensagemFormGroupInput = IMensagem | PartialWithRequiredKeyOf<NewMensagem>;

type MensagemFormDefaults = Pick<NewMensagem, 'id'>;

type MensagemFormGroupContent = {
  id: FormControl<IMensagem['id'] | NewMensagem['id']>;
  texto: FormControl<IMensagem['texto']>;
  opcao: FormControl<IMensagem['opcao']>;
  fluxo: FormControl<IMensagem['fluxo']>;
};

export type MensagemFormGroup = FormGroup<MensagemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MensagemFormService {
  createMensagemFormGroup(mensagem: MensagemFormGroupInput = { id: null }): MensagemFormGroup {
    const mensagemRawValue = {
      ...this.getFormDefaults(),
      ...mensagem,
    };
    return new FormGroup<MensagemFormGroupContent>({
      id: new FormControl(
        { value: mensagemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      texto: new FormControl(mensagemRawValue.texto, {
        validators: [Validators.required],
      }),
      opcao: new FormControl(mensagemRawValue.opcao),
      fluxo: new FormControl(mensagemRawValue.fluxo),
    });
  }

  getMensagem(form: MensagemFormGroup): IMensagem | NewMensagem {
    return form.getRawValue() as IMensagem | NewMensagem;
  }

  resetForm(form: MensagemFormGroup, mensagem: MensagemFormGroupInput): void {
    const mensagemRawValue = { ...this.getFormDefaults(), ...mensagem };
    form.reset(
      {
        ...mensagemRawValue,
        id: { value: mensagemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MensagemFormDefaults {
    return {
      id: null,
    };
  }
}
