import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITrackedEntityInstance } from '../tracked-entity-instance.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../tracked-entity-instance.test-samples';

import { TrackedEntityInstanceService, RestTrackedEntityInstance } from './tracked-entity-instance.service';

const requireRestSample: RestTrackedEntityInstance = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
  createdAtClient: sampleWithRequiredData.createdAtClient?.toJSON(),
  updatedAtClient: sampleWithRequiredData.updatedAtClient?.toJSON(),
  lastSynchronized: sampleWithRequiredData.lastSynchronized?.toJSON(),
};

describe('TrackedEntityInstance Service', () => {
  let service: TrackedEntityInstanceService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrackedEntityInstance | ITrackedEntityInstance[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TrackedEntityInstanceService);
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

    it('should create a TrackedEntityInstance', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const trackedEntityInstance = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(trackedEntityInstance).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TrackedEntityInstance', () => {
      const trackedEntityInstance = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(trackedEntityInstance).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TrackedEntityInstance', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TrackedEntityInstance', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TrackedEntityInstance', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTrackedEntityInstanceToCollectionIfMissing', () => {
      it('should add a TrackedEntityInstance to an empty array', () => {
        const trackedEntityInstance: ITrackedEntityInstance = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityInstanceToCollectionIfMissing([], trackedEntityInstance);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityInstance);
      });

      it('should not add a TrackedEntityInstance to an array that contains it', () => {
        const trackedEntityInstance: ITrackedEntityInstance = sampleWithRequiredData;
        const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [
          {
            ...trackedEntityInstance,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTrackedEntityInstanceToCollectionIfMissing(trackedEntityInstanceCollection, trackedEntityInstance);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TrackedEntityInstance to an array that doesn't contain it", () => {
        const trackedEntityInstance: ITrackedEntityInstance = sampleWithRequiredData;
        const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [sampleWithPartialData];
        expectedResult = service.addTrackedEntityInstanceToCollectionIfMissing(trackedEntityInstanceCollection, trackedEntityInstance);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityInstance);
      });

      it('should add only unique TrackedEntityInstance to an array', () => {
        const trackedEntityInstanceArray: ITrackedEntityInstance[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityInstanceToCollectionIfMissing(
          trackedEntityInstanceCollection,
          ...trackedEntityInstanceArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const trackedEntityInstance: ITrackedEntityInstance = sampleWithRequiredData;
        const trackedEntityInstance2: ITrackedEntityInstance = sampleWithPartialData;
        expectedResult = service.addTrackedEntityInstanceToCollectionIfMissing([], trackedEntityInstance, trackedEntityInstance2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityInstance);
        expect(expectedResult).toContain(trackedEntityInstance2);
      });

      it('should accept null and undefined values', () => {
        const trackedEntityInstance: ITrackedEntityInstance = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityInstanceToCollectionIfMissing([], null, trackedEntityInstance, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityInstance);
      });

      it('should return initial array if no TrackedEntityInstance is added', () => {
        const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityInstanceToCollectionIfMissing(trackedEntityInstanceCollection, undefined, null);
        expect(expectedResult).toEqual(trackedEntityInstanceCollection);
      });
    });

    describe('compareTrackedEntityInstance', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrackedEntityInstance(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTrackedEntityInstance(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityInstance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTrackedEntityInstance(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityInstance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTrackedEntityInstance(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityInstance(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
