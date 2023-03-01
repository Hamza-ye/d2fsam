import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IDemographicData } from '../demographic-data.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../demographic-data.test-samples';

import { DemographicDataService, RestDemographicData } from './demographic-data.service';

const requireRestSample: RestDemographicData = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
  date: sampleWithRequiredData.date?.format(DATE_FORMAT),
};

describe('DemographicData Service', () => {
  let service: DemographicDataService;
  let httpMock: HttpTestingController;
  let expectedResult: IDemographicData | IDemographicData[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DemographicDataService);
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

    it('should create a DemographicData', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const demographicData = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(demographicData).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DemographicData', () => {
      const demographicData = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(demographicData).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DemographicData', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DemographicData', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DemographicData', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDemographicDataToCollectionIfMissing', () => {
      it('should add a DemographicData to an empty array', () => {
        const demographicData: IDemographicData = sampleWithRequiredData;
        expectedResult = service.addDemographicDataToCollectionIfMissing([], demographicData);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(demographicData);
      });

      it('should not add a DemographicData to an array that contains it', () => {
        const demographicData: IDemographicData = sampleWithRequiredData;
        const demographicDataCollection: IDemographicData[] = [
          {
            ...demographicData,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDemographicDataToCollectionIfMissing(demographicDataCollection, demographicData);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DemographicData to an array that doesn't contain it", () => {
        const demographicData: IDemographicData = sampleWithRequiredData;
        const demographicDataCollection: IDemographicData[] = [sampleWithPartialData];
        expectedResult = service.addDemographicDataToCollectionIfMissing(demographicDataCollection, demographicData);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(demographicData);
      });

      it('should add only unique DemographicData to an array', () => {
        const demographicDataArray: IDemographicData[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const demographicDataCollection: IDemographicData[] = [sampleWithRequiredData];
        expectedResult = service.addDemographicDataToCollectionIfMissing(demographicDataCollection, ...demographicDataArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const demographicData: IDemographicData = sampleWithRequiredData;
        const demographicData2: IDemographicData = sampleWithPartialData;
        expectedResult = service.addDemographicDataToCollectionIfMissing([], demographicData, demographicData2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(demographicData);
        expect(expectedResult).toContain(demographicData2);
      });

      it('should accept null and undefined values', () => {
        const demographicData: IDemographicData = sampleWithRequiredData;
        expectedResult = service.addDemographicDataToCollectionIfMissing([], null, demographicData, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(demographicData);
      });

      it('should return initial array if no DemographicData is added', () => {
        const demographicDataCollection: IDemographicData[] = [sampleWithRequiredData];
        expectedResult = service.addDemographicDataToCollectionIfMissing(demographicDataCollection, undefined, null);
        expect(expectedResult).toEqual(demographicDataCollection);
      });
    });

    describe('compareDemographicData', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDemographicData(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDemographicData(entity1, entity2);
        const compareResult2 = service.compareDemographicData(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDemographicData(entity1, entity2);
        const compareResult2 = service.compareDemographicData(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDemographicData(entity1, entity2);
        const compareResult2 = service.compareDemographicData(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
