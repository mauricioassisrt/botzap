import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IInteracao, NewInteracao } from '../interacao.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IInteracao for edit and NewInteracaoFormGroupInput for create.
 */
type InteracaoFormGroupInput = IInteracao | PartialWithRequiredKeyOf<NewInteracao>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IInteracao | NewInteracao> = Omit<T, 'dataHora'> & {
  dataHora?: string | null;
};

type InteracaoFormRawValue = FormValueOf<IInteracao>;

type NewInteracaoFormRawValue = FormValueOf<NewInteracao>;

type InteracaoFormDefaults = Pick<NewInteracao, 'id' | 'dataHora'>;

type InteracaoFormGroupContent = {
  id: FormControl<InteracaoFormRawValue['id'] | NewInteracao['id']>;
  dataHora: FormControl<InteracaoFormRawValue['dataHora']>;
  usuario: FormControl<InteracaoFormRawValue['usuario']>;
  mensagem: FormControl<InteracaoFormRawValue['mensagem']>;
};

export type InteracaoFormGroup = FormGroup<InteracaoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class InteracaoFormService {
  createInteracaoFormGroup(interacao: InteracaoFormGroupInput = { id: null }): InteracaoFormGroup {
    const interacaoRawValue = this.convertInteracaoToInteracaoRawValue({
      ...this.getFormDefaults(),
      ...interacao,
    });
    return new FormGroup<InteracaoFormGroupContent>({
      id: new FormControl(
        { value: interacaoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      dataHora: new FormControl(interacaoRawValue.dataHora, {
        validators: [Validators.required],
      }),
      usuario: new FormControl(interacaoRawValue.usuario),
      mensagem: new FormControl(interacaoRawValue.mensagem),
    });
  }

  getInteracao(form: InteracaoFormGroup): IInteracao | NewInteracao {
    return this.convertInteracaoRawValueToInteracao(form.getRawValue() as InteracaoFormRawValue | NewInteracaoFormRawValue);
  }

  resetForm(form: InteracaoFormGroup, interacao: InteracaoFormGroupInput): void {
    const interacaoRawValue = this.convertInteracaoToInteracaoRawValue({ ...this.getFormDefaults(), ...interacao });
    form.reset(
      {
        ...interacaoRawValue,
        id: { value: interacaoRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): InteracaoFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dataHora: currentTime,
    };
  }

  private convertInteracaoRawValueToInteracao(rawInteracao: InteracaoFormRawValue | NewInteracaoFormRawValue): IInteracao | NewInteracao {
    return {
      ...rawInteracao,
      dataHora: dayjs(rawInteracao.dataHora, DATE_TIME_FORMAT),
    };
  }

  private convertInteracaoToInteracaoRawValue(
    interacao: IInteracao | (Partial<NewInteracao> & InteracaoFormDefaults),
  ): InteracaoFormRawValue | PartialWithRequiredKeyOf<NewInteracaoFormRawValue> {
    return {
      ...interacao,
      dataHora: interacao.dataHora ? interacao.dataHora.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
