import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IChv } from '../chv.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../chv.test-samples';

import { ChvService, RestChv } from './chv.service';

const requireRestSample: RestChv = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
  dateJoined: sampleWithRequiredData.dateJoined?.format(DATE_FORMAT),
  dateWithdrawn: sampleWithRequiredData.dateWithdrawn?.format(DATE_FORMAT),
};

describe('Chv Service', () => {
  let service: ChvService;
  let httpMock: HttpTestingController;
  let expectedResult: IChv | IChv[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ChvService);
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

    it('should create a Chv', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const chv = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(chv).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Chv', () => {
      const chv = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(chv).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Chv', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Chv', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Chv', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addChvToCollectionIfMissing', () => {
      it('should add a Chv to an empty array', () => {
        const chv: IChv = sampleWithRequiredData;
        expectedResult = service.addChvToCollectionIfMissing([], chv);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(chv);
      });

      it('should not add a Chv to an array that contains it', () => {
        const chv: IChv = sampleWithRequiredData;
        const chvCollection: IChv[] = [
          {
            ...chv,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addChvToCollectionIfMissing(chvCollection, chv);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Chv to an array that doesn't contain it", () => {
        const chv: IChv = sampleWithRequiredData;
        const chvCollection: IChv[] = [sampleWithPartialData];
        expectedResult = service.addChvToCollectionIfMissing(chvCollection, chv);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(chv);
      });

      it('should add only unique Chv to an array', () => {
        const chvArray: IChv[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const chvCollection: IChv[] = [sampleWithRequiredData];
        expectedResult = service.addChvToCollectionIfMissing(chvCollection, ...chvArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const chv: IChv = sampleWithRequiredData;
        const chv2: IChv = sampleWithPartialData;
        expectedResult = service.addChvToCollectionIfMissing([], chv, chv2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(chv);
        expect(expectedResult).toContain(chv2);
      });

      it('should accept null and undefined values', () => {
        const chv: IChv = sampleWithRequiredData;
        expectedResult = service.addChvToCollectionIfMissing([], null, chv, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(chv);
      });

      it('should return initial array if no Chv is added', () => {
        const chvCollection: IChv[] = [sampleWithRequiredData];
        expectedResult = service.addChvToCollectionIfMissing(chvCollection, undefined, null);
        expect(expectedResult).toEqual(chvCollection);
      });
    });

    describe('compareChv', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareChv(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareChv(entity1, entity2);
        const compareResult2 = service.compareChv(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareChv(entity1, entity2);
        const compareResult2 = service.compareChv(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareChv(entity1, entity2);
        const compareResult2 = service.compareChv(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
