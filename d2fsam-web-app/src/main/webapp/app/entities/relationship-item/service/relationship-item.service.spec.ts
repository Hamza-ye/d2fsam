import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRelationshipItem } from '../relationship-item.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../relationship-item.test-samples';

import { RelationshipItemService } from './relationship-item.service';

const requireRestSample: IRelationshipItem = {
  ...sampleWithRequiredData,
};

describe('RelationshipItem Service', () => {
  let service: RelationshipItemService;
  let httpMock: HttpTestingController;
  let expectedResult: IRelationshipItem | IRelationshipItem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RelationshipItemService);
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

    it('should create a RelationshipItem', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const relationshipItem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(relationshipItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RelationshipItem', () => {
      const relationshipItem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(relationshipItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RelationshipItem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RelationshipItem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RelationshipItem', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRelationshipItemToCollectionIfMissing', () => {
      it('should add a RelationshipItem to an empty array', () => {
        const relationshipItem: IRelationshipItem = sampleWithRequiredData;
        expectedResult = service.addRelationshipItemToCollectionIfMissing([], relationshipItem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relationshipItem);
      });

      it('should not add a RelationshipItem to an array that contains it', () => {
        const relationshipItem: IRelationshipItem = sampleWithRequiredData;
        const relationshipItemCollection: IRelationshipItem[] = [
          {
            ...relationshipItem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRelationshipItemToCollectionIfMissing(relationshipItemCollection, relationshipItem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RelationshipItem to an array that doesn't contain it", () => {
        const relationshipItem: IRelationshipItem = sampleWithRequiredData;
        const relationshipItemCollection: IRelationshipItem[] = [sampleWithPartialData];
        expectedResult = service.addRelationshipItemToCollectionIfMissing(relationshipItemCollection, relationshipItem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relationshipItem);
      });

      it('should add only unique RelationshipItem to an array', () => {
        const relationshipItemArray: IRelationshipItem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const relationshipItemCollection: IRelationshipItem[] = [sampleWithRequiredData];
        expectedResult = service.addRelationshipItemToCollectionIfMissing(relationshipItemCollection, ...relationshipItemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const relationshipItem: IRelationshipItem = sampleWithRequiredData;
        const relationshipItem2: IRelationshipItem = sampleWithPartialData;
        expectedResult = service.addRelationshipItemToCollectionIfMissing([], relationshipItem, relationshipItem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relationshipItem);
        expect(expectedResult).toContain(relationshipItem2);
      });

      it('should accept null and undefined values', () => {
        const relationshipItem: IRelationshipItem = sampleWithRequiredData;
        expectedResult = service.addRelationshipItemToCollectionIfMissing([], null, relationshipItem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relationshipItem);
      });

      it('should return initial array if no RelationshipItem is added', () => {
        const relationshipItemCollection: IRelationshipItem[] = [sampleWithRequiredData];
        expectedResult = service.addRelationshipItemToCollectionIfMissing(relationshipItemCollection, undefined, null);
        expect(expectedResult).toEqual(relationshipItemCollection);
      });
    });

    describe('compareRelationshipItem', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRelationshipItem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareRelationshipItem(entity1, entity2);
        const compareResult2 = service.compareRelationshipItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareRelationshipItem(entity1, entity2);
        const compareResult2 = service.compareRelationshipItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareRelationshipItem(entity1, entity2);
        const compareResult2 = service.compareRelationshipItem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
