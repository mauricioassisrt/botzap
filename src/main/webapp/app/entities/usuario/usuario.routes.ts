import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import UsuarioResolve from './route/usuario-routing-resolve.service';

const usuarioRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/usuario.component').then(m => m.UsuarioComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/usuario-detail.component').then(m => m.UsuarioDetailComponent),
    resolve: {
      usuario: UsuarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/usuario-update.component').then(m => m.UsuarioUpdateComponent),
    resolve: {
      usuario: UsuarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/usuario-update.component').then(m => m.UsuarioUpdateComponent),
    resolve: {
      usuario: UsuarioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default usuarioRoute;
