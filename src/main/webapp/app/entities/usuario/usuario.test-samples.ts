import { IUsuario, NewUsuario } from './usuario.model';

export const sampleWithRequiredData: IUsuario = {
  id: 2981,
  nome: 'through annual lest',
  telefone: 'against amidst',
};

export const sampleWithPartialData: IUsuario = {
  id: 27914,
  nome: 'quiet light because',
  telefone: 'whenever',
};

export const sampleWithFullData: IUsuario = {
  id: 5768,
  nome: 'disinherit meanwhile finally',
  telefone: 'mean fooey',
};

export const sampleWithNewData: NewUsuario = {
  nome: 'mmm',
  telefone: 'lest inside searchingly',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
