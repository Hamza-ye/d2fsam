import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRelationshipConstraint } from '../relationship-constraint.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../relationship-constraint.test-samples';

import { RelationshipConstraintService } from './relationship-constraint.service';

const requireRestSample: IRelationshipConstraint = {
  ...sampleWithRequiredData,
};

describe('RelationshipConstraint Service', () => {
  let service: RelationshipConstraintService;
  let httpMock: HttpTestingController;
  let expectedResult: IRelationshipConstraint | IRelationshipConstraint[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RelationshipConstraintService);
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

    it('should create a RelationshipConstraint', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const relationshipConstraint = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(relationshipConstraint).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RelationshipConstraint', () => {
      const relationshipConstraint = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(relationshipConstraint).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RelationshipConstraint', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RelationshipConstraint', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RelationshipConstraint', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRelationshipConstraintToCollectionIfMissing', () => {
      it('should add a RelationshipConstraint to an empty array', () => {
        const relationshipConstraint: IRelationshipConstraint = sampleWithRequiredData;
        expectedResult = service.addRelationshipConstraintToCollectionIfMissing([], relationshipConstraint);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relationshipConstraint);
      });

      it('should not add a RelationshipConstraint to an array that contains it', () => {
        const relationshipConstraint: IRelationshipConstraint = sampleWithRequiredData;
        const relationshipConstraintCollection: IRelationshipConstraint[] = [
          {
            ...relationshipConstraint,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRelationshipConstraintToCollectionIfMissing(relationshipConstraintCollection, relationshipConstraint);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RelationshipConstraint to an array that doesn't contain it", () => {
        const relationshipConstraint: IRelationshipConstraint = sampleWithRequiredData;
        const relationshipConstraintCollection: IRelationshipConstraint[] = [sampleWithPartialData];
        expectedResult = service.addRelationshipConstraintToCollectionIfMissing(relationshipConstraintCollection, relationshipConstraint);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relationshipConstraint);
      });

      it('should add only unique RelationshipConstraint to an array', () => {
        const relationshipConstraintArray: IRelationshipConstraint[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const relationshipConstraintCollection: IRelationshipConstraint[] = [sampleWithRequiredData];
        expectedResult = service.addRelationshipConstraintToCollectionIfMissing(
          relationshipConstraintCollection,
          ...relationshipConstraintArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const relationshipConstraint: IRelationshipConstraint = sampleWithRequiredData;
        const relationshipConstraint2: IRelationshipConstraint = sampleWithPartialData;
        expectedResult = service.addRelationshipConstraintToCollectionIfMissing([], relationshipConstraint, relationshipConstraint2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relationshipConstraint);
        expect(expectedResult).toContain(relationshipConstraint2);
      });

      it('should accept null and undefined values', () => {
        const relationshipConstraint: IRelationshipConstraint = sampleWithRequiredData;
        expectedResult = service.addRelationshipConstraintToCollectionIfMissing([], null, relationshipConstraint, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relationshipConstraint);
      });

      it('should return initial array if no RelationshipConstraint is added', () => {
        const relationshipConstraintCollection: IRelationshipConstraint[] = [sampleWithRequiredData];
        expectedResult = service.addRelationshipConstraintToCollectionIfMissing(relationshipConstraintCollection, undefined, null);
        expect(expectedResult).toEqual(relationshipConstraintCollection);
      });
    });

    describe('compareRelationshipConstraint', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRelationshipConstraint(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareRelationshipConstraint(entity1, entity2);
        const compareResult2 = service.compareRelationshipConstraint(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareRelationshipConstraint(entity1, entity2);
        const compareResult2 = service.compareRelationshipConstraint(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareRelationshipConstraint(entity1, entity2);
        const compareResult2 = service.compareRelationshipConstraint(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
