import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../system-setting.test-samples';

import { SystemSettingFormService } from './system-setting-form.service';

describe('SystemSetting Form Service', () => {
  let service: SystemSettingFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SystemSettingFormService);
  });

  describe('Service methods', () => {
    describe('createSystemSettingFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSystemSettingFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            value: expect.any(Object),
          })
        );
      });

      it('passing ISystemSetting should create a new form with FormGroup', () => {
        const formGroup = service.createSystemSettingFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            value: expect.any(Object),
          })
        );
      });
    });

    describe('getSystemSetting', () => {
      it('should return NewSystemSetting for default SystemSetting initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSystemSettingFormGroup(sampleWithNewData);

        const systemSetting = service.getSystemSetting(formGroup) as any;

        expect(systemSetting).toMatchObject(sampleWithNewData);
      });

      it('should return NewSystemSetting for empty SystemSetting initial value', () => {
        const formGroup = service.createSystemSettingFormGroup();

        const systemSetting = service.getSystemSetting(formGroup) as any;

        expect(systemSetting).toMatchObject({});
      });

      it('should return ISystemSetting', () => {
        const formGroup = service.createSystemSettingFormGroup(sampleWithRequiredData);

        const systemSetting = service.getSystemSetting(formGroup) as any;

        expect(systemSetting).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISystemSetting should not enable id FormControl', () => {
        const formGroup = service.createSystemSettingFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSystemSetting should disable id FormControl', () => {
        const formGroup = service.createSystemSettingFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
