import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProgramStageInstanceFilter } from '../program-stage-instance-filter.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../program-stage-instance-filter.test-samples';

import { ProgramStageInstanceFilterService, RestProgramStageInstanceFilter } from './program-stage-instance-filter.service';

const requireRestSample: RestProgramStageInstanceFilter = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('ProgramStageInstanceFilter Service', () => {
  let service: ProgramStageInstanceFilterService;
  let httpMock: HttpTestingController;
  let expectedResult: IProgramStageInstanceFilter | IProgramStageInstanceFilter[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProgramStageInstanceFilterService);
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

    it('should create a ProgramStageInstanceFilter', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const programStageInstanceFilter = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(programStageInstanceFilter).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProgramStageInstanceFilter', () => {
      const programStageInstanceFilter = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(programStageInstanceFilter).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProgramStageInstanceFilter', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProgramStageInstanceFilter', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProgramStageInstanceFilter', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProgramStageInstanceFilterToCollectionIfMissing', () => {
      it('should add a ProgramStageInstanceFilter to an empty array', () => {
        const programStageInstanceFilter: IProgramStageInstanceFilter = sampleWithRequiredData;
        expectedResult = service.addProgramStageInstanceFilterToCollectionIfMissing([], programStageInstanceFilter);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programStageInstanceFilter);
      });

      it('should not add a ProgramStageInstanceFilter to an array that contains it', () => {
        const programStageInstanceFilter: IProgramStageInstanceFilter = sampleWithRequiredData;
        const programStageInstanceFilterCollection: IProgramStageInstanceFilter[] = [
          {
            ...programStageInstanceFilter,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProgramStageInstanceFilterToCollectionIfMissing(
          programStageInstanceFilterCollection,
          programStageInstanceFilter
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProgramStageInstanceFilter to an array that doesn't contain it", () => {
        const programStageInstanceFilter: IProgramStageInstanceFilter = sampleWithRequiredData;
        const programStageInstanceFilterCollection: IProgramStageInstanceFilter[] = [sampleWithPartialData];
        expectedResult = service.addProgramStageInstanceFilterToCollectionIfMissing(
          programStageInstanceFilterCollection,
          programStageInstanceFilter
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programStageInstanceFilter);
      });

      it('should add only unique ProgramStageInstanceFilter to an array', () => {
        const programStageInstanceFilterArray: IProgramStageInstanceFilter[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const programStageInstanceFilterCollection: IProgramStageInstanceFilter[] = [sampleWithRequiredData];
        expectedResult = service.addProgramStageInstanceFilterToCollectionIfMissing(
          programStageInstanceFilterCollection,
          ...programStageInstanceFilterArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const programStageInstanceFilter: IProgramStageInstanceFilter = sampleWithRequiredData;
        const programStageInstanceFilter2: IProgramStageInstanceFilter = sampleWithPartialData;
        expectedResult = service.addProgramStageInstanceFilterToCollectionIfMissing(
          [],
          programStageInstanceFilter,
          programStageInstanceFilter2
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programStageInstanceFilter);
        expect(expectedResult).toContain(programStageInstanceFilter2);
      });

      it('should accept null and undefined values', () => {
        const programStageInstanceFilter: IProgramStageInstanceFilter = sampleWithRequiredData;
        expectedResult = service.addProgramStageInstanceFilterToCollectionIfMissing([], null, programStageInstanceFilter, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programStageInstanceFilter);
      });

      it('should return initial array if no ProgramStageInstanceFilter is added', () => {
        const programStageInstanceFilterCollection: IProgramStageInstanceFilter[] = [sampleWithRequiredData];
        expectedResult = service.addProgramStageInstanceFilterToCollectionIfMissing(programStageInstanceFilterCollection, undefined, null);
        expect(expectedResult).toEqual(programStageInstanceFilterCollection);
      });
    });

    describe('compareProgramStageInstanceFilter', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProgramStageInstanceFilter(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProgramStageInstanceFilter(entity1, entity2);
        const compareResult2 = service.compareProgramStageInstanceFilter(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProgramStageInstanceFilter(entity1, entity2);
        const compareResult2 = service.compareProgramStageInstanceFilter(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProgramStageInstanceFilter(entity1, entity2);
        const compareResult2 = service.compareProgramStageInstanceFilter(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
