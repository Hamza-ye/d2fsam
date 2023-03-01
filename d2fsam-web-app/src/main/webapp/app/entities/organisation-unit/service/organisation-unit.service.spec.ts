import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IOrganisationUnit } from '../organisation-unit.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../organisation-unit.test-samples';

import { OrganisationUnitService, RestOrganisationUnit } from './organisation-unit.service';

const requireRestSample: RestOrganisationUnit = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
  openingDate: sampleWithRequiredData.openingDate?.format(DATE_FORMAT),
  closedDate: sampleWithRequiredData.closedDate?.format(DATE_FORMAT),
};

describe('OrganisationUnit Service', () => {
  let service: OrganisationUnitService;
  let httpMock: HttpTestingController;
  let expectedResult: IOrganisationUnit | IOrganisationUnit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrganisationUnitService);
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

    it('should create a OrganisationUnit', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const organisationUnit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(organisationUnit).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrganisationUnit', () => {
      const organisationUnit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(organisationUnit).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OrganisationUnit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrganisationUnit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OrganisationUnit', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOrganisationUnitToCollectionIfMissing', () => {
      it('should add a OrganisationUnit to an empty array', () => {
        const organisationUnit: IOrganisationUnit = sampleWithRequiredData;
        expectedResult = service.addOrganisationUnitToCollectionIfMissing([], organisationUnit);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(organisationUnit);
      });

      it('should not add a OrganisationUnit to an array that contains it', () => {
        const organisationUnit: IOrganisationUnit = sampleWithRequiredData;
        const organisationUnitCollection: IOrganisationUnit[] = [
          {
            ...organisationUnit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOrganisationUnitToCollectionIfMissing(organisationUnitCollection, organisationUnit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrganisationUnit to an array that doesn't contain it", () => {
        const organisationUnit: IOrganisationUnit = sampleWithRequiredData;
        const organisationUnitCollection: IOrganisationUnit[] = [sampleWithPartialData];
        expectedResult = service.addOrganisationUnitToCollectionIfMissing(organisationUnitCollection, organisationUnit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(organisationUnit);
      });

      it('should add only unique OrganisationUnit to an array', () => {
        const organisationUnitArray: IOrganisationUnit[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const organisationUnitCollection: IOrganisationUnit[] = [sampleWithRequiredData];
        expectedResult = service.addOrganisationUnitToCollectionIfMissing(organisationUnitCollection, ...organisationUnitArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const organisationUnit: IOrganisationUnit = sampleWithRequiredData;
        const organisationUnit2: IOrganisationUnit = sampleWithPartialData;
        expectedResult = service.addOrganisationUnitToCollectionIfMissing([], organisationUnit, organisationUnit2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(organisationUnit);
        expect(expectedResult).toContain(organisationUnit2);
      });

      it('should accept null and undefined values', () => {
        const organisationUnit: IOrganisationUnit = sampleWithRequiredData;
        expectedResult = service.addOrganisationUnitToCollectionIfMissing([], null, organisationUnit, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(organisationUnit);
      });

      it('should return initial array if no OrganisationUnit is added', () => {
        const organisationUnitCollection: IOrganisationUnit[] = [sampleWithRequiredData];
        expectedResult = service.addOrganisationUnitToCollectionIfMissing(organisationUnitCollection, undefined, null);
        expect(expectedResult).toEqual(organisationUnitCollection);
      });
    });

    describe('compareOrganisationUnit', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOrganisationUnit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareOrganisationUnit(entity1, entity2);
        const compareResult2 = service.compareOrganisationUnit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareOrganisationUnit(entity1, entity2);
        const compareResult2 = service.compareOrganisationUnit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareOrganisationUnit(entity1, entity2);
        const compareResult2 = service.compareOrganisationUnit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
