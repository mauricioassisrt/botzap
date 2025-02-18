import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 24163,
  login: 'L@3',
};

export const sampleWithPartialData: IUser = {
  id: 8474,
  login: 'E-X',
};

export const sampleWithFullData: IUser = {
  id: 7474,
  login: '-owU',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
