import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import InteracaoResolve from './route/interacao-routing-resolve.service';

const interacaoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/interacao.component').then(m => m.InteracaoComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/interacao-detail.component').then(m => m.InteracaoDetailComponent),
    resolve: {
      interacao: InteracaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/interacao-update.component').then(m => m.InteracaoUpdateComponent),
    resolve: {
      interacao: InteracaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/interacao-update.component').then(m => m.InteracaoUpdateComponent),
    resolve: {
      interacao: InteracaoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default interacaoRoute;
