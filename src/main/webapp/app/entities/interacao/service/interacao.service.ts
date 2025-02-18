import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IInteracao, NewInteracao } from '../interacao.model';

export type PartialUpdateInteracao = Partial<IInteracao> & Pick<IInteracao, 'id'>;

type RestOf<T extends IInteracao | NewInteracao> = Omit<T, 'dataHora'> & {
  dataHora?: string | null;
};

export type RestInteracao = RestOf<IInteracao>;

export type NewRestInteracao = RestOf<NewInteracao>;

export type PartialUpdateRestInteracao = RestOf<PartialUpdateInteracao>;

export type EntityResponseType = HttpResponse<IInteracao>;
export type EntityArrayResponseType = HttpResponse<IInteracao[]>;

@Injectable({ providedIn: 'root' })
export class InteracaoService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/interacaos');

  create(interacao: NewInteracao): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(interacao);
    return this.http
      .post<RestInteracao>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(interacao: IInteracao): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(interacao);
    return this.http
      .put<RestInteracao>(`${this.resourceUrl}/${this.getInteracaoIdentifier(interacao)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(interacao: PartialUpdateInteracao): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(interacao);
    return this.http
      .patch<RestInteracao>(`${this.resourceUrl}/${this.getInteracaoIdentifier(interacao)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestInteracao>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestInteracao[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getInteracaoIdentifier(interacao: Pick<IInteracao, 'id'>): number {
    return interacao.id;
  }

  compareInteracao(o1: Pick<IInteracao, 'id'> | null, o2: Pick<IInteracao, 'id'> | null): boolean {
    return o1 && o2 ? this.getInteracaoIdentifier(o1) === this.getInteracaoIdentifier(o2) : o1 === o2;
  }

  addInteracaoToCollectionIfMissing<Type extends Pick<IInteracao, 'id'>>(
    interacaoCollection: Type[],
    ...interacaosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const interacaos: Type[] = interacaosToCheck.filter(isPresent);
    if (interacaos.length > 0) {
      const interacaoCollectionIdentifiers = interacaoCollection.map(interacaoItem => this.getInteracaoIdentifier(interacaoItem));
      const interacaosToAdd = interacaos.filter(interacaoItem => {
        const interacaoIdentifier = this.getInteracaoIdentifier(interacaoItem);
        if (interacaoCollectionIdentifiers.includes(interacaoIdentifier)) {
          return false;
        }
        interacaoCollectionIdentifiers.push(interacaoIdentifier);
        return true;
      });
      return [...interacaosToAdd, ...interacaoCollection];
    }
    return interacaoCollection;
  }

  protected convertDateFromClient<T extends IInteracao | NewInteracao | PartialUpdateInteracao>(interacao: T): RestOf<T> {
    return {
      ...interacao,
      dataHora: interacao.dataHora?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restInteracao: RestInteracao): IInteracao {
    return {
      ...restInteracao,
      dataHora: restInteracao.dataHora ? dayjs(restInteracao.dataHora) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestInteracao>): HttpResponse<IInteracao> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestInteracao[]>): HttpResponse<IInteracao[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
