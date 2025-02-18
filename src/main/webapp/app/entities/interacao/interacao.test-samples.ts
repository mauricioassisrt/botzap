import dayjs from 'dayjs/esm';

import { IInteracao, NewInteracao } from './interacao.model';

export const sampleWithRequiredData: IInteracao = {
  id: 13087,
  dataHora: dayjs('2025-02-17T12:42'),
};

export const sampleWithPartialData: IInteracao = {
  id: 32428,
  dataHora: dayjs('2025-02-17T03:12'),
};

export const sampleWithFullData: IInteracao = {
  id: 16032,
  dataHora: dayjs('2025-02-17T04:25'),
};

export const sampleWithNewData: NewInteracao = {
  dataHora: dayjs('2025-02-17T10:08'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
