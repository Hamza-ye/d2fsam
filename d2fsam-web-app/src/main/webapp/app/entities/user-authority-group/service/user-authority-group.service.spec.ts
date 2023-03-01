import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUserAuthorityGroup } from '../user-authority-group.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../user-authority-group.test-samples';

import { UserAuthorityGroupService, RestUserAuthorityGroup } from './user-authority-group.service';

const requireRestSample: RestUserAuthorityGroup = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('UserAuthorityGroup Service', () => {
  let service: UserAuthorityGroupService;
  let httpMock: HttpTestingController;
  let expectedResult: IUserAuthorityGroup | IUserAuthorityGroup[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UserAuthorityGroupService);
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

    it('should create a UserAuthorityGroup', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const userAuthorityGroup = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(userAuthorityGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserAuthorityGroup', () => {
      const userAuthorityGroup = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(userAuthorityGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserAuthorityGroup', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserAuthorityGroup', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a UserAuthorityGroup', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addUserAuthorityGroupToCollectionIfMissing', () => {
      it('should add a UserAuthorityGroup to an empty array', () => {
        const userAuthorityGroup: IUserAuthorityGroup = sampleWithRequiredData;
        expectedResult = service.addUserAuthorityGroupToCollectionIfMissing([], userAuthorityGroup);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userAuthorityGroup);
      });

      it('should not add a UserAuthorityGroup to an array that contains it', () => {
        const userAuthorityGroup: IUserAuthorityGroup = sampleWithRequiredData;
        const userAuthorityGroupCollection: IUserAuthorityGroup[] = [
          {
            ...userAuthorityGroup,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addUserAuthorityGroupToCollectionIfMissing(userAuthorityGroupCollection, userAuthorityGroup);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserAuthorityGroup to an array that doesn't contain it", () => {
        const userAuthorityGroup: IUserAuthorityGroup = sampleWithRequiredData;
        const userAuthorityGroupCollection: IUserAuthorityGroup[] = [sampleWithPartialData];
        expectedResult = service.addUserAuthorityGroupToCollectionIfMissing(userAuthorityGroupCollection, userAuthorityGroup);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userAuthorityGroup);
      });

      it('should add only unique UserAuthorityGroup to an array', () => {
        const userAuthorityGroupArray: IUserAuthorityGroup[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const userAuthorityGroupCollection: IUserAuthorityGroup[] = [sampleWithRequiredData];
        expectedResult = service.addUserAuthorityGroupToCollectionIfMissing(userAuthorityGroupCollection, ...userAuthorityGroupArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userAuthorityGroup: IUserAuthorityGroup = sampleWithRequiredData;
        const userAuthorityGroup2: IUserAuthorityGroup = sampleWithPartialData;
        expectedResult = service.addUserAuthorityGroupToCollectionIfMissing([], userAuthorityGroup, userAuthorityGroup2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userAuthorityGroup);
        expect(expectedResult).toContain(userAuthorityGroup2);
      });

      it('should accept null and undefined values', () => {
        const userAuthorityGroup: IUserAuthorityGroup = sampleWithRequiredData;
        expectedResult = service.addUserAuthorityGroupToCollectionIfMissing([], null, userAuthorityGroup, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userAuthorityGroup);
      });

      it('should return initial array if no UserAuthorityGroup is added', () => {
        const userAuthorityGroupCollection: IUserAuthorityGroup[] = [sampleWithRequiredData];
        expectedResult = service.addUserAuthorityGroupToCollectionIfMissing(userAuthorityGroupCollection, undefined, null);
        expect(expectedResult).toEqual(userAuthorityGroupCollection);
      });
    });

    describe('compareUserAuthorityGroup', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareUserAuthorityGroup(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareUserAuthorityGroup(entity1, entity2);
        const compareResult2 = service.compareUserAuthorityGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareUserAuthorityGroup(entity1, entity2);
        const compareResult2 = service.compareUserAuthorityGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareUserAuthorityGroup(entity1, entity2);
        const compareResult2 = service.compareUserAuthorityGroup(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
