import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITrackedEntityDataValueAudit } from '../tracked-entity-data-value-audit.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../tracked-entity-data-value-audit.test-samples';

import { TrackedEntityDataValueAuditService, RestTrackedEntityDataValueAudit } from './tracked-entity-data-value-audit.service';

const requireRestSample: RestTrackedEntityDataValueAudit = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('TrackedEntityDataValueAudit Service', () => {
  let service: TrackedEntityDataValueAuditService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrackedEntityDataValueAudit | ITrackedEntityDataValueAudit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TrackedEntityDataValueAuditService);
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

    it('should create a TrackedEntityDataValueAudit', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const trackedEntityDataValueAudit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(trackedEntityDataValueAudit).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TrackedEntityDataValueAudit', () => {
      const trackedEntityDataValueAudit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(trackedEntityDataValueAudit).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TrackedEntityDataValueAudit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TrackedEntityDataValueAudit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TrackedEntityDataValueAudit', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTrackedEntityDataValueAuditToCollectionIfMissing', () => {
      it('should add a TrackedEntityDataValueAudit to an empty array', () => {
        const trackedEntityDataValueAudit: ITrackedEntityDataValueAudit = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityDataValueAuditToCollectionIfMissing([], trackedEntityDataValueAudit);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityDataValueAudit);
      });

      it('should not add a TrackedEntityDataValueAudit to an array that contains it', () => {
        const trackedEntityDataValueAudit: ITrackedEntityDataValueAudit = sampleWithRequiredData;
        const trackedEntityDataValueAuditCollection: ITrackedEntityDataValueAudit[] = [
          {
            ...trackedEntityDataValueAudit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTrackedEntityDataValueAuditToCollectionIfMissing(
          trackedEntityDataValueAuditCollection,
          trackedEntityDataValueAudit
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TrackedEntityDataValueAudit to an array that doesn't contain it", () => {
        const trackedEntityDataValueAudit: ITrackedEntityDataValueAudit = sampleWithRequiredData;
        const trackedEntityDataValueAuditCollection: ITrackedEntityDataValueAudit[] = [sampleWithPartialData];
        expectedResult = service.addTrackedEntityDataValueAuditToCollectionIfMissing(
          trackedEntityDataValueAuditCollection,
          trackedEntityDataValueAudit
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityDataValueAudit);
      });

      it('should add only unique TrackedEntityDataValueAudit to an array', () => {
        const trackedEntityDataValueAuditArray: ITrackedEntityDataValueAudit[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const trackedEntityDataValueAuditCollection: ITrackedEntityDataValueAudit[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityDataValueAuditToCollectionIfMissing(
          trackedEntityDataValueAuditCollection,
          ...trackedEntityDataValueAuditArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const trackedEntityDataValueAudit: ITrackedEntityDataValueAudit = sampleWithRequiredData;
        const trackedEntityDataValueAudit2: ITrackedEntityDataValueAudit = sampleWithPartialData;
        expectedResult = service.addTrackedEntityDataValueAuditToCollectionIfMissing(
          [],
          trackedEntityDataValueAudit,
          trackedEntityDataValueAudit2
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityDataValueAudit);
        expect(expectedResult).toContain(trackedEntityDataValueAudit2);
      });

      it('should accept null and undefined values', () => {
        const trackedEntityDataValueAudit: ITrackedEntityDataValueAudit = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityDataValueAuditToCollectionIfMissing([], null, trackedEntityDataValueAudit, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityDataValueAudit);
      });

      it('should return initial array if no TrackedEntityDataValueAudit is added', () => {
        const trackedEntityDataValueAuditCollection: ITrackedEntityDataValueAudit[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityDataValueAuditToCollectionIfMissing(
          trackedEntityDataValueAuditCollection,
          undefined,
          null
        );
        expect(expectedResult).toEqual(trackedEntityDataValueAuditCollection);
      });
    });

    describe('compareTrackedEntityDataValueAudit', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrackedEntityDataValueAudit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTrackedEntityDataValueAudit(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityDataValueAudit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTrackedEntityDataValueAudit(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityDataValueAudit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTrackedEntityDataValueAudit(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityDataValueAudit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
