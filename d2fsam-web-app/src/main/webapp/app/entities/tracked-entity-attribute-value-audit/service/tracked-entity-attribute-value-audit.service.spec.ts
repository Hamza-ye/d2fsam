import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITrackedEntityAttributeValueAudit } from '../tracked-entity-attribute-value-audit.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../tracked-entity-attribute-value-audit.test-samples';

import {
  TrackedEntityAttributeValueAuditService,
  RestTrackedEntityAttributeValueAudit,
} from './tracked-entity-attribute-value-audit.service';

const requireRestSample: RestTrackedEntityAttributeValueAudit = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('TrackedEntityAttributeValueAudit Service', () => {
  let service: TrackedEntityAttributeValueAuditService;
  let httpMock: HttpTestingController;
  let expectedResult: ITrackedEntityAttributeValueAudit | ITrackedEntityAttributeValueAudit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TrackedEntityAttributeValueAuditService);
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

    it('should create a TrackedEntityAttributeValueAudit', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const trackedEntityAttributeValueAudit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(trackedEntityAttributeValueAudit).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TrackedEntityAttributeValueAudit', () => {
      const trackedEntityAttributeValueAudit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(trackedEntityAttributeValueAudit).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TrackedEntityAttributeValueAudit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TrackedEntityAttributeValueAudit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TrackedEntityAttributeValueAudit', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTrackedEntityAttributeValueAuditToCollectionIfMissing', () => {
      it('should add a TrackedEntityAttributeValueAudit to an empty array', () => {
        const trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityAttributeValueAuditToCollectionIfMissing([], trackedEntityAttributeValueAudit);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityAttributeValueAudit);
      });

      it('should not add a TrackedEntityAttributeValueAudit to an array that contains it', () => {
        const trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit = sampleWithRequiredData;
        const trackedEntityAttributeValueAuditCollection: ITrackedEntityAttributeValueAudit[] = [
          {
            ...trackedEntityAttributeValueAudit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTrackedEntityAttributeValueAuditToCollectionIfMissing(
          trackedEntityAttributeValueAuditCollection,
          trackedEntityAttributeValueAudit
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TrackedEntityAttributeValueAudit to an array that doesn't contain it", () => {
        const trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit = sampleWithRequiredData;
        const trackedEntityAttributeValueAuditCollection: ITrackedEntityAttributeValueAudit[] = [sampleWithPartialData];
        expectedResult = service.addTrackedEntityAttributeValueAuditToCollectionIfMissing(
          trackedEntityAttributeValueAuditCollection,
          trackedEntityAttributeValueAudit
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityAttributeValueAudit);
      });

      it('should add only unique TrackedEntityAttributeValueAudit to an array', () => {
        const trackedEntityAttributeValueAuditArray: ITrackedEntityAttributeValueAudit[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const trackedEntityAttributeValueAuditCollection: ITrackedEntityAttributeValueAudit[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityAttributeValueAuditToCollectionIfMissing(
          trackedEntityAttributeValueAuditCollection,
          ...trackedEntityAttributeValueAuditArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit = sampleWithRequiredData;
        const trackedEntityAttributeValueAudit2: ITrackedEntityAttributeValueAudit = sampleWithPartialData;
        expectedResult = service.addTrackedEntityAttributeValueAuditToCollectionIfMissing(
          [],
          trackedEntityAttributeValueAudit,
          trackedEntityAttributeValueAudit2
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(trackedEntityAttributeValueAudit);
        expect(expectedResult).toContain(trackedEntityAttributeValueAudit2);
      });

      it('should accept null and undefined values', () => {
        const trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit = sampleWithRequiredData;
        expectedResult = service.addTrackedEntityAttributeValueAuditToCollectionIfMissing(
          [],
          null,
          trackedEntityAttributeValueAudit,
          undefined
        );
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(trackedEntityAttributeValueAudit);
      });

      it('should return initial array if no TrackedEntityAttributeValueAudit is added', () => {
        const trackedEntityAttributeValueAuditCollection: ITrackedEntityAttributeValueAudit[] = [sampleWithRequiredData];
        expectedResult = service.addTrackedEntityAttributeValueAuditToCollectionIfMissing(
          trackedEntityAttributeValueAuditCollection,
          undefined,
          null
        );
        expect(expectedResult).toEqual(trackedEntityAttributeValueAuditCollection);
      });
    });

    describe('compareTrackedEntityAttributeValueAudit', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTrackedEntityAttributeValueAudit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTrackedEntityAttributeValueAudit(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityAttributeValueAudit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTrackedEntityAttributeValueAudit(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityAttributeValueAudit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTrackedEntityAttributeValueAudit(entity1, entity2);
        const compareResult2 = service.compareTrackedEntityAttributeValueAudit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
