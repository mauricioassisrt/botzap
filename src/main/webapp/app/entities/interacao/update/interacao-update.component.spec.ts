import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUsuario } from 'app/entities/usuario/usuario.model';
import { UsuarioService } from 'app/entities/usuario/service/usuario.service';
import { IMensagem } from 'app/entities/mensagem/mensagem.model';
import { MensagemService } from 'app/entities/mensagem/service/mensagem.service';
import { IInteracao } from '../interacao.model';
import { InteracaoService } from '../service/interacao.service';
import { InteracaoFormService } from './interacao-form.service';

import { InteracaoUpdateComponent } from './interacao-update.component';

describe('Interacao Management Update Component', () => {
  let comp: InteracaoUpdateComponent;
  let fixture: ComponentFixture<InteracaoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let interacaoFormService: InteracaoFormService;
  let interacaoService: InteracaoService;
  let usuarioService: UsuarioService;
  let mensagemService: MensagemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [InteracaoUpdateComponent],
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
      .overrideTemplate(InteracaoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InteracaoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    interacaoFormService = TestBed.inject(InteracaoFormService);
    interacaoService = TestBed.inject(InteracaoService);
    usuarioService = TestBed.inject(UsuarioService);
    mensagemService = TestBed.inject(MensagemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Usuario query and add missing value', () => {
      const interacao: IInteracao = { id: 456 };
      const usuario: IUsuario = { id: 27535 };
      interacao.usuario = usuario;

      const usuarioCollection: IUsuario[] = [{ id: 19662 }];
      jest.spyOn(usuarioService, 'query').mockReturnValue(of(new HttpResponse({ body: usuarioCollection })));
      const additionalUsuarios = [usuario];
      const expectedCollection: IUsuario[] = [...additionalUsuarios, ...usuarioCollection];
      jest.spyOn(usuarioService, 'addUsuarioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ interacao });
      comp.ngOnInit();

      expect(usuarioService.query).toHaveBeenCalled();
      expect(usuarioService.addUsuarioToCollectionIfMissing).toHaveBeenCalledWith(
        usuarioCollection,
        ...additionalUsuarios.map(expect.objectContaining),
      );
      expect(comp.usuariosSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Mensagem query and add missing value', () => {
      const interacao: IInteracao = { id: 456 };
      const mensagem: IMensagem = { id: 11096 };
      interacao.mensagem = mensagem;

      const mensagemCollection: IMensagem[] = [{ id: 14351 }];
      jest.spyOn(mensagemService, 'query').mockReturnValue(of(new HttpResponse({ body: mensagemCollection })));
      const additionalMensagems = [mensagem];
      const expectedCollection: IMensagem[] = [...additionalMensagems, ...mensagemCollection];
      jest.spyOn(mensagemService, 'addMensagemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ interacao });
      comp.ngOnInit();

      expect(mensagemService.query).toHaveBeenCalled();
      expect(mensagemService.addMensagemToCollectionIfMissing).toHaveBeenCalledWith(
        mensagemCollection,
        ...additionalMensagems.map(expect.objectContaining),
      );
      expect(comp.mensagemsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const interacao: IInteracao = { id: 456 };
      const usuario: IUsuario = { id: 837 };
      interacao.usuario = usuario;
      const mensagem: IMensagem = { id: 27376 };
      interacao.mensagem = mensagem;

      activatedRoute.data = of({ interacao });
      comp.ngOnInit();

      expect(comp.usuariosSharedCollection).toContain(usuario);
      expect(comp.mensagemsSharedCollection).toContain(mensagem);
      expect(comp.interacao).toEqual(interacao);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInteracao>>();
      const interacao = { id: 123 };
      jest.spyOn(interacaoFormService, 'getInteracao').mockReturnValue(interacao);
      jest.spyOn(interacaoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ interacao });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: interacao }));
      saveSubject.complete();

      // THEN
      expect(interacaoFormService.getInteracao).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(interacaoService.update).toHaveBeenCalledWith(expect.objectContaining(interacao));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInteracao>>();
      const interacao = { id: 123 };
      jest.spyOn(interacaoFormService, 'getInteracao').mockReturnValue({ id: null });
      jest.spyOn(interacaoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ interacao: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: interacao }));
      saveSubject.complete();

      // THEN
      expect(interacaoFormService.getInteracao).toHaveBeenCalled();
      expect(interacaoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInteracao>>();
      const interacao = { id: 123 };
      jest.spyOn(interacaoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ interacao });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(interacaoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUsuario', () => {
      it('Should forward to usuarioService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(usuarioService, 'compareUsuario');
        comp.compareUsuario(entity, entity2);
        expect(usuarioService.compareUsuario).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareMensagem', () => {
      it('Should forward to mensagemService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(mensagemService, 'compareMensagem');
        comp.compareMensagem(entity, entity2);
        expect(mensagemService.compareMensagem).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
