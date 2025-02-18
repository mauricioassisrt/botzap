import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISeguinteMensagem } from '../seguinte-mensagem.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../seguinte-mensagem.test-samples';

import { SeguinteMensagemService } from './seguinte-mensagem.service';

const requireRestSample: ISeguinteMensagem = {
  ...sampleWithRequiredData,
};

describe('SeguinteMensagem Service', () => {
  let service: SeguinteMensagemService;
  let httpMock: HttpTestingController;
  let expectedResult: ISeguinteMensagem | ISeguinteMensagem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SeguinteMensagemService);
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

    it('should create a SeguinteMensagem', () => {
      const seguinteMensagem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(seguinteMensagem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SeguinteMensagem', () => {
      const seguinteMensagem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(seguinteMensagem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SeguinteMensagem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SeguinteMensagem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SeguinteMensagem', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSeguinteMensagemToCollectionIfMissing', () => {
      it('should add a SeguinteMensagem to an empty array', () => {
        const seguinteMensagem: ISeguinteMensagem = sampleWithRequiredData;
        expectedResult = service.addSeguinteMensagemToCollectionIfMissing([], seguinteMensagem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(seguinteMensagem);
      });

      it('should not add a SeguinteMensagem to an array that contains it', () => {
        const seguinteMensagem: ISeguinteMensagem = sampleWithRequiredData;
        const seguinteMensagemCollection: ISeguinteMensagem[] = [
          {
            ...seguinteMensagem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSeguinteMensagemToCollectionIfMissing(seguinteMensagemCollection, seguinteMensagem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SeguinteMensagem to an array that doesn't contain it", () => {
        const seguinteMensagem: ISeguinteMensagem = sampleWithRequiredData;
        const seguinteMensagemCollection: ISeguinteMensagem[] = [sampleWithPartialData];
        expectedResult = service.addSeguinteMensagemToCollectionIfMissing(seguinteMensagemCollection, seguinteMensagem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(seguinteMensagem);
      });

      it('should add only unique SeguinteMensagem to an array', () => {
        const seguinteMensagemArray: ISeguinteMensagem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const seguinteMensagemCollection: ISeguinteMensagem[] = [sampleWithRequiredData];
        expectedResult = service.addSeguinteMensagemToCollectionIfMissing(seguinteMensagemCollection, ...seguinteMensagemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const seguinteMensagem: ISeguinteMensagem = sampleWithRequiredData;
        const seguinteMensagem2: ISeguinteMensagem = sampleWithPartialData;
        expectedResult = service.addSeguinteMensagemToCollectionIfMissing([], seguinteMensagem, seguinteMensagem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(seguinteMensagem);
        expect(expectedResult).toContain(seguinteMensagem2);
      });

      it('should accept null and undefined values', () => {
        const seguinteMensagem: ISeguinteMensagem = sampleWithRequiredData;
        expectedResult = service.addSeguinteMensagemToCollectionIfMissing([], null, seguinteMensagem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(seguinteMensagem);
      });

      it('should return initial array if no SeguinteMensagem is added', () => {
        const seguinteMensagemCollection: ISeguinteMensagem[] = [sampleWithRequiredData];
        expectedResult = service.addSeguinteMensagemToCollectionIfMissing(seguinteMensagemCollection, undefined, null);
        expect(expectedResult).toEqual(seguinteMensagemCollection);
      });
    });

    describe('compareSeguinteMensagem', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSeguinteMensagem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSeguinteMensagem(entity1, entity2);
        const compareResult2 = service.compareSeguinteMensagem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSeguinteMensagem(entity1, entity2);
        const compareResult2 = service.compareSeguinteMensagem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSeguinteMensagem(entity1, entity2);
        const compareResult2 = service.compareSeguinteMensagem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
