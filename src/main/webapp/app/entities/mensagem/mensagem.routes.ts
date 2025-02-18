import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MensagemResolve from './route/mensagem-routing-resolve.service';

const mensagemRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mensagem.component').then(m => m.MensagemComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mensagem-detail.component').then(m => m.MensagemDetailComponent),
    resolve: {
      mensagem: MensagemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mensagem-update.component').then(m => m.MensagemUpdateComponent),
    resolve: {
      mensagem: MensagemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mensagem-update.component').then(m => m.MensagemUpdateComponent),
    resolve: {
      mensagem: MensagemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default mensagemRoute;
