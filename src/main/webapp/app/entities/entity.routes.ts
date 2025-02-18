import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'usuario',
    data: { pageTitle: 'Usuarios' },
    loadChildren: () => import('./usuario/usuario.routes'),
  },
  {
    path: 'interacao',
    data: { pageTitle: 'Interacaos' },
    loadChildren: () => import('./interacao/interacao.routes'),
  },
  {
    path: 'mensagem',
    data: { pageTitle: 'Mensagems' },
    loadChildren: () => import('./mensagem/mensagem.routes'),
  },
  {
    path: 'seguinte-mensagem',
    data: { pageTitle: 'SeguinteMensagems' },
    loadChildren: () => import('./seguinte-mensagem/seguinte-mensagem.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
