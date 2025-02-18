import { ISeguinteMensagem } from 'app/entities/seguinte-mensagem/seguinte-mensagem.model';

export interface IMensagem {
  id: number;
  texto?: string | null;
  opcao?: number | null;
  fluxo?: ISeguinteMensagem | null;
}

export type NewMensagem = Omit<IMensagem, 'id'> & { id: null };
