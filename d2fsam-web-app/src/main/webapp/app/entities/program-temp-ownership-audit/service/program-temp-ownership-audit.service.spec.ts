import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProgramTempOwnershipAudit } from '../program-temp-ownership-audit.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../program-temp-ownership-audit.test-samples';

import { ProgramTempOwnershipAuditService, RestProgramTempOwnershipAudit } from './program-temp-ownership-audit.service';

const requireRestSample: RestProgramTempOwnershipAudit = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
};

describe('ProgramTempOwnershipAudit Service', () => {
  let service: ProgramTempOwnershipAuditService;
  let httpMock: HttpTestingController;
  let expectedResult: IProgramTempOwnershipAudit | IProgramTempOwnershipAudit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProgramTempOwnershipAuditService);
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

    it('should create a ProgramTempOwnershipAudit', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const programTempOwnershipAudit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(programTempOwnershipAudit).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProgramTempOwnershipAudit', () => {
      const programTempOwnershipAudit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(programTempOwnershipAudit).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProgramTempOwnershipAudit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProgramTempOwnershipAudit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProgramTempOwnershipAudit', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProgramTempOwnershipAuditToCollectionIfMissing', () => {
      it('should add a ProgramTempOwnershipAudit to an empty array', () => {
        const programTempOwnershipAudit: IProgramTempOwnershipAudit = sampleWithRequiredData;
        expectedResult = service.addProgramTempOwnershipAuditToCollectionIfMissing([], programTempOwnershipAudit);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programTempOwnershipAudit);
      });

      it('should not add a ProgramTempOwnershipAudit to an array that contains it', () => {
        const programTempOwnershipAudit: IProgramTempOwnershipAudit = sampleWithRequiredData;
        const programTempOwnershipAuditCollection: IProgramTempOwnershipAudit[] = [
          {
            ...programTempOwnershipAudit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProgramTempOwnershipAuditToCollectionIfMissing(
          programTempOwnershipAuditCollection,
          programTempOwnershipAudit
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProgramTempOwnershipAudit to an array that doesn't contain it", () => {
        const programTempOwnershipAudit: IProgramTempOwnershipAudit = sampleWithRequiredData;
        const programTempOwnershipAuditCollection: IProgramTempOwnershipAudit[] = [sampleWithPartialData];
        expectedResult = service.addProgramTempOwnershipAuditToCollectionIfMissing(
          programTempOwnershipAuditCollection,
          programTempOwnershipAudit
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programTempOwnershipAudit);
      });

      it('should add only unique ProgramTempOwnershipAudit to an array', () => {
        const programTempOwnershipAuditArray: IProgramTempOwnershipAudit[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const programTempOwnershipAuditCollection: IProgramTempOwnershipAudit[] = [sampleWithRequiredData];
        expectedResult = service.addProgramTempOwnershipAuditToCollectionIfMissing(
          programTempOwnershipAuditCollection,
          ...programTempOwnershipAuditArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const programTempOwnershipAudit: IProgramTempOwnershipAudit = sampleWithRequiredData;
        const programTempOwnershipAudit2: IProgramTempOwnershipAudit = sampleWithPartialData;
        expectedResult = service.addProgramTempOwnershipAuditToCollectionIfMissing(
          [],
          programTempOwnershipAudit,
          programTempOwnershipAudit2
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programTempOwnershipAudit);
        expect(expectedResult).toContain(programTempOwnershipAudit2);
      });

      it('should accept null and undefined values', () => {
        const programTempOwnershipAudit: IProgramTempOwnershipAudit = sampleWithRequiredData;
        expectedResult = service.addProgramTempOwnershipAuditToCollectionIfMissing([], null, programTempOwnershipAudit, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programTempOwnershipAudit);
      });

      it('should return initial array if no ProgramTempOwnershipAudit is added', () => {
        const programTempOwnershipAuditCollection: IProgramTempOwnershipAudit[] = [sampleWithRequiredData];
        expectedResult = service.addProgramTempOwnershipAuditToCollectionIfMissing(programTempOwnershipAuditCollection, undefined, null);
        expect(expectedResult).toEqual(programTempOwnershipAuditCollection);
      });
    });

    describe('compareProgramTempOwnershipAudit', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProgramTempOwnershipAudit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProgramTempOwnershipAudit(entity1, entity2);
        const compareResult2 = service.compareProgramTempOwnershipAudit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProgramTempOwnershipAudit(entity1, entity2);
        const compareResult2 = service.compareProgramTempOwnershipAudit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProgramTempOwnershipAudit(entity1, entity2);
        const compareResult2 = service.compareProgramTempOwnershipAudit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
