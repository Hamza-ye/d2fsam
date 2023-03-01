import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOrganisationUnitGroup } from '../organisation-unit-group.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../organisation-unit-group.test-samples';

import { OrganisationUnitGroupService, RestOrganisationUnitGroup } from './organisation-unit-group.service';

const requireRestSample: RestOrganisationUnitGroup = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('OrganisationUnitGroup Service', () => {
  let service: OrganisationUnitGroupService;
  let httpMock: HttpTestingController;
  let expectedResult: IOrganisationUnitGroup | IOrganisationUnitGroup[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrganisationUnitGroupService);
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

    it('should create a OrganisationUnitGroup', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const organisationUnitGroup = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(organisationUnitGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrganisationUnitGroup', () => {
      const organisationUnitGroup = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(organisationUnitGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OrganisationUnitGroup', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrganisationUnitGroup', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OrganisationUnitGroup', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOrganisationUnitGroupToCollectionIfMissing', () => {
      it('should add a OrganisationUnitGroup to an empty array', () => {
        const organisationUnitGroup: IOrganisationUnitGroup = sampleWithRequiredData;
        expectedResult = service.addOrganisationUnitGroupToCollectionIfMissing([], organisationUnitGroup);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(organisationUnitGroup);
      });

      it('should not add a OrganisationUnitGroup to an array that contains it', () => {
        const organisationUnitGroup: IOrganisationUnitGroup = sampleWithRequiredData;
        const organisationUnitGroupCollection: IOrganisationUnitGroup[] = [
          {
            ...organisationUnitGroup,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOrganisationUnitGroupToCollectionIfMissing(organisationUnitGroupCollection, organisationUnitGroup);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrganisationUnitGroup to an array that doesn't contain it", () => {
        const organisationUnitGroup: IOrganisationUnitGroup = sampleWithRequiredData;
        const organisationUnitGroupCollection: IOrganisationUnitGroup[] = [sampleWithPartialData];
        expectedResult = service.addOrganisationUnitGroupToCollectionIfMissing(organisationUnitGroupCollection, organisationUnitGroup);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(organisationUnitGroup);
      });

      it('should add only unique OrganisationUnitGroup to an array', () => {
        const organisationUnitGroupArray: IOrganisationUnitGroup[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const organisationUnitGroupCollection: IOrganisationUnitGroup[] = [sampleWithRequiredData];
        expectedResult = service.addOrganisationUnitGroupToCollectionIfMissing(
          organisationUnitGroupCollection,
          ...organisationUnitGroupArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const organisationUnitGroup: IOrganisationUnitGroup = sampleWithRequiredData;
        const organisationUnitGroup2: IOrganisationUnitGroup = sampleWithPartialData;
        expectedResult = service.addOrganisationUnitGroupToCollectionIfMissing([], organisationUnitGroup, organisationUnitGroup2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(organisationUnitGroup);
        expect(expectedResult).toContain(organisationUnitGroup2);
      });

      it('should accept null and undefined values', () => {
        const organisationUnitGroup: IOrganisationUnitGroup = sampleWithRequiredData;
        expectedResult = service.addOrganisationUnitGroupToCollectionIfMissing([], null, organisationUnitGroup, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(organisationUnitGroup);
      });

      it('should return initial array if no OrganisationUnitGroup is added', () => {
        const organisationUnitGroupCollection: IOrganisationUnitGroup[] = [sampleWithRequiredData];
        expectedResult = service.addOrganisationUnitGroupToCollectionIfMissing(organisationUnitGroupCollection, undefined, null);
        expect(expectedResult).toEqual(organisationUnitGroupCollection);
      });
    });

    describe('compareOrganisationUnitGroup', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOrganisationUnitGroup(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareOrganisationUnitGroup(entity1, entity2);
        const compareResult2 = service.compareOrganisationUnitGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareOrganisationUnitGroup(entity1, entity2);
        const compareResult2 = service.compareOrganisationUnitGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareOrganisationUnitGroup(entity1, entity2);
        const compareResult2 = service.compareOrganisationUnitGroup(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
