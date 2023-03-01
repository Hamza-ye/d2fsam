import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDemographicDataSource } from '../demographic-data-source.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../demographic-data-source.test-samples';

import { DemographicDataSourceService, RestDemographicDataSource } from './demographic-data-source.service';

const requireRestSample: RestDemographicDataSource = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('DemographicDataSource Service', () => {
  let service: DemographicDataSourceService;
  let httpMock: HttpTestingController;
  let expectedResult: IDemographicDataSource | IDemographicDataSource[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DemographicDataSourceService);
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

    it('should create a DemographicDataSource', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const demographicDataSource = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(demographicDataSource).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DemographicDataSource', () => {
      const demographicDataSource = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(demographicDataSource).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DemographicDataSource', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DemographicDataSource', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DemographicDataSource', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDemographicDataSourceToCollectionIfMissing', () => {
      it('should add a DemographicDataSource to an empty array', () => {
        const demographicDataSource: IDemographicDataSource = sampleWithRequiredData;
        expectedResult = service.addDemographicDataSourceToCollectionIfMissing([], demographicDataSource);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(demographicDataSource);
      });

      it('should not add a DemographicDataSource to an array that contains it', () => {
        const demographicDataSource: IDemographicDataSource = sampleWithRequiredData;
        const demographicDataSourceCollection: IDemographicDataSource[] = [
          {
            ...demographicDataSource,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDemographicDataSourceToCollectionIfMissing(demographicDataSourceCollection, demographicDataSource);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DemographicDataSource to an array that doesn't contain it", () => {
        const demographicDataSource: IDemographicDataSource = sampleWithRequiredData;
        const demographicDataSourceCollection: IDemographicDataSource[] = [sampleWithPartialData];
        expectedResult = service.addDemographicDataSourceToCollectionIfMissing(demographicDataSourceCollection, demographicDataSource);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(demographicDataSource);
      });

      it('should add only unique DemographicDataSource to an array', () => {
        const demographicDataSourceArray: IDemographicDataSource[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const demographicDataSourceCollection: IDemographicDataSource[] = [sampleWithRequiredData];
        expectedResult = service.addDemographicDataSourceToCollectionIfMissing(
          demographicDataSourceCollection,
          ...demographicDataSourceArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const demographicDataSource: IDemographicDataSource = sampleWithRequiredData;
        const demographicDataSource2: IDemographicDataSource = sampleWithPartialData;
        expectedResult = service.addDemographicDataSourceToCollectionIfMissing([], demographicDataSource, demographicDataSource2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(demographicDataSource);
        expect(expectedResult).toContain(demographicDataSource2);
      });

      it('should accept null and undefined values', () => {
        const demographicDataSource: IDemographicDataSource = sampleWithRequiredData;
        expectedResult = service.addDemographicDataSourceToCollectionIfMissing([], null, demographicDataSource, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(demographicDataSource);
      });

      it('should return initial array if no DemographicDataSource is added', () => {
        const demographicDataSourceCollection: IDemographicDataSource[] = [sampleWithRequiredData];
        expectedResult = service.addDemographicDataSourceToCollectionIfMissing(demographicDataSourceCollection, undefined, null);
        expect(expectedResult).toEqual(demographicDataSourceCollection);
      });
    });

    describe('compareDemographicDataSource', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDemographicDataSource(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDemographicDataSource(entity1, entity2);
        const compareResult2 = service.compareDemographicDataSource(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDemographicDataSource(entity1, entity2);
        const compareResult2 = service.compareDemographicDataSource(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDemographicDataSource(entity1, entity2);
        const compareResult2 = service.compareDemographicDataSource(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
