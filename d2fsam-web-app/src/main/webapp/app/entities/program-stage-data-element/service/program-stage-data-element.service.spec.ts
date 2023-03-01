import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProgramStageDataElement } from '../program-stage-data-element.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../program-stage-data-element.test-samples';

import { ProgramStageDataElementService, RestProgramStageDataElement } from './program-stage-data-element.service';

const requireRestSample: RestProgramStageDataElement = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('ProgramStageDataElement Service', () => {
  let service: ProgramStageDataElementService;
  let httpMock: HttpTestingController;
  let expectedResult: IProgramStageDataElement | IProgramStageDataElement[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProgramStageDataElementService);
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

    it('should create a ProgramStageDataElement', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const programStageDataElement = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(programStageDataElement).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProgramStageDataElement', () => {
      const programStageDataElement = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(programStageDataElement).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProgramStageDataElement', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProgramStageDataElement', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProgramStageDataElement', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProgramStageDataElementToCollectionIfMissing', () => {
      it('should add a ProgramStageDataElement to an empty array', () => {
        const programStageDataElement: IProgramStageDataElement = sampleWithRequiredData;
        expectedResult = service.addProgramStageDataElementToCollectionIfMissing([], programStageDataElement);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programStageDataElement);
      });

      it('should not add a ProgramStageDataElement to an array that contains it', () => {
        const programStageDataElement: IProgramStageDataElement = sampleWithRequiredData;
        const programStageDataElementCollection: IProgramStageDataElement[] = [
          {
            ...programStageDataElement,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProgramStageDataElementToCollectionIfMissing(
          programStageDataElementCollection,
          programStageDataElement
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProgramStageDataElement to an array that doesn't contain it", () => {
        const programStageDataElement: IProgramStageDataElement = sampleWithRequiredData;
        const programStageDataElementCollection: IProgramStageDataElement[] = [sampleWithPartialData];
        expectedResult = service.addProgramStageDataElementToCollectionIfMissing(
          programStageDataElementCollection,
          programStageDataElement
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programStageDataElement);
      });

      it('should add only unique ProgramStageDataElement to an array', () => {
        const programStageDataElementArray: IProgramStageDataElement[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const programStageDataElementCollection: IProgramStageDataElement[] = [sampleWithRequiredData];
        expectedResult = service.addProgramStageDataElementToCollectionIfMissing(
          programStageDataElementCollection,
          ...programStageDataElementArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const programStageDataElement: IProgramStageDataElement = sampleWithRequiredData;
        const programStageDataElement2: IProgramStageDataElement = sampleWithPartialData;
        expectedResult = service.addProgramStageDataElementToCollectionIfMissing([], programStageDataElement, programStageDataElement2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programStageDataElement);
        expect(expectedResult).toContain(programStageDataElement2);
      });

      it('should accept null and undefined values', () => {
        const programStageDataElement: IProgramStageDataElement = sampleWithRequiredData;
        expectedResult = service.addProgramStageDataElementToCollectionIfMissing([], null, programStageDataElement, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programStageDataElement);
      });

      it('should return initial array if no ProgramStageDataElement is added', () => {
        const programStageDataElementCollection: IProgramStageDataElement[] = [sampleWithRequiredData];
        expectedResult = service.addProgramStageDataElementToCollectionIfMissing(programStageDataElementCollection, undefined, null);
        expect(expectedResult).toEqual(programStageDataElementCollection);
      });
    });

    describe('compareProgramStageDataElement', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProgramStageDataElement(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProgramStageDataElement(entity1, entity2);
        const compareResult2 = service.compareProgramStageDataElement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProgramStageDataElement(entity1, entity2);
        const compareResult2 = service.compareProgramStageDataElement(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProgramStageDataElement(entity1, entity2);
        const compareResult2 = service.compareProgramStageDataElement(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
