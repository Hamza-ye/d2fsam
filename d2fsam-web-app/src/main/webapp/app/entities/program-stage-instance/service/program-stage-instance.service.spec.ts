import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProgramStageInstance } from '../program-stage-instance.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../program-stage-instance.test-samples';

import { ProgramStageInstanceService, RestProgramStageInstance } from './program-stage-instance.service';

const requireRestSample: RestProgramStageInstance = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
  createdAtClient: sampleWithRequiredData.createdAtClient?.toJSON(),
  updatedAtClient: sampleWithRequiredData.updatedAtClient?.toJSON(),
  lastSynchronized: sampleWithRequiredData.lastSynchronized?.toJSON(),
  dueDate: sampleWithRequiredData.dueDate?.toJSON(),
  executionDate: sampleWithRequiredData.executionDate?.toJSON(),
  completedDate: sampleWithRequiredData.completedDate?.toJSON(),
  deletedAt: sampleWithRequiredData.deletedAt?.toJSON(),
};

describe('ProgramStageInstance Service', () => {
  let service: ProgramStageInstanceService;
  let httpMock: HttpTestingController;
  let expectedResult: IProgramStageInstance | IProgramStageInstance[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProgramStageInstanceService);
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

    it('should create a ProgramStageInstance', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const programStageInstance = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(programStageInstance).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProgramStageInstance', () => {
      const programStageInstance = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(programStageInstance).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProgramStageInstance', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProgramStageInstance', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProgramStageInstance', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProgramStageInstanceToCollectionIfMissing', () => {
      it('should add a ProgramStageInstance to an empty array', () => {
        const programStageInstance: IProgramStageInstance = sampleWithRequiredData;
        expectedResult = service.addProgramStageInstanceToCollectionIfMissing([], programStageInstance);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programStageInstance);
      });

      it('should not add a ProgramStageInstance to an array that contains it', () => {
        const programStageInstance: IProgramStageInstance = sampleWithRequiredData;
        const programStageInstanceCollection: IProgramStageInstance[] = [
          {
            ...programStageInstance,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProgramStageInstanceToCollectionIfMissing(programStageInstanceCollection, programStageInstance);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProgramStageInstance to an array that doesn't contain it", () => {
        const programStageInstance: IProgramStageInstance = sampleWithRequiredData;
        const programStageInstanceCollection: IProgramStageInstance[] = [sampleWithPartialData];
        expectedResult = service.addProgramStageInstanceToCollectionIfMissing(programStageInstanceCollection, programStageInstance);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programStageInstance);
      });

      it('should add only unique ProgramStageInstance to an array', () => {
        const programStageInstanceArray: IProgramStageInstance[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const programStageInstanceCollection: IProgramStageInstance[] = [sampleWithRequiredData];
        expectedResult = service.addProgramStageInstanceToCollectionIfMissing(programStageInstanceCollection, ...programStageInstanceArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const programStageInstance: IProgramStageInstance = sampleWithRequiredData;
        const programStageInstance2: IProgramStageInstance = sampleWithPartialData;
        expectedResult = service.addProgramStageInstanceToCollectionIfMissing([], programStageInstance, programStageInstance2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(programStageInstance);
        expect(expectedResult).toContain(programStageInstance2);
      });

      it('should accept null and undefined values', () => {
        const programStageInstance: IProgramStageInstance = sampleWithRequiredData;
        expectedResult = service.addProgramStageInstanceToCollectionIfMissing([], null, programStageInstance, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(programStageInstance);
      });

      it('should return initial array if no ProgramStageInstance is added', () => {
        const programStageInstanceCollection: IProgramStageInstance[] = [sampleWithRequiredData];
        expectedResult = service.addProgramStageInstanceToCollectionIfMissing(programStageInstanceCollection, undefined, null);
        expect(expectedResult).toEqual(programStageInstanceCollection);
      });
    });

    describe('compareProgramStageInstance', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProgramStageInstance(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProgramStageInstance(entity1, entity2);
        const compareResult2 = service.compareProgramStageInstance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProgramStageInstance(entity1, entity2);
        const compareResult2 = service.compareProgramStageInstance(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProgramStageInstance(entity1, entity2);
        const compareResult2 = service.compareProgramStageInstance(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
