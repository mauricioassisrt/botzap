import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISeguinteMensagem, NewSeguinteMensagem } from '../seguinte-mensagem.model';

export type PartialUpdateSeguinteMensagem = Partial<ISeguinteMensagem> & Pick<ISeguinteMensagem, 'id'>;

export type EntityResponseType = HttpResponse<ISeguinteMensagem>;
export type EntityArrayResponseType = HttpResponse<ISeguinteMensagem[]>;

@Injectable({ providedIn: 'root' })
export class SeguinteMensagemService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/seguinte-mensagems');

  create(seguinteMensagem: NewSeguinteMensagem): Observable<EntityResponseType> {
    return this.http.post<ISeguinteMensagem>(this.resourceUrl, seguinteMensagem, { observe: 'response' });
  }

  update(seguinteMensagem: ISeguinteMensagem): Observable<EntityResponseType> {
    return this.http.put<ISeguinteMensagem>(
      `${this.resourceUrl}/${this.getSeguinteMensagemIdentifier(seguinteMensagem)}`,
      seguinteMensagem,
      { observe: 'response' },
    );
  }

  partialUpdate(seguinteMensagem: PartialUpdateSeguinteMensagem): Observable<EntityResponseType> {
    return this.http.patch<ISeguinteMensagem>(
      `${this.resourceUrl}/${this.getSeguinteMensagemIdentifier(seguinteMensagem)}`,
      seguinteMensagem,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISeguinteMensagem>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISeguinteMensagem[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSeguinteMensagemIdentifier(seguinteMensagem: Pick<ISeguinteMensagem, 'id'>): number {
    return seguinteMensagem.id;
  }

  compareSeguinteMensagem(o1: Pick<ISeguinteMensagem, 'id'> | null, o2: Pick<ISeguinteMensagem, 'id'> | null): boolean {
    return o1 && o2 ? this.getSeguinteMensagemIdentifier(o1) === this.getSeguinteMensagemIdentifier(o2) : o1 === o2;
  }

  addSeguinteMensagemToCollectionIfMissing<Type extends Pick<ISeguinteMensagem, 'id'>>(
    seguinteMensagemCollection: Type[],
    ...seguinteMensagemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const seguinteMensagems: Type[] = seguinteMensagemsToCheck.filter(isPresent);
    if (seguinteMensagems.length > 0) {
      const seguinteMensagemCollectionIdentifiers = seguinteMensagemCollection.map(seguinteMensagemItem =>
        this.getSeguinteMensagemIdentifier(seguinteMensagemItem),
      );
      const seguinteMensagemsToAdd = seguinteMensagems.filter(seguinteMensagemItem => {
        const seguinteMensagemIdentifier = this.getSeguinteMensagemIdentifier(seguinteMensagemItem);
        if (seguinteMensagemCollectionIdentifiers.includes(seguinteMensagemIdentifier)) {
          return false;
        }
        seguinteMensagemCollectionIdentifiers.push(seguinteMensagemIdentifier);
        return true;
      });
      return [...seguinteMensagemsToAdd, ...seguinteMensagemCollection];
    }
    return seguinteMensagemCollection;
  }
}
