import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TrackedEntityInstanceAuditFormService } from './tracked-entity-instance-audit-form.service';
import { TrackedEntityInstanceAuditService } from '../service/tracked-entity-instance-audit.service';
import { ITrackedEntityInstanceAudit } from '../tracked-entity-instance-audit.model';

import { TrackedEntityInstanceAuditUpdateComponent } from './tracked-entity-instance-audit-update.component';

describe('TrackedEntityInstanceAudit Management Update Component', () => {
  let comp: TrackedEntityInstanceAuditUpdateComponent;
  let fixture: ComponentFixture<TrackedEntityInstanceAuditUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trackedEntityInstanceAuditFormService: TrackedEntityInstanceAuditFormService;
  let trackedEntityInstanceAuditService: TrackedEntityInstanceAuditService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TrackedEntityInstanceAuditUpdateComponent],
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
      .overrideTemplate(TrackedEntityInstanceAuditUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityInstanceAuditUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trackedEntityInstanceAuditFormService = TestBed.inject(TrackedEntityInstanceAuditFormService);
    trackedEntityInstanceAuditService = TestBed.inject(TrackedEntityInstanceAuditService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const trackedEntityInstanceAudit: ITrackedEntityInstanceAudit = { id: 456 };

      activatedRoute.data = of({ trackedEntityInstanceAudit });
      comp.ngOnInit();

      expect(comp.trackedEntityInstanceAudit).toEqual(trackedEntityInstanceAudit);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityInstanceAudit>>();
      const trackedEntityInstanceAudit = { id: 123 };
      jest.spyOn(trackedEntityInstanceAuditFormService, 'getTrackedEntityInstanceAudit').mockReturnValue(trackedEntityInstanceAudit);
      jest.spyOn(trackedEntityInstanceAuditService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityInstanceAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityInstanceAudit }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityInstanceAuditFormService.getTrackedEntityInstanceAudit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trackedEntityInstanceAuditService.update).toHaveBeenCalledWith(expect.objectContaining(trackedEntityInstanceAudit));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityInstanceAudit>>();
      const trackedEntityInstanceAudit = { id: 123 };
      jest.spyOn(trackedEntityInstanceAuditFormService, 'getTrackedEntityInstanceAudit').mockReturnValue({ id: null });
      jest.spyOn(trackedEntityInstanceAuditService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityInstanceAudit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityInstanceAudit }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityInstanceAuditFormService.getTrackedEntityInstanceAudit).toHaveBeenCalled();
      expect(trackedEntityInstanceAuditService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityInstanceAudit>>();
      const trackedEntityInstanceAudit = { id: 123 };
      jest.spyOn(trackedEntityInstanceAuditService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityInstanceAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trackedEntityInstanceAuditService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
