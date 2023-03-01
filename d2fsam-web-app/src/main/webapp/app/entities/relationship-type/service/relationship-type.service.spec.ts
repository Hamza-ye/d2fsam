import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRelationshipType } from '../relationship-type.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../relationship-type.test-samples';

import { RelationshipTypeService, RestRelationshipType } from './relationship-type.service';

const requireRestSample: RestRelationshipType = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('RelationshipType Service', () => {
  let service: RelationshipTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: IRelationshipType | IRelationshipType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RelationshipTypeService);
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

    it('should create a RelationshipType', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const relationshipType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(relationshipType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RelationshipType', () => {
      const relationshipType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(relationshipType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RelationshipType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RelationshipType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RelationshipType', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRelationshipTypeToCollectionIfMissing', () => {
      it('should add a RelationshipType to an empty array', () => {
        const relationshipType: IRelationshipType = sampleWithRequiredData;
        expectedResult = service.addRelationshipTypeToCollectionIfMissing([], relationshipType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relationshipType);
      });

      it('should not add a RelationshipType to an array that contains it', () => {
        const relationshipType: IRelationshipType = sampleWithRequiredData;
        const relationshipTypeCollection: IRelationshipType[] = [
          {
            ...relationshipType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRelationshipTypeToCollectionIfMissing(relationshipTypeCollection, relationshipType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RelationshipType to an array that doesn't contain it", () => {
        const relationshipType: IRelationshipType = sampleWithRequiredData;
        const relationshipTypeCollection: IRelationshipType[] = [sampleWithPartialData];
        expectedResult = service.addRelationshipTypeToCollectionIfMissing(relationshipTypeCollection, relationshipType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relationshipType);
      });

      it('should add only unique RelationshipType to an array', () => {
        const relationshipTypeArray: IRelationshipType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const relationshipTypeCollection: IRelationshipType[] = [sampleWithRequiredData];
        expectedResult = service.addRelationshipTypeToCollectionIfMissing(relationshipTypeCollection, ...relationshipTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const relationshipType: IRelationshipType = sampleWithRequiredData;
        const relationshipType2: IRelationshipType = sampleWithPartialData;
        expectedResult = service.addRelationshipTypeToCollectionIfMissing([], relationshipType, relationshipType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relationshipType);
        expect(expectedResult).toContain(relationshipType2);
      });

      it('should accept null and undefined values', () => {
        const relationshipType: IRelationshipType = sampleWithRequiredData;
        expectedResult = service.addRelationshipTypeToCollectionIfMissing([], null, relationshipType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relationshipType);
      });

      it('should return initial array if no RelationshipType is added', () => {
        const relationshipTypeCollection: IRelationshipType[] = [sampleWithRequiredData];
        expectedResult = service.addRelationshipTypeToCollectionIfMissing(relationshipTypeCollection, undefined, null);
        expect(expectedResult).toEqual(relationshipTypeCollection);
      });
    });

    describe('compareRelationshipType', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRelationshipType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareRelationshipType(entity1, entity2);
        const compareResult2 = service.compareRelationshipType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareRelationshipType(entity1, entity2);
        const compareResult2 = service.compareRelationshipType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareRelationshipType(entity1, entity2);
        const compareResult2 = service.compareRelationshipType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
