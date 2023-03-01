import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDatastoreEntry } from '../datastore-entry.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../datastore-entry.test-samples';

import { DatastoreEntryService, RestDatastoreEntry } from './datastore-entry.service';

const requireRestSample: RestDatastoreEntry = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('DatastoreEntry Service', () => {
  let service: DatastoreEntryService;
  let httpMock: HttpTestingController;
  let expectedResult: IDatastoreEntry | IDatastoreEntry[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DatastoreEntryService);
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

    it('should create a DatastoreEntry', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const datastoreEntry = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(datastoreEntry).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DatastoreEntry', () => {
      const datastoreEntry = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(datastoreEntry).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DatastoreEntry', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DatastoreEntry', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DatastoreEntry', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDatastoreEntryToCollectionIfMissing', () => {
      it('should add a DatastoreEntry to an empty array', () => {
        const datastoreEntry: IDatastoreEntry = sampleWithRequiredData;
        expectedResult = service.addDatastoreEntryToCollectionIfMissing([], datastoreEntry);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(datastoreEntry);
      });

      it('should not add a DatastoreEntry to an array that contains it', () => {
        const datastoreEntry: IDatastoreEntry = sampleWithRequiredData;
        const datastoreEntryCollection: IDatastoreEntry[] = [
          {
            ...datastoreEntry,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDatastoreEntryToCollectionIfMissing(datastoreEntryCollection, datastoreEntry);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DatastoreEntry to an array that doesn't contain it", () => {
        const datastoreEntry: IDatastoreEntry = sampleWithRequiredData;
        const datastoreEntryCollection: IDatastoreEntry[] = [sampleWithPartialData];
        expectedResult = service.addDatastoreEntryToCollectionIfMissing(datastoreEntryCollection, datastoreEntry);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(datastoreEntry);
      });

      it('should add only unique DatastoreEntry to an array', () => {
        const datastoreEntryArray: IDatastoreEntry[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const datastoreEntryCollection: IDatastoreEntry[] = [sampleWithRequiredData];
        expectedResult = service.addDatastoreEntryToCollectionIfMissing(datastoreEntryCollection, ...datastoreEntryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const datastoreEntry: IDatastoreEntry = sampleWithRequiredData;
        const datastoreEntry2: IDatastoreEntry = sampleWithPartialData;
        expectedResult = service.addDatastoreEntryToCollectionIfMissing([], datastoreEntry, datastoreEntry2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(datastoreEntry);
        expect(expectedResult).toContain(datastoreEntry2);
      });

      it('should accept null and undefined values', () => {
        const datastoreEntry: IDatastoreEntry = sampleWithRequiredData;
        expectedResult = service.addDatastoreEntryToCollectionIfMissing([], null, datastoreEntry, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(datastoreEntry);
      });

      it('should return initial array if no DatastoreEntry is added', () => {
        const datastoreEntryCollection: IDatastoreEntry[] = [sampleWithRequiredData];
        expectedResult = service.addDatastoreEntryToCollectionIfMissing(datastoreEntryCollection, undefined, null);
        expect(expectedResult).toEqual(datastoreEntryCollection);
      });
    });

    describe('compareDatastoreEntry', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDatastoreEntry(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDatastoreEntry(entity1, entity2);
        const compareResult2 = service.compareDatastoreEntry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDatastoreEntry(entity1, entity2);
        const compareResult2 = service.compareDatastoreEntry(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDatastoreEntry(entity1, entity2);
        const compareResult2 = service.compareDatastoreEntry(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
