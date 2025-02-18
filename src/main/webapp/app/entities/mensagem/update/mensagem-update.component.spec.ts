import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ISeguinteMensagem } from 'app/entities/seguinte-mensagem/seguinte-mensagem.model';
import { SeguinteMensagemService } from 'app/entities/seguinte-mensagem/service/seguinte-mensagem.service';
import { MensagemService } from '../service/mensagem.service';
import { IMensagem } from '../mensagem.model';
import { MensagemFormService } from './mensagem-form.service';

import { MensagemUpdateComponent } from './mensagem-update.component';

describe('Mensagem Management Update Component', () => {
  let comp: MensagemUpdateComponent;
  let fixture: ComponentFixture<MensagemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let mensagemFormService: MensagemFormService;
  let mensagemService: MensagemService;
  let seguinteMensagemService: SeguinteMensagemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MensagemUpdateComponent],
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
      .overrideTemplate(MensagemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MensagemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    mensagemFormService = TestBed.inject(MensagemFormService);
    mensagemService = TestBed.inject(MensagemService);
    seguinteMensagemService = TestBed.inject(SeguinteMensagemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call SeguinteMensagem query and add missing value', () => {
      const mensagem: IMensagem = { id: 456 };
      const fluxo: ISeguinteMensagem = { id: 24963 };
      mensagem.fluxo = fluxo;

      const seguinteMensagemCollection: ISeguinteMensagem[] = [{ id: 14279 }];
      jest.spyOn(seguinteMensagemService, 'query').mockReturnValue(of(new HttpResponse({ body: seguinteMensagemCollection })));
      const additionalSeguinteMensagems = [fluxo];
      const expectedCollection: ISeguinteMensagem[] = [...additionalSeguinteMensagems, ...seguinteMensagemCollection];
      jest.spyOn(seguinteMensagemService, 'addSeguinteMensagemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mensagem });
      comp.ngOnInit();

      expect(seguinteMensagemService.query).toHaveBeenCalled();
      expect(seguinteMensagemService.addSeguinteMensagemToCollectionIfMissing).toHaveBeenCalledWith(
        seguinteMensagemCollection,
        ...additionalSeguinteMensagems.map(expect.objectContaining),
      );
      expect(comp.seguinteMensagemsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const mensagem: IMensagem = { id: 456 };
      const fluxo: ISeguinteMensagem = { id: 28917 };
      mensagem.fluxo = fluxo;

      activatedRoute.data = of({ mensagem });
      comp.ngOnInit();

      expect(comp.seguinteMensagemsSharedCollection).toContain(fluxo);
      expect(comp.mensagem).toEqual(mensagem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMensagem>>();
      const mensagem = { id: 123 };
      jest.spyOn(mensagemFormService, 'getMensagem').mockReturnValue(mensagem);
      jest.spyOn(mensagemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mensagem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mensagem }));
      saveSubject.complete();

      // THEN
      expect(mensagemFormService.getMensagem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(mensagemService.update).toHaveBeenCalledWith(expect.objectContaining(mensagem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMensagem>>();
      const mensagem = { id: 123 };
      jest.spyOn(mensagemFormService, 'getMensagem').mockReturnValue({ id: null });
      jest.spyOn(mensagemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mensagem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mensagem }));
      saveSubject.complete();

      // THEN
      expect(mensagemFormService.getMensagem).toHaveBeenCalled();
      expect(mensagemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMensagem>>();
      const mensagem = { id: 123 };
      jest.spyOn(mensagemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mensagem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(mensagemService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSeguinteMensagem', () => {
      it('Should forward to seguinteMensagemService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(seguinteMensagemService, 'compareSeguinteMensagem');
        comp.compareSeguinteMensagem(entity, entity2);
        expect(seguinteMensagemService.compareSeguinteMensagem).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
