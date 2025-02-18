import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IInteracao } from '../interacao.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../interacao.test-samples';

import { InteracaoService, RestInteracao } from './interacao.service';

const requireRestSample: RestInteracao = {
  ...sampleWithRequiredData,
  dataHora: sampleWithRequiredData.dataHora?.toJSON(),
};

describe('Interacao Service', () => {
  let service: InteracaoService;
  let httpMock: HttpTestingController;
  let expectedResult: IInteracao | IInteracao[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(InteracaoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Interacao', () => {
      const interacao = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(interacao).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Interacao', () => {
      const interacao = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(interacao).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Interacao', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Interacao', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Interacao', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addInteracaoToCollectionIfMissing', () => {
      it('should add a Interacao to an empty array', () => {
        const interacao: IInteracao = sampleWithRequiredData;
        expectedResult = service.addInteracaoToCollectionIfMissing([], interacao);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(interacao);
      });

      it('should not add a Interacao to an array that contains it', () => {
        const interacao: IInteracao = sampleWithRequiredData;
        const interacaoCollection: IInteracao[] = [
          {
            ...interacao,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addInteracaoToCollectionIfMissing(interacaoCollection, interacao);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Interacao to an array that doesn't contain it", () => {
        const interacao: IInteracao = sampleWithRequiredData;
        const interacaoCollection: IInteracao[] = [sampleWithPartialData];
        expectedResult = service.addInteracaoToCollectionIfMissing(interacaoCollection, interacao);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(interacao);
      });

      it('should add only unique Interacao to an array', () => {
        const interacaoArray: IInteracao[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const interacaoCollection: IInteracao[] = [sampleWithRequiredData];
        expectedResult = service.addInteracaoToCollectionIfMissing(interacaoCollection, ...interacaoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const interacao: IInteracao = sampleWithRequiredData;
        const interacao2: IInteracao = sampleWithPartialData;
        expectedResult = service.addInteracaoToCollectionIfMissing([], interacao, interacao2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(interacao);
        expect(expectedResult).toContain(interacao2);
      });

      it('should accept null and undefined values', () => {
        const interacao: IInteracao = sampleWithRequiredData;
        expectedResult = service.addInteracaoToCollectionIfMissing([], null, interacao, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(interacao);
      });

      it('should return initial array if no Interacao is added', () => {
        const interacaoCollection: IInteracao[] = [sampleWithRequiredData];
        expectedResult = service.addInteracaoToCollectionIfMissing(interacaoCollection, undefined, null);
        expect(expectedResult).toEqual(interacaoCollection);
      });
    });

    describe('compareInteracao', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareInteracao(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareInteracao(entity1, entity2);
        const compareResult2 = service.compareInteracao(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareInteracao(entity1, entity2);
        const compareResult2 = service.compareInteracao(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareInteracao(entity1, entity2);
        const compareResult2 = service.compareInteracao(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
