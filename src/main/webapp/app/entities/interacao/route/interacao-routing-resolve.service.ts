import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IInteracao } from '../interacao.model';
import { InteracaoService } from '../service/interacao.service';

const interacaoResolve = (route: ActivatedRouteSnapshot): Observable<null | IInteracao> => {
  const id = route.params.id;
  if (id) {
    return inject(InteracaoService)
      .find(id)
      .pipe(
        mergeMap((interacao: HttpResponse<IInteracao>) => {
          if (interacao.body) {
            return of(interacao.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default interacaoResolve;
