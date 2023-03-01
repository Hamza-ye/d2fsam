import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITrackedEntityProgramOwner } from '../tracked-entity-program-owner.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../tracked-entity-program-owner.test-samples';

import { TrackedEntityProgramOwnerService, RestTrackedEntityProgramOwner } from './tracked-entity-program-owner.service';

const requireRestSample: RestTrackedEntityProgramOwner = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('TrackedEntityProgramOwner Service', () => {
  let service: TrackedEntityProgramOwnerService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrackedEntityProgramOwner | ITrackedEntityProgramOwner[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TrackedEntityProgramOwnerService);
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

    it('should create a TrackedEntityProgramOwner', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const trackedEntityProgramOwner = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(trackedEntityProgramOwner).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TrackedEntityProgramOwner', () => {
      const trackedEntityProgramOwner = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(trackedEntityProgramOwner).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TrackedEntityProgramOwner', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TrackedEntityProgramOwner', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TrackedEntityProgramOwner', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTrackedEntityProgramOwnerToCollectionIfMissing', () => {
      it('should add a TrackedEntityProgramOwner to an empty array', () => {
        const trackedEntityProgramOwner: ITrackedEntityProgramOwner = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityProgramOwnerToCollectionIfMissing([], trackedEntityProgramOwner);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityProgramOwner);
      });

      it('should not add a TrackedEntityProgramOwner to an array that contains it', () => {
        const trackedEntityProgramOwner: ITrackedEntityProgramOwner = sampleWithRequiredData;
        const trackedEntityProgramOwnerCollection: ITrackedEntityProgramOwner[] = [
          {
            ...trackedEntityProgramOwner,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTrackedEntityProgramOwnerToCollectionIfMissing(
          trackedEntityProgramOwnerCollection,
          trackedEntityProgramOwner
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TrackedEntityProgramOwner to an array that doesn't contain it", () => {
        const trackedEntityProgramOwner: ITrackedEntityProgramOwner = sampleWithRequiredData;
        const trackedEntityProgramOwnerCollection: ITrackedEntityProgramOwner[] = [sampleWithPartialData];
        expectedResult = service.addTrackedEntityProgramOwnerToCollectionIfMissing(
          trackedEntityProgramOwnerCollection,
          trackedEntityProgramOwner
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityProgramOwner);
      });

      it('should add only unique TrackedEntityProgramOwner to an array', () => {
        const trackedEntityProgramOwnerArray: ITrackedEntityProgramOwner[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const trackedEntityProgramOwnerCollection: ITrackedEntityProgramOwner[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityProgramOwnerToCollectionIfMissing(
          trackedEntityProgramOwnerCollection,
          ...trackedEntityProgramOwnerArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const trackedEntityProgramOwner: ITrackedEntityProgramOwner = sampleWithRequiredData;
        const trackedEntityProgramOwner2: ITrackedEntityProgramOwner = sampleWithPartialData;
        expectedResult = service.addTrackedEntityProgramOwnerToCollectionIfMissing(
          [],
          trackedEntityProgramOwner,
          trackedEntityProgramOwner2
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityProgramOwner);
        expect(expectedResult).toContain(trackedEntityProgramOwner2);
      });

      it('should accept null and undefined values', () => {
        const trackedEntityProgramOwner: ITrackedEntityProgramOwner = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityProgramOwnerToCollectionIfMissing([], null, trackedEntityProgramOwner, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityProgramOwner);
      });

      it('should return initial array if no TrackedEntityProgramOwner is added', () => {
        const trackedEntityProgramOwnerCollection: ITrackedEntityProgramOwner[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityProgramOwnerToCollectionIfMissing(trackedEntityProgramOwnerCollection, undefined, null);
        expect(expectedResult).toEqual(trackedEntityProgramOwnerCollection);
      });
    });

    describe('compareTrackedEntityProgramOwner', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrackedEntityProgramOwner(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTrackedEntityProgramOwner(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityProgramOwner(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTrackedEntityProgramOwner(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityProgramOwner(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTrackedEntityProgramOwner(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityProgramOwner(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
