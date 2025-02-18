import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { SeguinteMensagemDetailComponent } from './seguinte-mensagem-detail.component';

describe('SeguinteMensagem Management Detail Component', () => {
  let comp: SeguinteMensagemDetailComponent;
  let fixture: ComponentFixture<SeguinteMensagemDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SeguinteMensagemDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./seguinte-mensagem-detail.component').then(m => m.SeguinteMensagemDetailComponent),
              resolve: { seguinteMensagem: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SeguinteMensagemDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SeguinteMensagemDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load seguinteMensagem on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SeguinteMensagemDetailComponent);

      // THEN
      expect(instance.seguinteMensagem()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
