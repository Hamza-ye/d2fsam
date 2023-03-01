import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRelationship } from '../relationship.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../relationship.test-samples';

import { RelationshipService, RestRelationship } from './relationship.service';

const requireRestSample: RestRelationship = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('Relationship Service', () => {
  let service: RelationshipService;
  let httpMock: HttpTestingController;
  let expectedResult: IRelationship | IRelationship[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RelationshipService);
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

    it('should create a Relationship', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const relationship = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(relationship).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Relationship', () => {
      const relationship = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(relationship).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Relationship', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Relationship', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Relationship', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRelationshipToCollectionIfMissing', () => {
      it('should add a Relationship to an empty array', () => {
        const relationship: IRelationship = sampleWithRequiredData;
        expectedResult = service.addRelationshipToCollectionIfMissing([], relationship);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relationship);
      });

      it('should not add a Relationship to an array that contains it', () => {
        const relationship: IRelationship = sampleWithRequiredData;
        const relationshipCollection: IRelationship[] = [
          {
            ...relationship,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRelationshipToCollectionIfMissing(relationshipCollection, relationship);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Relationship to an array that doesn't contain it", () => {
        const relationship: IRelationship = sampleWithRequiredData;
        const relationshipCollection: IRelationship[] = [sampleWithPartialData];
        expectedResult = service.addRelationshipToCollectionIfMissing(relationshipCollection, relationship);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relationship);
      });

      it('should add only unique Relationship to an array', () => {
        const relationshipArray: IRelationship[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const relationshipCollection: IRelationship[] = [sampleWithRequiredData];
        expectedResult = service.addRelationshipToCollectionIfMissing(relationshipCollection, ...relationshipArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const relationship: IRelationship = sampleWithRequiredData;
        const relationship2: IRelationship = sampleWithPartialData;
        expectedResult = service.addRelationshipToCollectionIfMissing([], relationship, relationship2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relationship);
        expect(expectedResult).toContain(relationship2);
      });

      it('should accept null and undefined values', () => {
        const relationship: IRelationship = sampleWithRequiredData;
        expectedResult = service.addRelationshipToCollectionIfMissing([], null, relationship, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relationship);
      });

      it('should return initial array if no Relationship is added', () => {
        const relationshipCollection: IRelationship[] = [sampleWithRequiredData];
        expectedResult = service.addRelationshipToCollectionIfMissing(relationshipCollection, undefined, null);
        expect(expectedResult).toEqual(relationshipCollection);
      });
    });

    describe('compareRelationship', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRelationship(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareRelationship(entity1, entity2);
        const compareResult2 = service.compareRelationship(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareRelationship(entity1, entity2);
        const compareResult2 = service.compareRelationship(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareRelationship(entity1, entity2);
        const compareResult2 = service.compareRelationship(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
