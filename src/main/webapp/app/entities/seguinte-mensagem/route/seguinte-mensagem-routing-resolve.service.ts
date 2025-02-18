import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISeguinteMensagem } from '../seguinte-mensagem.model';
import { SeguinteMensagemService } from '../service/seguinte-mensagem.service';

const seguinteMensagemResolve = (route: ActivatedRouteSnapshot): Observable<null | ISeguinteMensagem> => {
  const id = route.params.id;
  if (id) {
    return inject(SeguinteMensagemService)
      .find(id)
      .pipe(
        mergeMap((seguinteMensagem: HttpResponse<ISeguinteMensagem>) => {
          if (seguinteMensagem.body) {
            return of(seguinteMensagem.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default seguinteMensagemResolve;
