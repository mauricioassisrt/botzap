import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'e0453ec4-9f77-484c-9c25-0a15dbc89d85',
};

export const sampleWithPartialData: IAuthority = {
  name: 'ccf7db2a-b5dd-4eeb-869e-ffa0aa579cd7',
};

export const sampleWithFullData: IAuthority = {
  name: '8c3c9ca0-1a15-4302-a40c-4318e5ab8e02',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
