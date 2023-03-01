import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMetadataVersion } from '../metadata-version.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../metadata-version.test-samples';

import { MetadataVersionService, RestMetadataVersion } from './metadata-version.service';

const requireRestSample: RestMetadataVersion = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
  importDate: sampleWithRequiredData.importDate?.toJSON(),
};

describe('MetadataVersion Service', () => {
  let service: MetadataVersionService;
  let httpMock: HttpTestingController;
  let expectedResult: IMetadataVersion | IMetadataVersion[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MetadataVersionService);
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

    it('should create a MetadataVersion', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const metadataVersion = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(metadataVersion).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MetadataVersion', () => {
      const metadataVersion = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(metadataVersion).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MetadataVersion', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MetadataVersion', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MetadataVersion', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMetadataVersionToCollectionIfMissing', () => {
      it('should add a MetadataVersion to an empty array', () => {
        const metadataVersion: IMetadataVersion = sampleWithRequiredData;
        expectedResult = service.addMetadataVersionToCollectionIfMissing([], metadataVersion);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(metadataVersion);
      });

      it('should not add a MetadataVersion to an array that contains it', () => {
        const metadataVersion: IMetadataVersion = sampleWithRequiredData;
        const metadataVersionCollection: IMetadataVersion[] = [
          {
            ...metadataVersion,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMetadataVersionToCollectionIfMissing(metadataVersionCollection, metadataVersion);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MetadataVersion to an array that doesn't contain it", () => {
        const metadataVersion: IMetadataVersion = sampleWithRequiredData;
        const metadataVersionCollection: IMetadataVersion[] = [sampleWithPartialData];
        expectedResult = service.addMetadataVersionToCollectionIfMissing(metadataVersionCollection, metadataVersion);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(metadataVersion);
      });

      it('should add only unique MetadataVersion to an array', () => {
        const metadataVersionArray: IMetadataVersion[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const metadataVersionCollection: IMetadataVersion[] = [sampleWithRequiredData];
        expectedResult = service.addMetadataVersionToCollectionIfMissing(metadataVersionCollection, ...metadataVersionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const metadataVersion: IMetadataVersion = sampleWithRequiredData;
        const metadataVersion2: IMetadataVersion = sampleWithPartialData;
        expectedResult = service.addMetadataVersionToCollectionIfMissing([], metadataVersion, metadataVersion2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(metadataVersion);
        expect(expectedResult).toContain(metadataVersion2);
      });

      it('should accept null and undefined values', () => {
        const metadataVersion: IMetadataVersion = sampleWithRequiredData;
        expectedResult = service.addMetadataVersionToCollectionIfMissing([], null, metadataVersion, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(metadataVersion);
      });

      it('should return initial array if no MetadataVersion is added', () => {
        const metadataVersionCollection: IMetadataVersion[] = [sampleWithRequiredData];
        expectedResult = service.addMetadataVersionToCollectionIfMissing(metadataVersionCollection, undefined, null);
        expect(expectedResult).toEqual(metadataVersionCollection);
      });
    });

    describe('compareMetadataVersion', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMetadataVersion(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMetadataVersion(entity1, entity2);
        const compareResult2 = service.compareMetadataVersion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMetadataVersion(entity1, entity2);
        const compareResult2 = service.compareMetadataVersion(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMetadataVersion(entity1, entity2);
        const compareResult2 = service.compareMetadataVersion(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
