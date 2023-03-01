import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDataElement } from '../data-element.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../data-element.test-samples';

import { DataElementService, RestDataElement } from './data-element.service';

const requireRestSample: RestDataElement = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('DataElement Service', () => {
  let service: DataElementService;
  let httpMock: HttpTestingController;
  let expectedResult: IDataElement | IDataElement[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DataElementService);
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

    it('should create a DataElement', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const dataElement = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(dataElement).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DataElement', () => {
      const dataElement = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(dataElement).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DataElement', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DataElement', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DataElement', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDataElementToCollectionIfMissing', () => {
      it('should add a DataElement to an empty array', () => {
        const dataElement: IDataElement = sampleWithRequiredData;
        expectedResult = service.addDataElementToCollectionIfMissing([], dataElement);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dataElement);
      });

      it('should not add a DataElement to an array that contains it', () => {
        const dataElement: IDataElement = sampleWithRequiredData;
        const dataElementCollection: IDataElement[] = [
          {
            ...dataElement,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDataElementToCollectionIfMissing(dataElementCollection, dataElement);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DataElement to an array that doesn't contain it", () => {
        const dataElement: IDataElement = sampleWithRequiredData;
        const dataElementCollection: IDataElement[] = [sampleWithPartialData];
        expectedResult = service.addDataElementToCollectionIfMissing(dataElementCollection, dataElement);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dataElement);
      });

      it('should add only unique DataElement to an array', () => {
        const dataElementArray: IDataElement[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const dataElementCollection: IDataElement[] = [sampleWithRequiredData];
        expectedResult = service.addDataElementToCollectionIfMissing(dataElementCollection, ...dataElementArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dataElement: IDataElement = sampleWithRequiredData;
        const dataElement2: IDataElement = sampleWithPartialData;
        expectedResult = service.addDataElementToCollectionIfMissing([], dataElement, dataElement2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dataElement);
        expect(expectedResult).toContain(dataElement2);
      });

      it('should accept null and undefined values', () => {
        const dataElement: IDataElement = sampleWithRequiredData;
        expectedResult = service.addDataElementToCollectionIfMissing([], null, dataElement, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dataElement);
      });

      it('should return initial array if no DataElement is added', () => {
        const dataElementCollection: IDataElement[] = [sampleWithRequiredData];
        expectedResult = service.addDataElementToCollectionIfMissing(dataElementCollection, undefined, null);
        expect(expectedResult).toEqual(dataElementCollection);
      });
    });

    describe('compareDataElement', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDataElement(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDataElement(entity1, entity2);
        const compareResult2 = service.compareDataElement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDataElement(entity1, entity2);
        const compareResult2 = service.compareDataElement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDataElement(entity1, entity2);
        const compareResult2 = service.compareDataElement(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
