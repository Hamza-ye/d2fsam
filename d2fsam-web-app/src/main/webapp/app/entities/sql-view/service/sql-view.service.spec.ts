import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISqlView } from '../sql-view.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../sql-view.test-samples';

import { SqlViewService, RestSqlView } from './sql-view.service';

const requireRestSample: RestSqlView = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('SqlView Service', () => {
  let service: SqlViewService;
  let httpMock: HttpTestingController;
  let expectedResult: ISqlView | ISqlView[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SqlViewService);
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

    it('should create a SqlView', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const sqlView = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(sqlView).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SqlView', () => {
      const sqlView = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(sqlView).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SqlView', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SqlView', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SqlView', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSqlViewToCollectionIfMissing', () => {
      it('should add a SqlView to an empty array', () => {
        const sqlView: ISqlView = sampleWithRequiredData;
        expectedResult = service.addSqlViewToCollectionIfMissing([], sqlView);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sqlView);
      });

      it('should not add a SqlView to an array that contains it', () => {
        const sqlView: ISqlView = sampleWithRequiredData;
        const sqlViewCollection: ISqlView[] = [
          {
            ...sqlView,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSqlViewToCollectionIfMissing(sqlViewCollection, sqlView);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SqlView to an array that doesn't contain it", () => {
        const sqlView: ISqlView = sampleWithRequiredData;
        const sqlViewCollection: ISqlView[] = [sampleWithPartialData];
        expectedResult = service.addSqlViewToCollectionIfMissing(sqlViewCollection, sqlView);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sqlView);
      });

      it('should add only unique SqlView to an array', () => {
        const sqlViewArray: ISqlView[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const sqlViewCollection: ISqlView[] = [sampleWithRequiredData];
        expectedResult = service.addSqlViewToCollectionIfMissing(sqlViewCollection, ...sqlViewArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const sqlView: ISqlView = sampleWithRequiredData;
        const sqlView2: ISqlView = sampleWithPartialData;
        expectedResult = service.addSqlViewToCollectionIfMissing([], sqlView, sqlView2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(sqlView);
        expect(expectedResult).toContain(sqlView2);
      });

      it('should accept null and undefined values', () => {
        const sqlView: ISqlView = sampleWithRequiredData;
        expectedResult = service.addSqlViewToCollectionIfMissing([], null, sqlView, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(sqlView);
      });

      it('should return initial array if no SqlView is added', () => {
        const sqlViewCollection: ISqlView[] = [sampleWithRequiredData];
        expectedResult = service.addSqlViewToCollectionIfMissing(sqlViewCollection, undefined, null);
        expect(expectedResult).toEqual(sqlViewCollection);
      });
    });

    describe('compareSqlView', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSqlView(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSqlView(entity1, entity2);
        const compareResult2 = service.compareSqlView(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSqlView(entity1, entity2);
        const compareResult2 = service.compareSqlView(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSqlView(entity1, entity2);
        const compareResult2 = service.compareSqlView(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
