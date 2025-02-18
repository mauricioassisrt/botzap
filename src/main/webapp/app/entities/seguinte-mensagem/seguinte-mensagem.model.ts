export interface ISeguinteMensagem {
  id: number;
  descricao?: string | null;
}

export type NewSeguinteMensagem = Omit<ISeguinteMensagem, 'id'> & { id: null };
