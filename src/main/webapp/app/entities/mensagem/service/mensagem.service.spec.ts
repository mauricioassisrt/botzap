import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IMensagem } from '../mensagem.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../mensagem.test-samples';

import { MensagemService } from './mensagem.service';

const requireRestSample: IMensagem = {
  ...sampleWithRequiredData,
};

describe('Mensagem Service', () => {
  let service: MensagemService;
  let httpMock: HttpTestingController;
  let expectedResult: IMensagem | IMensagem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MensagemService);
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

    it('should create a Mensagem', () => {
      const mensagem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(mensagem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Mensagem', () => {
      const mensagem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(mensagem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Mensagem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Mensagem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Mensagem', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMensagemToCollectionIfMissing', () => {
      it('should add a Mensagem to an empty array', () => {
        const mensagem: IMensagem = sampleWithRequiredData;
        expectedResult = service.addMensagemToCollectionIfMissing([], mensagem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mensagem);
      });

      it('should not add a Mensagem to an array that contains it', () => {
        const mensagem: IMensagem = sampleWithRequiredData;
        const mensagemCollection: IMensagem[] = [
          {
            ...mensagem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMensagemToCollectionIfMissing(mensagemCollection, mensagem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Mensagem to an array that doesn't contain it", () => {
        const mensagem: IMensagem = sampleWithRequiredData;
        const mensagemCollection: IMensagem[] = [sampleWithPartialData];
        expectedResult = service.addMensagemToCollectionIfMissing(mensagemCollection, mensagem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mensagem);
      });

      it('should add only unique Mensagem to an array', () => {
        const mensagemArray: IMensagem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const mensagemCollection: IMensagem[] = [sampleWithRequiredData];
        expectedResult = service.addMensagemToCollectionIfMissing(mensagemCollection, ...mensagemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const mensagem: IMensagem = sampleWithRequiredData;
        const mensagem2: IMensagem = sampleWithPartialData;
        expectedResult = service.addMensagemToCollectionIfMissing([], mensagem, mensagem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mensagem);
        expect(expectedResult).toContain(mensagem2);
      });

      it('should accept null and undefined values', () => {
        const mensagem: IMensagem = sampleWithRequiredData;
        expectedResult = service.addMensagemToCollectionIfMissing([], null, mensagem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mensagem);
      });

      it('should return initial array if no Mensagem is added', () => {
        const mensagemCollection: IMensagem[] = [sampleWithRequiredData];
        expectedResult = service.addMensagemToCollectionIfMissing(mensagemCollection, undefined, null);
        expect(expectedResult).toEqual(mensagemCollection);
      });
    });

    describe('compareMensagem', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMensagem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMensagem(entity1, entity2);
        const compareResult2 = service.compareMensagem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMensagem(entity1, entity2);
        const compareResult2 = service.compareMensagem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMensagem(entity1, entity2);
        const compareResult2 = service.compareMensagem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
