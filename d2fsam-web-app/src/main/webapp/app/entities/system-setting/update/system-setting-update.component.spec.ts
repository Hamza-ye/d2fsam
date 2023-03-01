import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SystemSettingFormService } from './system-setting-form.service';
import { SystemSettingService } from '../service/system-setting.service';
import { ISystemSetting } from '../system-setting.model';

import { SystemSettingUpdateComponent } from './system-setting-update.component';

describe('SystemSetting Management Update Component', () => {
  let comp: SystemSettingUpdateComponent;
  let fixture: ComponentFixture<SystemSettingUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let systemSettingFormService: SystemSettingFormService;
  let systemSettingService: SystemSettingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SystemSettingUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SystemSettingUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SystemSettingUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    systemSettingFormService = TestBed.inject(SystemSettingFormService);
    systemSettingService = TestBed.inject(SystemSettingService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const systemSetting: ISystemSetting = { id: 456 };

      activatedRoute.data = of({ systemSetting });
      comp.ngOnInit();

      expect(comp.systemSetting).toEqual(systemSetting);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISystemSetting>>();
      const systemSetting = { id: 123 };
      jest.spyOn(systemSettingFormService, 'getSystemSetting').mockReturnValue(systemSetting);
      jest.spyOn(systemSettingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ systemSetting });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: systemSetting }));
      saveSubject.complete();

      // THEN
      expect(systemSettingFormService.getSystemSetting).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(systemSettingService.update).toHaveBeenCalledWith(expect.objectContaining(systemSetting));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISystemSetting>>();
      const systemSetting = { id: 123 };
      jest.spyOn(systemSettingFormService, 'getSystemSetting').mockReturnValue({ id: null });
      jest.spyOn(systemSettingService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ systemSetting: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: systemSetting }));
      saveSubject.complete();

      // THEN
      expect(systemSettingFormService.getSystemSetting).toHaveBeenCalled();
      expect(systemSettingService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISystemSetting>>();
      const systemSetting = { id: 123 };
      jest.spyOn(systemSettingService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ systemSetting });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(systemSettingService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
