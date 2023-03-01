import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISystemSetting } from '../system-setting.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../system-setting.test-samples';

import { SystemSettingService } from './system-setting.service';

const requireRestSample: ISystemSetting = {
  ...sampleWithRequiredData,
};

describe('SystemSetting Service', () => {
  let service: SystemSettingService;
  let httpMock: HttpTestingController;
  let expectedResult: ISystemSetting | ISystemSetting[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SystemSettingService);
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

    it('should create a SystemSetting', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const systemSetting = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(systemSetting).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SystemSetting', () => {
      const systemSetting = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(systemSetting).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SystemSetting', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SystemSetting', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SystemSetting', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSystemSettingToCollectionIfMissing', () => {
      it('should add a SystemSetting to an empty array', () => {
        const systemSetting: ISystemSetting = sampleWithRequiredData;
        expectedResult = service.addSystemSettingToCollectionIfMissing([], systemSetting);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(systemSetting);
      });

      it('should not add a SystemSetting to an array that contains it', () => {
        const systemSetting: ISystemSetting = sampleWithRequiredData;
        const systemSettingCollection: ISystemSetting[] = [
          {
            ...systemSetting,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSystemSettingToCollectionIfMissing(systemSettingCollection, systemSetting);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SystemSetting to an array that doesn't contain it", () => {
        const systemSetting: ISystemSetting = sampleWithRequiredData;
        const systemSettingCollection: ISystemSetting[] = [sampleWithPartialData];
        expectedResult = service.addSystemSettingToCollectionIfMissing(systemSettingCollection, systemSetting);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(systemSetting);
      });

      it('should add only unique SystemSetting to an array', () => {
        const systemSettingArray: ISystemSetting[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const systemSettingCollection: ISystemSetting[] = [sampleWithRequiredData];
        expectedResult = service.addSystemSettingToCollectionIfMissing(systemSettingCollection, ...systemSettingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const systemSetting: ISystemSetting = sampleWithRequiredData;
        const systemSetting2: ISystemSetting = sampleWithPartialData;
        expectedResult = service.addSystemSettingToCollectionIfMissing([], systemSetting, systemSetting2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(systemSetting);
        expect(expectedResult).toContain(systemSetting2);
      });

      it('should accept null and undefined values', () => {
        const systemSetting: ISystemSetting = sampleWithRequiredData;
        expectedResult = service.addSystemSettingToCollectionIfMissing([], null, systemSetting, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(systemSetting);
      });

      it('should return initial array if no SystemSetting is added', () => {
        const systemSettingCollection: ISystemSetting[] = [sampleWithRequiredData];
        expectedResult = service.addSystemSettingToCollectionIfMissing(systemSettingCollection, undefined, null);
        expect(expectedResult).toEqual(systemSettingCollection);
      });
    });

    describe('compareSystemSetting', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSystemSetting(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSystemSetting(entity1, entity2);
        const compareResult2 = service.compareSystemSetting(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSystemSetting(entity1, entity2);
        const compareResult2 = service.compareSystemSetting(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSystemSetting(entity1, entity2);
        const compareResult2 = service.compareSystemSetting(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
