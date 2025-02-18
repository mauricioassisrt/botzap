import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../seguinte-mensagem.test-samples';

import { SeguinteMensagemFormService } from './seguinte-mensagem-form.service';

describe('SeguinteMensagem Form Service', () => {
  let service: SeguinteMensagemFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SeguinteMensagemFormService);
  });

  describe('Service methods', () => {
    describe('createSeguinteMensagemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSeguinteMensagemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            descricao: expect.any(Object),
          }),
        );
      });

      it('passing ISeguinteMensagem should create a new form with FormGroup', () => {
        const formGroup = service.createSeguinteMensagemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            descricao: expect.any(Object),
          }),
        );
      });
    });

    describe('getSeguinteMensagem', () => {
      it('should return NewSeguinteMensagem for default SeguinteMensagem initial value', () => {
        const formGroup = service.createSeguinteMensagemFormGroup(sampleWithNewData);

        const seguinteMensagem = service.getSeguinteMensagem(formGroup) as any;

        expect(seguinteMensagem).toMatchObject(sampleWithNewData);
      });

      it('should return NewSeguinteMensagem for empty SeguinteMensagem initial value', () => {
        const formGroup = service.createSeguinteMensagemFormGroup();

        const seguinteMensagem = service.getSeguinteMensagem(formGroup) as any;

        expect(seguinteMensagem).toMatchObject({});
      });

      it('should return ISeguinteMensagem', () => {
        const formGroup = service.createSeguinteMensagemFormGroup(sampleWithRequiredData);

        const seguinteMensagem = service.getSeguinteMensagem(formGroup) as any;

        expect(seguinteMensagem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISeguinteMensagem should not enable id FormControl', () => {
        const formGroup = service.createSeguinteMensagemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSeguinteMensagem should disable id FormControl', () => {
        const formGroup = service.createSeguinteMensagemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
