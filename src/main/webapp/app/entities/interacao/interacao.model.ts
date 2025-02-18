import dayjs from 'dayjs/esm';
import { IUsuario } from 'app/entities/usuario/usuario.model';
import { IMensagem } from 'app/entities/mensagem/mensagem.model';

export interface IInteracao {
  id: number;
  dataHora?: dayjs.Dayjs | null;
  usuario?: IUsuario | null;
  mensagem?: IMensagem | null;
}

export type NewInteracao = Omit<IInteracao, 'id'> & { id: null };
