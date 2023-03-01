import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProgramTempOwner } from '../program-temp-owner.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../program-temp-owner.test-samples';

import { ProgramTempOwnerService, RestProgramTempOwner } from './program-temp-owner.service';

const requireRestSample: RestProgramTempOwner = {
  ...sampleWithRequiredData,
  validTill: sampleWithRequiredData.validTill?.toJSON(),
};

describe('ProgramTempOwner Service', () => {
  let service: ProgramTempOwnerService;
  let httpMock: HttpTestingController;
  let expectedResult: IProgramTempOwner | IProgramTempOwner[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProgramTempOwnerService);
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

    it('should create a ProgramTempOwner', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const programTempOwner = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(programTempOwner).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProgramTempOwner', () => {
      const programTempOwner = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(programTempOwner).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProgramTempOwner', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProgramTempOwner', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProgramTempOwner', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProgramTempOwnerToCollectionIfMissing', () => {
      it('should add a ProgramTempOwner to an empty array', () => {
        const programTempOwner: IProgramTempOwner = sampleWithRequiredData;
        expectedResult = service.addProgramTempOwnerToCollectionIfMissing([], programTempOwner);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programTempOwner);
      });

      it('should not add a ProgramTempOwner to an array that contains it', () => {
        const programTempOwner: IProgramTempOwner = sampleWithRequiredData;
        const programTempOwnerCollection: IProgramTempOwner[] = [
          {
            ...programTempOwner,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProgramTempOwnerToCollectionIfMissing(programTempOwnerCollection, programTempOwner);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProgramTempOwner to an array that doesn't contain it", () => {
        const programTempOwner: IProgramTempOwner = sampleWithRequiredData;
        const programTempOwnerCollection: IProgramTempOwner[] = [sampleWithPartialData];
        expectedResult = service.addProgramTempOwnerToCollectionIfMissing(programTempOwnerCollection, programTempOwner);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programTempOwner);
      });

      it('should add only unique ProgramTempOwner to an array', () => {
        const programTempOwnerArray: IProgramTempOwner[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const programTempOwnerCollection: IProgramTempOwner[] = [sampleWithRequiredData];
        expectedResult = service.addProgramTempOwnerToCollectionIfMissing(programTempOwnerCollection, ...programTempOwnerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const programTempOwner: IProgramTempOwner = sampleWithRequiredData;
        const programTempOwner2: IProgramTempOwner = sampleWithPartialData;
        expectedResult = service.addProgramTempOwnerToCollectionIfMissing([], programTempOwner, programTempOwner2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programTempOwner);
        expect(expectedResult).toContain(programTempOwner2);
      });

      it('should accept null and undefined values', () => {
        const programTempOwner: IProgramTempOwner = sampleWithRequiredData;
        expectedResult = service.addProgramTempOwnerToCollectionIfMissing([], null, programTempOwner, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programTempOwner);
      });

      it('should return initial array if no ProgramTempOwner is added', () => {
        const programTempOwnerCollection: IProgramTempOwner[] = [sampleWithRequiredData];
        expectedResult = service.addProgramTempOwnerToCollectionIfMissing(programTempOwnerCollection, undefined, null);
        expect(expectedResult).toEqual(programTempOwnerCollection);
      });
    });

    describe('compareProgramTempOwner', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProgramTempOwner(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProgramTempOwner(entity1, entity2);
        const compareResult2 = service.compareProgramTempOwner(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProgramTempOwner(entity1, entity2);
        const compareResult2 = service.compareProgramTempOwner(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProgramTempOwner(entity1, entity2);
        const compareResult2 = service.compareProgramTempOwner(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
