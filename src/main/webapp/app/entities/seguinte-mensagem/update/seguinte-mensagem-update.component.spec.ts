import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { SeguinteMensagemService } from '../service/seguinte-mensagem.service';
import { ISeguinteMensagem } from '../seguinte-mensagem.model';
import { SeguinteMensagemFormService } from './seguinte-mensagem-form.service';

import { SeguinteMensagemUpdateComponent } from './seguinte-mensagem-update.component';

describe('SeguinteMensagem Management Update Component', () => {
  let comp: SeguinteMensagemUpdateComponent;
  let fixture: ComponentFixture<SeguinteMensagemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let seguinteMensagemFormService: SeguinteMensagemFormService;
  let seguinteMensagemService: SeguinteMensagemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SeguinteMensagemUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SeguinteMensagemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SeguinteMensagemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    seguinteMensagemFormService = TestBed.inject(SeguinteMensagemFormService);
    seguinteMensagemService = TestBed.inject(SeguinteMensagemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const seguinteMensagem: ISeguinteMensagem = { id: 456 };

      activatedRoute.data = of({ seguinteMensagem });
      comp.ngOnInit();

      expect(comp.seguinteMensagem).toEqual(seguinteMensagem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISeguinteMensagem>>();
      const seguinteMensagem = { id: 123 };
      jest.spyOn(seguinteMensagemFormService, 'getSeguinteMensagem').mockReturnValue(seguinteMensagem);
      jest.spyOn(seguinteMensagemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ seguinteMensagem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: seguinteMensagem }));
      saveSubject.complete();

      // THEN
      expect(seguinteMensagemFormService.getSeguinteMensagem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(seguinteMensagemService.update).toHaveBeenCalledWith(expect.objectContaining(seguinteMensagem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISeguinteMensagem>>();
      const seguinteMensagem = { id: 123 };
      jest.spyOn(seguinteMensagemFormService, 'getSeguinteMensagem').mockReturnValue({ id: null });
      jest.spyOn(seguinteMensagemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ seguinteMensagem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: seguinteMensagem }));
      saveSubject.complete();

      // THEN
      expect(seguinteMensagemFormService.getSeguinteMensagem).toHaveBeenCalled();
      expect(seguinteMensagemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISeguinteMensagem>>();
      const seguinteMensagem = { id: 123 };
      jest.spyOn(seguinteMensagemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ seguinteMensagem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(seguinteMensagemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
