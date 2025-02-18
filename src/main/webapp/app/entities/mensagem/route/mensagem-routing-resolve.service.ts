import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMensagem } from '../mensagem.model';
import { MensagemService } from '../service/mensagem.service';

const mensagemResolve = (route: ActivatedRouteSnapshot): Observable<null | IMensagem> => {
  const id = route.params.id;
  if (id) {
    return inject(MensagemService)
      .find(id)
      .pipe(
        mergeMap((mensagem: HttpResponse<IMensagem>) => {
          if (mensagem.body) {
            return of(mensagem.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default mensagemResolve;
