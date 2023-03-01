import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProgramInstance } from '../program-instance.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../program-instance.test-samples';

import { ProgramInstanceService, RestProgramInstance } from './program-instance.service';

const requireRestSample: RestProgramInstance = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
  createdAtClient: sampleWithRequiredData.createdAtClient?.toJSON(),
  updatedAtClient: sampleWithRequiredData.updatedAtClient?.toJSON(),
  lastSynchronized: sampleWithRequiredData.lastSynchronized?.toJSON(),
  incidentDate: sampleWithRequiredData.incidentDate?.toJSON(),
  enrollmentDate: sampleWithRequiredData.enrollmentDate?.toJSON(),
  endDate: sampleWithRequiredData.endDate?.toJSON(),
  completedDate: sampleWithRequiredData.completedDate?.toJSON(),
  deletedAt: sampleWithRequiredData.deletedAt?.toJSON(),
};

describe('ProgramInstance Service', () => {
  let service: ProgramInstanceService;
  let httpMock: HttpTestingController;
  let expectedResult: IProgramInstance | IProgramInstance[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProgramInstanceService);
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

    it('should create a ProgramInstance', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const programInstance = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(programInstance).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProgramInstance', () => {
      const programInstance = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(programInstance).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProgramInstance', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProgramInstance', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProgramInstance', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProgramInstanceToCollectionIfMissing', () => {
      it('should add a ProgramInstance to an empty array', () => {
        const programInstance: IProgramInstance = sampleWithRequiredData;
        expectedResult = service.addProgramInstanceToCollectionIfMissing([], programInstance);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programInstance);
      });

      it('should not add a ProgramInstance to an array that contains it', () => {
        const programInstance: IProgramInstance = sampleWithRequiredData;
        const programInstanceCollection: IProgramInstance[] = [
          {
            ...programInstance,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProgramInstanceToCollectionIfMissing(programInstanceCollection, programInstance);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProgramInstance to an array that doesn't contain it", () => {
        const programInstance: IProgramInstance = sampleWithRequiredData;
        const programInstanceCollection: IProgramInstance[] = [sampleWithPartialData];
        expectedResult = service.addProgramInstanceToCollectionIfMissing(programInstanceCollection, programInstance);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programInstance);
      });

      it('should add only unique ProgramInstance to an array', () => {
        const programInstanceArray: IProgramInstance[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const programInstanceCollection: IProgramInstance[] = [sampleWithRequiredData];
        expectedResult = service.addProgramInstanceToCollectionIfMissing(programInstanceCollection, ...programInstanceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const programInstance: IProgramInstance = sampleWithRequiredData;
        const programInstance2: IProgramInstance = sampleWithPartialData;
        expectedResult = service.addProgramInstanceToCollectionIfMissing([], programInstance, programInstance2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programInstance);
        expect(expectedResult).toContain(programInstance2);
      });

      it('should accept null and undefined values', () => {
        const programInstance: IProgramInstance = sampleWithRequiredData;
        expectedResult = service.addProgramInstanceToCollectionIfMissing([], null, programInstance, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programInstance);
      });

      it('should return initial array if no ProgramInstance is added', () => {
        const programInstanceCollection: IProgramInstance[] = [sampleWithRequiredData];
        expectedResult = service.addProgramInstanceToCollectionIfMissing(programInstanceCollection, undefined, null);
        expect(expectedResult).toEqual(programInstanceCollection);
      });
    });

    describe('compareProgramInstance', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProgramInstance(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProgramInstance(entity1, entity2);
        const compareResult2 = service.compareProgramInstance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProgramInstance(entity1, entity2);
        const compareResult2 = service.compareProgramInstance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProgramInstance(entity1, entity2);
        const compareResult2 = service.compareProgramInstance(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
