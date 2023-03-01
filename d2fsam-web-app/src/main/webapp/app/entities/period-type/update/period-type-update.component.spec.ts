import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PeriodTypeFormService } from './period-type-form.service';
import { PeriodTypeService } from '../service/period-type.service';
import { IPeriodType } from '../period-type.model';

import { PeriodTypeUpdateComponent } from './period-type-update.component';

describe('PeriodType Management Update Component', () => {
  let comp: PeriodTypeUpdateComponent;
  let fixture: ComponentFixture<PeriodTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let periodTypeFormService: PeriodTypeFormService;
  let periodTypeService: PeriodTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PeriodTypeUpdateComponent],
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
      .overrideTemplate(PeriodTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PeriodTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    periodTypeFormService = TestBed.inject(PeriodTypeFormService);
    periodTypeService = TestBed.inject(PeriodTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const periodType: IPeriodType = { id: 456 };

      activatedRoute.data = of({ periodType });
      comp.ngOnInit();

      expect(comp.periodType).toEqual(periodType);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPeriodType>>();
      const periodType = { id: 123 };
      jest.spyOn(periodTypeFormService, 'getPeriodType').mockReturnValue(periodType);
      jest.spyOn(periodTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ periodType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: periodType }));
      saveSubject.complete();

      // THEN
      expect(periodTypeFormService.getPeriodType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(periodTypeService.update).toHaveBeenCalledWith(expect.objectContaining(periodType));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPeriodType>>();
      const periodType = { id: 123 };
      jest.spyOn(periodTypeFormService, 'getPeriodType').mockReturnValue({ id: null });
      jest.spyOn(periodTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ periodType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: periodType }));
      saveSubject.complete();

      // THEN
      expect(periodTypeFormService.getPeriodType).toHaveBeenCalled();
      expect(periodTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPeriodType>>();
      const periodType = { id: 123 };
      jest.spyOn(periodTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ periodType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(periodTypeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
