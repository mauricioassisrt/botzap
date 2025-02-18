import { ISeguinteMensagem, NewSeguinteMensagem } from './seguinte-mensagem.model';

export const sampleWithRequiredData: ISeguinteMensagem = {
  id: 218,
  descricao: 'alb',
};

export const sampleWithPartialData: ISeguinteMensagem = {
  id: 30492,
  descricao: 'egg',
};

export const sampleWithFullData: ISeguinteMensagem = {
  id: 12039,
  descricao: 'continually painfully',
};

export const sampleWithNewData: NewSeguinteMensagem = {
  descricao: 'generally',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
