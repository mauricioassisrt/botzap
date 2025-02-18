import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUsuario } from '../usuario.model';
import { UsuarioService } from '../service/usuario.service';

const usuarioResolve = (route: ActivatedRouteSnapshot): Observable<null | IUsuario> => {
  const id = route.params.id;
  if (id) {
    return inject(UsuarioService)
      .find(id)
      .pipe(
        mergeMap((usuario: HttpResponse<IUsuario>) => {
          if (usuario.body) {
            return of(usuario.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default usuarioResolve;
