import { IMensagem, NewMensagem } from './mensagem.model';

export const sampleWithRequiredData: IMensagem = {
  id: 7830,
  texto: 'defendant',
};

export const sampleWithPartialData: IMensagem = {
  id: 17431,
  texto: 'meanwhile imagineer',
};

export const sampleWithFullData: IMensagem = {
  id: 15546,
  texto: 'cauliflower old-fashioned',
  opcao: 6985,
};

export const sampleWithNewData: NewMensagem = {
  texto: 'duh pointed',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
