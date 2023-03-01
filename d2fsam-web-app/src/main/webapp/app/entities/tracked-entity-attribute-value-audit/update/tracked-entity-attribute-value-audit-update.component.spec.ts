import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TrackedEntityAttributeValueAuditFormService } from './tracked-entity-attribute-value-audit-form.service';
import { TrackedEntityAttributeValueAuditService } from '../service/tracked-entity-attribute-value-audit.service';
import { ITrackedEntityAttributeValueAudit } from '../tracked-entity-attribute-value-audit.model';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';

import { TrackedEntityAttributeValueAuditUpdateComponent } from './tracked-entity-attribute-value-audit-update.component';

describe('TrackedEntityAttributeValueAudit Management Update Component', () => {
  let comp: TrackedEntityAttributeValueAuditUpdateComponent;
  let fixture: ComponentFixture<TrackedEntityAttributeValueAuditUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trackedEntityAttributeValueAuditFormService: TrackedEntityAttributeValueAuditFormService;
  let trackedEntityAttributeValueAuditService: TrackedEntityAttributeValueAuditService;
  let dataElementService: DataElementService;
  let trackedEntityInstanceService: TrackedEntityInstanceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TrackedEntityAttributeValueAuditUpdateComponent],
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
      .overrideTemplate(TrackedEntityAttributeValueAuditUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityAttributeValueAuditUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trackedEntityAttributeValueAuditFormService = TestBed.inject(TrackedEntityAttributeValueAuditFormService);
    trackedEntityAttributeValueAuditService = TestBed.inject(TrackedEntityAttributeValueAuditService);
    dataElementService = TestBed.inject(DataElementService);
    trackedEntityInstanceService = TestBed.inject(TrackedEntityInstanceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call DataElement query and add missing value', () => {
      const trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit = { id: 456 };
      const attribute: IDataElement = { id: 88687 };
      trackedEntityAttributeValueAudit.attribute = attribute;

      const dataElementCollection: IDataElement[] = [{ id: 76366 }];
      jest.spyOn(dataElementService, 'query').mockReturnValue(of(new HttpResponse({ body: dataElementCollection })));
      const additionalDataElements = [attribute];
      const expectedCollection: IDataElement[] = [...additionalDataElements, ...dataElementCollection];
      jest.spyOn(dataElementService, 'addDataElementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityAttributeValueAudit });
      comp.ngOnInit();

      expect(dataElementService.query).toHaveBeenCalled();
      expect(dataElementService.addDataElementToCollectionIfMissing).toHaveBeenCalledWith(
        dataElementCollection,
        ...additionalDataElements.map(expect.objectContaining)
      );
      expect(comp.dataElementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TrackedEntityInstance query and add missing value', () => {
      const trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit = { id: 456 };
      const entityInstance: ITrackedEntityInstance = { id: 58370 };
      trackedEntityAttributeValueAudit.entityInstance = entityInstance;

      const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [{ id: 19984 }];
      jest.spyOn(trackedEntityInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityInstanceCollection })));
      const additionalTrackedEntityInstances = [entityInstance];
      const expectedCollection: ITrackedEntityInstance[] = [...additionalTrackedEntityInstances, ...trackedEntityInstanceCollection];
      jest.spyOn(trackedEntityInstanceService, 'addTrackedEntityInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityAttributeValueAudit });
      comp.ngOnInit();

      expect(trackedEntityInstanceService.query).toHaveBeenCalled();
      expect(trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityInstanceCollection,
        ...additionalTrackedEntityInstances.map(expect.objectContaining)
      );
      expect(comp.trackedEntityInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const trackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit = { id: 456 };
      const attribute: IDataElement = { id: 67181 };
      trackedEntityAttributeValueAudit.attribute = attribute;
      const entityInstance: ITrackedEntityInstance = { id: 99663 };
      trackedEntityAttributeValueAudit.entityInstance = entityInstance;

      activatedRoute.data = of({ trackedEntityAttributeValueAudit });
      comp.ngOnInit();

      expect(comp.dataElementsSharedCollection).toContain(attribute);
      expect(comp.trackedEntityInstancesSharedCollection).toContain(entityInstance);
      expect(comp.trackedEntityAttributeValueAudit).toEqual(trackedEntityAttributeValueAudit);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityAttributeValueAudit>>();
      const trackedEntityAttributeValueAudit = { id: 123 };
      jest
        .spyOn(trackedEntityAttributeValueAuditFormService, 'getTrackedEntityAttributeValueAudit')
        .mockReturnValue(trackedEntityAttributeValueAudit);
      jest.spyOn(trackedEntityAttributeValueAuditService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityAttributeValueAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityAttributeValueAudit }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityAttributeValueAuditFormService.getTrackedEntityAttributeValueAudit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trackedEntityAttributeValueAuditService.update).toHaveBeenCalledWith(
        expect.objectContaining(trackedEntityAttributeValueAudit)
      );
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityAttributeValueAudit>>();
      const trackedEntityAttributeValueAudit = { id: 123 };
      jest.spyOn(trackedEntityAttributeValueAuditFormService, 'getTrackedEntityAttributeValueAudit').mockReturnValue({ id: null });
      jest.spyOn(trackedEntityAttributeValueAuditService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityAttributeValueAudit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityAttributeValueAudit }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityAttributeValueAuditFormService.getTrackedEntityAttributeValueAudit).toHaveBeenCalled();
      expect(trackedEntityAttributeValueAuditService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityAttributeValueAudit>>();
      const trackedEntityAttributeValueAudit = { id: 123 };
      jest.spyOn(trackedEntityAttributeValueAuditService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityAttributeValueAudit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trackedEntityAttributeValueAuditService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDataElement', () => {
      it('Should forward to dataElementService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(dataElementService, 'compareDataElement');
        comp.compareDataElement(entity, entity2);
        expect(dataElementService.compareDataElement).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTrackedEntityInstance', () => {
      it('Should forward to trackedEntityInstanceService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(trackedEntityInstanceService, 'compareTrackedEntityInstance');
        comp.compareTrackedEntityInstance(entity, entity2);
        expect(trackedEntityInstanceService.compareTrackedEntityInstance).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
