import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IUserGroup } from '../user-group.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../user-group.test-samples';

import { UserGroupService, RestUserGroup } from './user-group.service';

const requireRestSample: RestUserGroup = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
  activeFrom: sampleWithRequiredData.activeFrom?.toJSON(),
  activeTo: sampleWithRequiredData.activeTo?.toJSON(),
};

describe('UserGroup Service', () => {
  let service: UserGroupService;
  let httpMock: HttpTestingController;
  let expectedResult: IUserGroup | IUserGroup[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(UserGroupService);
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

    it('should create a UserGroup', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const userGroup = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(userGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a UserGroup', () => {
      const userGroup = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(userGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a UserGroup', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of UserGroup', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a UserGroup', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addUserGroupToCollectionIfMissing', () => {
      it('should add a UserGroup to an empty array', () => {
        const userGroup: IUserGroup = sampleWithRequiredData;
        expectedResult = service.addUserGroupToCollectionIfMissing([], userGroup);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userGroup);
      });

      it('should not add a UserGroup to an array that contains it', () => {
        const userGroup: IUserGroup = sampleWithRequiredData;
        const userGroupCollection: IUserGroup[] = [
          {
            ...userGroup,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addUserGroupToCollectionIfMissing(userGroupCollection, userGroup);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a UserGroup to an array that doesn't contain it", () => {
        const userGroup: IUserGroup = sampleWithRequiredData;
        const userGroupCollection: IUserGroup[] = [sampleWithPartialData];
        expectedResult = service.addUserGroupToCollectionIfMissing(userGroupCollection, userGroup);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userGroup);
      });

      it('should add only unique UserGroup to an array', () => {
        const userGroupArray: IUserGroup[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const userGroupCollection: IUserGroup[] = [sampleWithRequiredData];
        expectedResult = service.addUserGroupToCollectionIfMissing(userGroupCollection, ...userGroupArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const userGroup: IUserGroup = sampleWithRequiredData;
        const userGroup2: IUserGroup = sampleWithPartialData;
        expectedResult = service.addUserGroupToCollectionIfMissing([], userGroup, userGroup2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(userGroup);
        expect(expectedResult).toContain(userGroup2);
      });

      it('should accept null and undefined values', () => {
        const userGroup: IUserGroup = sampleWithRequiredData;
        expectedResult = service.addUserGroupToCollectionIfMissing([], null, userGroup, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(userGroup);
      });

      it('should return initial array if no UserGroup is added', () => {
        const userGroupCollection: IUserGroup[] = [sampleWithRequiredData];
        expectedResult = service.addUserGroupToCollectionIfMissing(userGroupCollection, undefined, null);
        expect(expectedResult).toEqual(userGroupCollection);
      });
    });

    describe('compareUserGroup', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareUserGroup(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareUserGroup(entity1, entity2);
        const compareResult2 = service.compareUserGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareUserGroup(entity1, entity2);
        const compareResult2 = service.compareUserGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareUserGroup(entity1, entity2);
        const compareResult2 = service.compareUserGroup(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
