jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, fakeAsync, inject, tick } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { SeguinteMensagemService } from '../service/seguinte-mensagem.service';

import { SeguinteMensagemDeleteDialogComponent } from './seguinte-mensagem-delete-dialog.component';

describe('SeguinteMensagem Management Delete Component', () => {
  let comp: SeguinteMensagemDeleteDialogComponent;
  let fixture: ComponentFixture<SeguinteMensagemDeleteDialogComponent>;
  let service: SeguinteMensagemService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SeguinteMensagemDeleteDialogComponent],
      providers: [provideHttpClient(), NgbActiveModal],
    })
      .overrideTemplate(SeguinteMensagemDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SeguinteMensagemDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SeguinteMensagemService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      }),
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
