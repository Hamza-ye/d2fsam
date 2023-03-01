import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IMalariaCase } from '../malaria-case.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../malaria-case.test-samples';

import { MalariaCaseService, RestMalariaCase } from './malaria-case.service';

const requireRestSample: RestMalariaCase = {
  ...sampleWithRequiredData,
  entryStarted: sampleWithRequiredData.entryStarted?.toJSON(),
  lastSynced: sampleWithRequiredData.lastSynced?.toJSON(),
  dateOfExamination: sampleWithRequiredData.dateOfExamination?.format(DATE_FORMAT),
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
  createdAtClient: sampleWithRequiredData.createdAtClient?.toJSON(),
  updatedAtClient: sampleWithRequiredData.updatedAtClient?.toJSON(),
  deletedAt: sampleWithRequiredData.deletedAt?.toJSON(),
};

describe('MalariaCase Service', () => {
  let service: MalariaCaseService;
  let httpMock: HttpTestingController;
  let expectedResult: IMalariaCase | IMalariaCase[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MalariaCaseService);
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

    it('should create a MalariaCase', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const malariaCase = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(malariaCase).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MalariaCase', () => {
      const malariaCase = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(malariaCase).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MalariaCase', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MalariaCase', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MalariaCase', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMalariaCaseToCollectionIfMissing', () => {
      it('should add a MalariaCase to an empty array', () => {
        const malariaCase: IMalariaCase = sampleWithRequiredData;
        expectedResult = service.addMalariaCaseToCollectionIfMissing([], malariaCase);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(malariaCase);
      });

      it('should not add a MalariaCase to an array that contains it', () => {
        const malariaCase: IMalariaCase = sampleWithRequiredData;
        const malariaCaseCollection: IMalariaCase[] = [
          {
            ...malariaCase,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMalariaCaseToCollectionIfMissing(malariaCaseCollection, malariaCase);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MalariaCase to an array that doesn't contain it", () => {
        const malariaCase: IMalariaCase = sampleWithRequiredData;
        const malariaCaseCollection: IMalariaCase[] = [sampleWithPartialData];
        expectedResult = service.addMalariaCaseToCollectionIfMissing(malariaCaseCollection, malariaCase);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(malariaCase);
      });

      it('should add only unique MalariaCase to an array', () => {
        const malariaCaseArray: IMalariaCase[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const malariaCaseCollection: IMalariaCase[] = [sampleWithRequiredData];
        expectedResult = service.addMalariaCaseToCollectionIfMissing(malariaCaseCollection, ...malariaCaseArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const malariaCase: IMalariaCase = sampleWithRequiredData;
        const malariaCase2: IMalariaCase = sampleWithPartialData;
        expectedResult = service.addMalariaCaseToCollectionIfMissing([], malariaCase, malariaCase2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(malariaCase);
        expect(expectedResult).toContain(malariaCase2);
      });

      it('should accept null and undefined values', () => {
        const malariaCase: IMalariaCase = sampleWithRequiredData;
        expectedResult = service.addMalariaCaseToCollectionIfMissing([], null, malariaCase, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(malariaCase);
      });

      it('should return initial array if no MalariaCase is added', () => {
        const malariaCaseCollection: IMalariaCase[] = [sampleWithRequiredData];
        expectedResult = service.addMalariaCaseToCollectionIfMissing(malariaCaseCollection, undefined, null);
        expect(expectedResult).toEqual(malariaCaseCollection);
      });
    });

    describe('compareMalariaCase', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMalariaCase(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMalariaCase(entity1, entity2);
        const compareResult2 = service.compareMalariaCase(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMalariaCase(entity1, entity2);
        const compareResult2 = service.compareMalariaCase(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMalariaCase(entity1, entity2);
        const compareResult2 = service.compareMalariaCase(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
