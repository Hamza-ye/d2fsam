import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProgramTrackedEntityAttribute } from '../program-tracked-entity-attribute.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../program-tracked-entity-attribute.test-samples';

import { ProgramTrackedEntityAttributeService, RestProgramTrackedEntityAttribute } from './program-tracked-entity-attribute.service';

const requireRestSample: RestProgramTrackedEntityAttribute = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('ProgramTrackedEntityAttribute Service', () => {
  let service: ProgramTrackedEntityAttributeService;
  let httpMock: HttpTestingController;
  let expectedResult: IProgramTrackedEntityAttribute | IProgramTrackedEntityAttribute[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProgramTrackedEntityAttributeService);
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

    it('should create a ProgramTrackedEntityAttribute', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const programTrackedEntityAttribute = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(programTrackedEntityAttribute).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProgramTrackedEntityAttribute', () => {
      const programTrackedEntityAttribute = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(programTrackedEntityAttribute).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProgramTrackedEntityAttribute', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProgramTrackedEntityAttribute', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProgramTrackedEntityAttribute', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProgramTrackedEntityAttributeToCollectionIfMissing', () => {
      it('should add a ProgramTrackedEntityAttribute to an empty array', () => {
        const programTrackedEntityAttribute: IProgramTrackedEntityAttribute = sampleWithRequiredData;
        expectedResult = service.addProgramTrackedEntityAttributeToCollectionIfMissing([], programTrackedEntityAttribute);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programTrackedEntityAttribute);
      });

      it('should not add a ProgramTrackedEntityAttribute to an array that contains it', () => {
        const programTrackedEntityAttribute: IProgramTrackedEntityAttribute = sampleWithRequiredData;
        const programTrackedEntityAttributeCollection: IProgramTrackedEntityAttribute[] = [
          {
            ...programTrackedEntityAttribute,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProgramTrackedEntityAttributeToCollectionIfMissing(
          programTrackedEntityAttributeCollection,
          programTrackedEntityAttribute
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProgramTrackedEntityAttribute to an array that doesn't contain it", () => {
        const programTrackedEntityAttribute: IProgramTrackedEntityAttribute = sampleWithRequiredData;
        const programTrackedEntityAttributeCollection: IProgramTrackedEntityAttribute[] = [sampleWithPartialData];
        expectedResult = service.addProgramTrackedEntityAttributeToCollectionIfMissing(
          programTrackedEntityAttributeCollection,
          programTrackedEntityAttribute
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programTrackedEntityAttribute);
      });

      it('should add only unique ProgramTrackedEntityAttribute to an array', () => {
        const programTrackedEntityAttributeArray: IProgramTrackedEntityAttribute[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const programTrackedEntityAttributeCollection: IProgramTrackedEntityAttribute[] = [sampleWithRequiredData];
        expectedResult = service.addProgramTrackedEntityAttributeToCollectionIfMissing(
          programTrackedEntityAttributeCollection,
          ...programTrackedEntityAttributeArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const programTrackedEntityAttribute: IProgramTrackedEntityAttribute = sampleWithRequiredData;
        const programTrackedEntityAttribute2: IProgramTrackedEntityAttribute = sampleWithPartialData;
        expectedResult = service.addProgramTrackedEntityAttributeToCollectionIfMissing(
          [],
          programTrackedEntityAttribute,
          programTrackedEntityAttribute2
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programTrackedEntityAttribute);
        expect(expectedResult).toContain(programTrackedEntityAttribute2);
      });

      it('should accept null and undefined values', () => {
        const programTrackedEntityAttribute: IProgramTrackedEntityAttribute = sampleWithRequiredData;
        expectedResult = service.addProgramTrackedEntityAttributeToCollectionIfMissing([], null, programTrackedEntityAttribute, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programTrackedEntityAttribute);
      });

      it('should return initial array if no ProgramTrackedEntityAttribute is added', () => {
        const programTrackedEntityAttributeCollection: IProgramTrackedEntityAttribute[] = [sampleWithRequiredData];
        expectedResult = service.addProgramTrackedEntityAttributeToCollectionIfMissing(
          programTrackedEntityAttributeCollection,
          undefined,
          null
        );
        expect(expectedResult).toEqual(programTrackedEntityAttributeCollection);
      });
    });

    describe('compareProgramTrackedEntityAttribute', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProgramTrackedEntityAttribute(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProgramTrackedEntityAttribute(entity1, entity2);
        const compareResult2 = service.compareProgramTrackedEntityAttribute(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProgramTrackedEntityAttribute(entity1, entity2);
        const compareResult2 = service.compareProgramTrackedEntityAttribute(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProgramTrackedEntityAttribute(entity1, entity2);
        const compareResult2 = service.compareProgramTrackedEntityAttribute(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
