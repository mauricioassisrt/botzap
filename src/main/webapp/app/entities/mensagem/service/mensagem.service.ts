import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMensagem, NewMensagem } from '../mensagem.model';

export type PartialUpdateMensagem = Partial<IMensagem> & Pick<IMensagem, 'id'>;

export type EntityResponseType = HttpResponse<IMensagem>;
export type EntityArrayResponseType = HttpResponse<IMensagem[]>;

@Injectable({ providedIn: 'root' })
export class MensagemService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/mensagems');

  create(mensagem: NewMensagem): Observable<EntityResponseType> {
    return this.http.post<IMensagem>(this.resourceUrl, mensagem, { observe: 'response' });
  }

  update(mensagem: IMensagem): Observable<EntityResponseType> {
    return this.http.put<IMensagem>(`${this.resourceUrl}/${this.getMensagemIdentifier(mensagem)}`, mensagem, { observe: 'response' });
  }

  partialUpdate(mensagem: PartialUpdateMensagem): Observable<EntityResponseType> {
    return this.http.patch<IMensagem>(`${this.resourceUrl}/${this.getMensagemIdentifier(mensagem)}`, mensagem, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMensagem>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMensagem[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMensagemIdentifier(mensagem: Pick<IMensagem, 'id'>): number {
    return mensagem.id;
  }

  compareMensagem(o1: Pick<IMensagem, 'id'> | null, o2: Pick<IMensagem, 'id'> | null): boolean {
    return o1 && o2 ? this.getMensagemIdentifier(o1) === this.getMensagemIdentifier(o2) : o1 === o2;
  }

  addMensagemToCollectionIfMissing<Type extends Pick<IMensagem, 'id'>>(
    mensagemCollection: Type[],
    ...mensagemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const mensagems: Type[] = mensagemsToCheck.filter(isPresent);
    if (mensagems.length > 0) {
      const mensagemCollectionIdentifiers = mensagemCollection.map(mensagemItem => this.getMensagemIdentifier(mensagemItem));
      const mensagemsToAdd = mensagems.filter(mensagemItem => {
        const mensagemIdentifier = this.getMensagemIdentifier(mensagemItem);
        if (mensagemCollectionIdentifiers.includes(mensagemIdentifier)) {
          return false;
        }
        mensagemCollectionIdentifiers.push(mensagemIdentifier);
        return true;
      });
      return [...mensagemsToAdd, ...mensagemCollection];
    }
    return mensagemCollection;
  }
}
