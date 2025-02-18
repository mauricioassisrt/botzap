import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../interacao.test-samples';

import { InteracaoFormService } from './interacao-form.service';

describe('Interacao Form Service', () => {
  let service: InteracaoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InteracaoFormService);
  });

  describe('Service methods', () => {
    describe('createInteracaoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createInteracaoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dataHora: expect.any(Object),
            usuario: expect.any(Object),
            mensagem: expect.any(Object),
          }),
        );
      });

      it('passing IInteracao should create a new form with FormGroup', () => {
        const formGroup = service.createInteracaoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dataHora: expect.any(Object),
            usuario: expect.any(Object),
            mensagem: expect.any(Object),
          }),
        );
      });
    });

    describe('getInteracao', () => {
      it('should return NewInteracao for default Interacao initial value', () => {
        const formGroup = service.createInteracaoFormGroup(sampleWithNewData);

        const interacao = service.getInteracao(formGroup) as any;

        expect(interacao).toMatchObject(sampleWithNewData);
      });

      it('should return NewInteracao for empty Interacao initial value', () => {
        const formGroup = service.createInteracaoFormGroup();

        const interacao = service.getInteracao(formGroup) as any;

        expect(interacao).toMatchObject({});
      });

      it('should return IInteracao', () => {
        const formGroup = service.createInteracaoFormGroup(sampleWithRequiredData);

        const interacao = service.getInteracao(formGroup) as any;

        expect(interacao).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IInteracao should not enable id FormControl', () => {
        const formGroup = service.createInteracaoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewInteracao should disable id FormControl', () => {
        const formGroup = service.createInteracaoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
