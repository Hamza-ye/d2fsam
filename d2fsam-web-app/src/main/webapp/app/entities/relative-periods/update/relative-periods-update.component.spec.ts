import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RelativePeriodsFormService } from './relative-periods-form.service';
import { RelativePeriodsService } from '../service/relative-periods.service';
import { IRelativePeriods } from '../relative-periods.model';

import { RelativePeriodsUpdateComponent } from './relative-periods-update.component';

describe('RelativePeriods Management Update Component', () => {
  let comp: RelativePeriodsUpdateComponent;
  let fixture: ComponentFixture<RelativePeriodsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let relativePeriodsFormService: RelativePeriodsFormService;
  let relativePeriodsService: RelativePeriodsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RelativePeriodsUpdateComponent],
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
      .overrideTemplate(RelativePeriodsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelativePeriodsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    relativePeriodsFormService = TestBed.inject(RelativePeriodsFormService);
    relativePeriodsService = TestBed.inject(RelativePeriodsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const relativePeriods: IRelativePeriods = { id: 456 };

      activatedRoute.data = of({ relativePeriods });
      comp.ngOnInit();

      expect(comp.relativePeriods).toEqual(relativePeriods);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelativePeriods>>();
      const relativePeriods = { id: 123 };
      jest.spyOn(relativePeriodsFormService, 'getRelativePeriods').mockReturnValue(relativePeriods);
      jest.spyOn(relativePeriodsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relativePeriods });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relativePeriods }));
      saveSubject.complete();

      // THEN
      expect(relativePeriodsFormService.getRelativePeriods).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(relativePeriodsService.update).toHaveBeenCalledWith(expect.objectContaining(relativePeriods));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelativePeriods>>();
      const relativePeriods = { id: 123 };
      jest.spyOn(relativePeriodsFormService, 'getRelativePeriods').mockReturnValue({ id: null });
      jest.spyOn(relativePeriodsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relativePeriods: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relativePeriods }));
      saveSubject.complete();

      // THEN
      expect(relativePeriodsFormService.getRelativePeriods).toHaveBeenCalled();
      expect(relativePeriodsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelativePeriods>>();
      const relativePeriods = { id: 123 };
      jest.spyOn(relativePeriodsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relativePeriods });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(relativePeriodsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
