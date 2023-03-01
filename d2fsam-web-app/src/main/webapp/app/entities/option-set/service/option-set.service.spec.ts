import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOptionSet } from '../option-set.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../option-set.test-samples';

import { OptionSetService, RestOptionSet } from './option-set.service';

const requireRestSample: RestOptionSet = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('OptionSet Service', () => {
  let service: OptionSetService;
  let httpMock: HttpTestingController;
  let expectedResult: IOptionSet | IOptionSet[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OptionSetService);
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

    it('should create a OptionSet', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const optionSet = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(optionSet).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OptionSet', () => {
      const optionSet = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(optionSet).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OptionSet', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OptionSet', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OptionSet', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOptionSetToCollectionIfMissing', () => {
      it('should add a OptionSet to an empty array', () => {
        const optionSet: IOptionSet = sampleWithRequiredData;
        expectedResult = service.addOptionSetToCollectionIfMissing([], optionSet);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(optionSet);
      });

      it('should not add a OptionSet to an array that contains it', () => {
        const optionSet: IOptionSet = sampleWithRequiredData;
        const optionSetCollection: IOptionSet[] = [
          {
            ...optionSet,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOptionSetToCollectionIfMissing(optionSetCollection, optionSet);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OptionSet to an array that doesn't contain it", () => {
        const optionSet: IOptionSet = sampleWithRequiredData;
        const optionSetCollection: IOptionSet[] = [sampleWithPartialData];
        expectedResult = service.addOptionSetToCollectionIfMissing(optionSetCollection, optionSet);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(optionSet);
      });

      it('should add only unique OptionSet to an array', () => {
        const optionSetArray: IOptionSet[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const optionSetCollection: IOptionSet[] = [sampleWithRequiredData];
        expectedResult = service.addOptionSetToCollectionIfMissing(optionSetCollection, ...optionSetArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const optionSet: IOptionSet = sampleWithRequiredData;
        const optionSet2: IOptionSet = sampleWithPartialData;
        expectedResult = service.addOptionSetToCollectionIfMissing([], optionSet, optionSet2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(optionSet);
        expect(expectedResult).toContain(optionSet2);
      });

      it('should accept null and undefined values', () => {
        const optionSet: IOptionSet = sampleWithRequiredData;
        expectedResult = service.addOptionSetToCollectionIfMissing([], null, optionSet, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(optionSet);
      });

      it('should return initial array if no OptionSet is added', () => {
        const optionSetCollection: IOptionSet[] = [sampleWithRequiredData];
        expectedResult = service.addOptionSetToCollectionIfMissing(optionSetCollection, undefined, null);
        expect(expectedResult).toEqual(optionSetCollection);
      });
    });

    describe('compareOptionSet', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOptionSet(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareOptionSet(entity1, entity2);
        const compareResult2 = service.compareOptionSet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareOptionSet(entity1, entity2);
        const compareResult2 = service.compareOptionSet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareOptionSet(entity1, entity2);
        const compareResult2 = service.compareOptionSet(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
