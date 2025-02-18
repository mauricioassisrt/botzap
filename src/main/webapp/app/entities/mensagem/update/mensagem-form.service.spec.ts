import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mensagem.test-samples';

import { MensagemFormService } from './mensagem-form.service';

describe('Mensagem Form Service', () => {
  let service: MensagemFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MensagemFormService);
  });

  describe('Service methods', () => {
    describe('createMensagemFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMensagemFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            texto: expect.any(Object),
            opcao: expect.any(Object),
            fluxo: expect.any(Object),
          }),
        );
      });

      it('passing IMensagem should create a new form with FormGroup', () => {
        const formGroup = service.createMensagemFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            texto: expect.any(Object),
            opcao: expect.any(Object),
            fluxo: expect.any(Object),
          }),
        );
      });
    });

    describe('getMensagem', () => {
      it('should return NewMensagem for default Mensagem initial value', () => {
        const formGroup = service.createMensagemFormGroup(sampleWithNewData);

        const mensagem = service.getMensagem(formGroup) as any;

        expect(mensagem).toMatchObject(sampleWithNewData);
      });

      it('should return NewMensagem for empty Mensagem initial value', () => {
        const formGroup = service.createMensagemFormGroup();

        const mensagem = service.getMensagem(formGroup) as any;

        expect(mensagem).toMatchObject({});
      });

      it('should return IMensagem', () => {
        const formGroup = service.createMensagemFormGroup(sampleWithRequiredData);

        const mensagem = service.getMensagem(formGroup) as any;

        expect(mensagem).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMensagem should not enable id FormControl', () => {
        const formGroup = service.createMensagemFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMensagem should disable id FormControl', () => {
        const formGroup = service.createMensagemFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
