import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProgramOwnershipHistory } from '../program-ownership-history.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../program-ownership-history.test-samples';

import { ProgramOwnershipHistoryService, RestProgramOwnershipHistory } from './program-ownership-history.service';

const requireRestSample: RestProgramOwnershipHistory = {
  ...sampleWithRequiredData,
  startDate: sampleWithRequiredData.startDate?.toJSON(),
  endDate: sampleWithRequiredData.endDate?.toJSON(),
};

describe('ProgramOwnershipHistory Service', () => {
  let service: ProgramOwnershipHistoryService;
  let httpMock: HttpTestingController;
  let expectedResult: IProgramOwnershipHistory | IProgramOwnershipHistory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProgramOwnershipHistoryService);
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

    it('should create a ProgramOwnershipHistory', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const programOwnershipHistory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(programOwnershipHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProgramOwnershipHistory', () => {
      const programOwnershipHistory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(programOwnershipHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProgramOwnershipHistory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProgramOwnershipHistory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProgramOwnershipHistory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProgramOwnershipHistoryToCollectionIfMissing', () => {
      it('should add a ProgramOwnershipHistory to an empty array', () => {
        const programOwnershipHistory: IProgramOwnershipHistory = sampleWithRequiredData;
        expectedResult = service.addProgramOwnershipHistoryToCollectionIfMissing([], programOwnershipHistory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programOwnershipHistory);
      });

      it('should not add a ProgramOwnershipHistory to an array that contains it', () => {
        const programOwnershipHistory: IProgramOwnershipHistory = sampleWithRequiredData;
        const programOwnershipHistoryCollection: IProgramOwnershipHistory[] = [
          {
            ...programOwnershipHistory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProgramOwnershipHistoryToCollectionIfMissing(
          programOwnershipHistoryCollection,
          programOwnershipHistory
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProgramOwnershipHistory to an array that doesn't contain it", () => {
        const programOwnershipHistory: IProgramOwnershipHistory = sampleWithRequiredData;
        const programOwnershipHistoryCollection: IProgramOwnershipHistory[] = [sampleWithPartialData];
        expectedResult = service.addProgramOwnershipHistoryToCollectionIfMissing(
          programOwnershipHistoryCollection,
          programOwnershipHistory
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programOwnershipHistory);
      });

      it('should add only unique ProgramOwnershipHistory to an array', () => {
        const programOwnershipHistoryArray: IProgramOwnershipHistory[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const programOwnershipHistoryCollection: IProgramOwnershipHistory[] = [sampleWithRequiredData];
        expectedResult = service.addProgramOwnershipHistoryToCollectionIfMissing(
          programOwnershipHistoryCollection,
          ...programOwnershipHistoryArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const programOwnershipHistory: IProgramOwnershipHistory = sampleWithRequiredData;
        const programOwnershipHistory2: IProgramOwnershipHistory = sampleWithPartialData;
        expectedResult = service.addProgramOwnershipHistoryToCollectionIfMissing([], programOwnershipHistory, programOwnershipHistory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programOwnershipHistory);
        expect(expectedResult).toContain(programOwnershipHistory2);
      });

      it('should accept null and undefined values', () => {
        const programOwnershipHistory: IProgramOwnershipHistory = sampleWithRequiredData;
        expectedResult = service.addProgramOwnershipHistoryToCollectionIfMissing([], null, programOwnershipHistory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programOwnershipHistory);
      });

      it('should return initial array if no ProgramOwnershipHistory is added', () => {
        const programOwnershipHistoryCollection: IProgramOwnershipHistory[] = [sampleWithRequiredData];
        expectedResult = service.addProgramOwnershipHistoryToCollectionIfMissing(programOwnershipHistoryCollection, undefined, null);
        expect(expectedResult).toEqual(programOwnershipHistoryCollection);
      });
    });

    describe('compareProgramOwnershipHistory', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProgramOwnershipHistory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProgramOwnershipHistory(entity1, entity2);
        const compareResult2 = service.compareProgramOwnershipHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProgramOwnershipHistory(entity1, entity2);
        const compareResult2 = service.compareProgramOwnershipHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProgramOwnershipHistory(entity1, entity2);
        const compareResult2 = service.compareProgramOwnershipHistory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
