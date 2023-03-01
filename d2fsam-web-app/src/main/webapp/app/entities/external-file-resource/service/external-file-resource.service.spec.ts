import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IExternalFileResource } from '../external-file-resource.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../external-file-resource.test-samples';

import { ExternalFileResourceService, RestExternalFileResource } from './external-file-resource.service';

const requireRestSample: RestExternalFileResource = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
  expires: sampleWithRequiredData.expires?.toJSON(),
};

describe('ExternalFileResource Service', () => {
  let service: ExternalFileResourceService;
  let httpMock: HttpTestingController;
  let expectedResult: IExternalFileResource | IExternalFileResource[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ExternalFileResourceService);
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

    it('should create a ExternalFileResource', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const externalFileResource = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(externalFileResource).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ExternalFileResource', () => {
      const externalFileResource = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(externalFileResource).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ExternalFileResource', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ExternalFileResource', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ExternalFileResource', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addExternalFileResourceToCollectionIfMissing', () => {
      it('should add a ExternalFileResource to an empty array', () => {
        const externalFileResource: IExternalFileResource = sampleWithRequiredData;
        expectedResult = service.addExternalFileResourceToCollectionIfMissing([], externalFileResource);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(externalFileResource);
      });

      it('should not add a ExternalFileResource to an array that contains it', () => {
        const externalFileResource: IExternalFileResource = sampleWithRequiredData;
        const externalFileResourceCollection: IExternalFileResource[] = [
          {
            ...externalFileResource,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addExternalFileResourceToCollectionIfMissing(externalFileResourceCollection, externalFileResource);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ExternalFileResource to an array that doesn't contain it", () => {
        const externalFileResource: IExternalFileResource = sampleWithRequiredData;
        const externalFileResourceCollection: IExternalFileResource[] = [sampleWithPartialData];
        expectedResult = service.addExternalFileResourceToCollectionIfMissing(externalFileResourceCollection, externalFileResource);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(externalFileResource);
      });

      it('should add only unique ExternalFileResource to an array', () => {
        const externalFileResourceArray: IExternalFileResource[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const externalFileResourceCollection: IExternalFileResource[] = [sampleWithRequiredData];
        expectedResult = service.addExternalFileResourceToCollectionIfMissing(externalFileResourceCollection, ...externalFileResourceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const externalFileResource: IExternalFileResource = sampleWithRequiredData;
        const externalFileResource2: IExternalFileResource = sampleWithPartialData;
        expectedResult = service.addExternalFileResourceToCollectionIfMissing([], externalFileResource, externalFileResource2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(externalFileResource);
        expect(expectedResult).toContain(externalFileResource2);
      });

      it('should accept null and undefined values', () => {
        const externalFileResource: IExternalFileResource = sampleWithRequiredData;
        expectedResult = service.addExternalFileResourceToCollectionIfMissing([], null, externalFileResource, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(externalFileResource);
      });

      it('should return initial array if no ExternalFileResource is added', () => {
        const externalFileResourceCollection: IExternalFileResource[] = [sampleWithRequiredData];
        expectedResult = service.addExternalFileResourceToCollectionIfMissing(externalFileResourceCollection, undefined, null);
        expect(expectedResult).toEqual(externalFileResourceCollection);
      });
    });

    describe('compareExternalFileResource', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareExternalFileResource(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareExternalFileResource(entity1, entity2);
        const compareResult2 = service.compareExternalFileResource(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareExternalFileResource(entity1, entity2);
        const compareResult2 = service.compareExternalFileResource(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareExternalFileResource(entity1, entity2);
        const compareResult2 = service.compareExternalFileResource(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
