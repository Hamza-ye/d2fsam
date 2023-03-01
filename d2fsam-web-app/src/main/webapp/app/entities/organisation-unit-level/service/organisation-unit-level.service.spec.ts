import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOrganisationUnitLevel } from '../organisation-unit-level.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../organisation-unit-level.test-samples';

import { OrganisationUnitLevelService, RestOrganisationUnitLevel } from './organisation-unit-level.service';

const requireRestSample: RestOrganisationUnitLevel = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('OrganisationUnitLevel Service', () => {
  let service: OrganisationUnitLevelService;
  let httpMock: HttpTestingController;
  let expectedResult: IOrganisationUnitLevel | IOrganisationUnitLevel[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrganisationUnitLevelService);
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

    it('should create a OrganisationUnitLevel', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const organisationUnitLevel = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(organisationUnitLevel).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrganisationUnitLevel', () => {
      const organisationUnitLevel = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(organisationUnitLevel).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OrganisationUnitLevel', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrganisationUnitLevel', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OrganisationUnitLevel', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOrganisationUnitLevelToCollectionIfMissing', () => {
      it('should add a OrganisationUnitLevel to an empty array', () => {
        const organisationUnitLevel: IOrganisationUnitLevel = sampleWithRequiredData;
        expectedResult = service.addOrganisationUnitLevelToCollectionIfMissing([], organisationUnitLevel);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(organisationUnitLevel);
      });

      it('should not add a OrganisationUnitLevel to an array that contains it', () => {
        const organisationUnitLevel: IOrganisationUnitLevel = sampleWithRequiredData;
        const organisationUnitLevelCollection: IOrganisationUnitLevel[] = [
          {
            ...organisationUnitLevel,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOrganisationUnitLevelToCollectionIfMissing(organisationUnitLevelCollection, organisationUnitLevel);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrganisationUnitLevel to an array that doesn't contain it", () => {
        const organisationUnitLevel: IOrganisationUnitLevel = sampleWithRequiredData;
        const organisationUnitLevelCollection: IOrganisationUnitLevel[] = [sampleWithPartialData];
        expectedResult = service.addOrganisationUnitLevelToCollectionIfMissing(organisationUnitLevelCollection, organisationUnitLevel);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(organisationUnitLevel);
      });

      it('should add only unique OrganisationUnitLevel to an array', () => {
        const organisationUnitLevelArray: IOrganisationUnitLevel[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const organisationUnitLevelCollection: IOrganisationUnitLevel[] = [sampleWithRequiredData];
        expectedResult = service.addOrganisationUnitLevelToCollectionIfMissing(
          organisationUnitLevelCollection,
          ...organisationUnitLevelArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const organisationUnitLevel: IOrganisationUnitLevel = sampleWithRequiredData;
        const organisationUnitLevel2: IOrganisationUnitLevel = sampleWithPartialData;
        expectedResult = service.addOrganisationUnitLevelToCollectionIfMissing([], organisationUnitLevel, organisationUnitLevel2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(organisationUnitLevel);
        expect(expectedResult).toContain(organisationUnitLevel2);
      });

      it('should accept null and undefined values', () => {
        const organisationUnitLevel: IOrganisationUnitLevel = sampleWithRequiredData;
        expectedResult = service.addOrganisationUnitLevelToCollectionIfMissing([], null, organisationUnitLevel, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(organisationUnitLevel);
      });

      it('should return initial array if no OrganisationUnitLevel is added', () => {
        const organisationUnitLevelCollection: IOrganisationUnitLevel[] = [sampleWithRequiredData];
        expectedResult = service.addOrganisationUnitLevelToCollectionIfMissing(organisationUnitLevelCollection, undefined, null);
        expect(expectedResult).toEqual(organisationUnitLevelCollection);
      });
    });

    describe('compareOrganisationUnitLevel', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOrganisationUnitLevel(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareOrganisationUnitLevel(entity1, entity2);
        const compareResult2 = service.compareOrganisationUnitLevel(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareOrganisationUnitLevel(entity1, entity2);
        const compareResult2 = service.compareOrganisationUnitLevel(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareOrganisationUnitLevel(entity1, entity2);
        const compareResult2 = service.compareOrganisationUnitLevel(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
