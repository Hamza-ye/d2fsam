import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITrackedEntityInstanceFilter } from '../tracked-entity-instance-filter.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../tracked-entity-instance-filter.test-samples';

import { TrackedEntityInstanceFilterService, RestTrackedEntityInstanceFilter } from './tracked-entity-instance-filter.service';

const requireRestSample: RestTrackedEntityInstanceFilter = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('TrackedEntityInstanceFilter Service', () => {
  let service: TrackedEntityInstanceFilterService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrackedEntityInstanceFilter | ITrackedEntityInstanceFilter[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TrackedEntityInstanceFilterService);
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

    it('should create a TrackedEntityInstanceFilter', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const trackedEntityInstanceFilter = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(trackedEntityInstanceFilter).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TrackedEntityInstanceFilter', () => {
      const trackedEntityInstanceFilter = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(trackedEntityInstanceFilter).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TrackedEntityInstanceFilter', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TrackedEntityInstanceFilter', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TrackedEntityInstanceFilter', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTrackedEntityInstanceFilterToCollectionIfMissing', () => {
      it('should add a TrackedEntityInstanceFilter to an empty array', () => {
        const trackedEntityInstanceFilter: ITrackedEntityInstanceFilter = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityInstanceFilterToCollectionIfMissing([], trackedEntityInstanceFilter);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityInstanceFilter);
      });

      it('should not add a TrackedEntityInstanceFilter to an array that contains it', () => {
        const trackedEntityInstanceFilter: ITrackedEntityInstanceFilter = sampleWithRequiredData;
        const trackedEntityInstanceFilterCollection: ITrackedEntityInstanceFilter[] = [
          {
            ...trackedEntityInstanceFilter,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTrackedEntityInstanceFilterToCollectionIfMissing(
          trackedEntityInstanceFilterCollection,
          trackedEntityInstanceFilter
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TrackedEntityInstanceFilter to an array that doesn't contain it", () => {
        const trackedEntityInstanceFilter: ITrackedEntityInstanceFilter = sampleWithRequiredData;
        const trackedEntityInstanceFilterCollection: ITrackedEntityInstanceFilter[] = [sampleWithPartialData];
        expectedResult = service.addTrackedEntityInstanceFilterToCollectionIfMissing(
          trackedEntityInstanceFilterCollection,
          trackedEntityInstanceFilter
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityInstanceFilter);
      });

      it('should add only unique TrackedEntityInstanceFilter to an array', () => {
        const trackedEntityInstanceFilterArray: ITrackedEntityInstanceFilter[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const trackedEntityInstanceFilterCollection: ITrackedEntityInstanceFilter[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityInstanceFilterToCollectionIfMissing(
          trackedEntityInstanceFilterCollection,
          ...trackedEntityInstanceFilterArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const trackedEntityInstanceFilter: ITrackedEntityInstanceFilter = sampleWithRequiredData;
        const trackedEntityInstanceFilter2: ITrackedEntityInstanceFilter = sampleWithPartialData;
        expectedResult = service.addTrackedEntityInstanceFilterToCollectionIfMissing(
          [],
          trackedEntityInstanceFilter,
          trackedEntityInstanceFilter2
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityInstanceFilter);
        expect(expectedResult).toContain(trackedEntityInstanceFilter2);
      });

      it('should accept null and undefined values', () => {
        const trackedEntityInstanceFilter: ITrackedEntityInstanceFilter = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityInstanceFilterToCollectionIfMissing([], null, trackedEntityInstanceFilter, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityInstanceFilter);
      });

      it('should return initial array if no TrackedEntityInstanceFilter is added', () => {
        const trackedEntityInstanceFilterCollection: ITrackedEntityInstanceFilter[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityInstanceFilterToCollectionIfMissing(
          trackedEntityInstanceFilterCollection,
          undefined,
          null
        );
        expect(expectedResult).toEqual(trackedEntityInstanceFilterCollection);
      });
    });

    describe('compareTrackedEntityInstanceFilter', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrackedEntityInstanceFilter(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTrackedEntityInstanceFilter(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityInstanceFilter(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTrackedEntityInstanceFilter(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityInstanceFilter(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTrackedEntityInstanceFilter(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityInstanceFilter(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
