import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TrackedEntityAttributeValueFormService } from './tracked-entity-attribute-value-form.service';
import { TrackedEntityAttributeValueService } from '../service/tracked-entity-attribute-value.service';
import { ITrackedEntityAttributeValue } from '../tracked-entity-attribute-value.model';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { ITrackedEntityInstance } from 'app/entities/tracked-entity-instance/tracked-entity-instance.model';
import { TrackedEntityInstanceService } from 'app/entities/tracked-entity-instance/service/tracked-entity-instance.service';

import { TrackedEntityAttributeValueUpdateComponent } from './tracked-entity-attribute-value-update.component';

describe('TrackedEntityAttributeValue Management Update Component', () => {
  let comp: TrackedEntityAttributeValueUpdateComponent;
  let fixture: ComponentFixture<TrackedEntityAttributeValueUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trackedEntityAttributeValueFormService: TrackedEntityAttributeValueFormService;
  let trackedEntityAttributeValueService: TrackedEntityAttributeValueService;
  let dataElementService: DataElementService;
  let trackedEntityInstanceService: TrackedEntityInstanceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TrackedEntityAttributeValueUpdateComponent],
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
      .overrideTemplate(TrackedEntityAttributeValueUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityAttributeValueUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trackedEntityAttributeValueFormService = TestBed.inject(TrackedEntityAttributeValueFormService);
    trackedEntityAttributeValueService = TestBed.inject(TrackedEntityAttributeValueService);
    dataElementService = TestBed.inject(DataElementService);
    trackedEntityInstanceService = TestBed.inject(TrackedEntityInstanceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call DataElement query and add missing value', () => {
      const trackedEntityAttributeValue: ITrackedEntityAttributeValue = { id: 456 };
      const attribute: IDataElement = { id: 13292 };
      trackedEntityAttributeValue.attribute = attribute;

      const dataElementCollection: IDataElement[] = [{ id: 35274 }];
      jest.spyOn(dataElementService, 'query').mockReturnValue(of(new HttpResponse({ body: dataElementCollection })));
      const additionalDataElements = [attribute];
      const expectedCollection: IDataElement[] = [...additionalDataElements, ...dataElementCollection];
      jest.spyOn(dataElementService, 'addDataElementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityAttributeValue });
      comp.ngOnInit();

      expect(dataElementService.query).toHaveBeenCalled();
      expect(dataElementService.addDataElementToCollectionIfMissing).toHaveBeenCalledWith(
        dataElementCollection,
        ...additionalDataElements.map(expect.objectContaining)
      );
      expect(comp.dataElementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TrackedEntityInstance query and add missing value', () => {
      const trackedEntityAttributeValue: ITrackedEntityAttributeValue = { id: 456 };
      const entityInstance: ITrackedEntityInstance = { id: 18297 };
      trackedEntityAttributeValue.entityInstance = entityInstance;

      const trackedEntityInstanceCollection: ITrackedEntityInstance[] = [{ id: 22294 }];
      jest.spyOn(trackedEntityInstanceService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityInstanceCollection })));
      const additionalTrackedEntityInstances = [entityInstance];
      const expectedCollection: ITrackedEntityInstance[] = [...additionalTrackedEntityInstances, ...trackedEntityInstanceCollection];
      jest.spyOn(trackedEntityInstanceService, 'addTrackedEntityInstanceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityAttributeValue });
      comp.ngOnInit();

      expect(trackedEntityInstanceService.query).toHaveBeenCalled();
      expect(trackedEntityInstanceService.addTrackedEntityInstanceToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityInstanceCollection,
        ...additionalTrackedEntityInstances.map(expect.objectContaining)
      );
      expect(comp.trackedEntityInstancesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const trackedEntityAttributeValue: ITrackedEntityAttributeValue = { id: 456 };
      const attribute: IDataElement = { id: 28306 };
      trackedEntityAttributeValue.attribute = attribute;
      const entityInstance: ITrackedEntityInstance = { id: 63197 };
      trackedEntityAttributeValue.entityInstance = entityInstance;

      activatedRoute.data = of({ trackedEntityAttributeValue });
      comp.ngOnInit();

      expect(comp.dataElementsSharedCollection).toContain(attribute);
      expect(comp.trackedEntityInstancesSharedCollection).toContain(entityInstance);
      expect(comp.trackedEntityAttributeValue).toEqual(trackedEntityAttributeValue);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityAttributeValue>>();
      const trackedEntityAttributeValue = { id: 123 };
      jest.spyOn(trackedEntityAttributeValueFormService, 'getTrackedEntityAttributeValue').mockReturnValue(trackedEntityAttributeValue);
      jest.spyOn(trackedEntityAttributeValueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityAttributeValue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityAttributeValue }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityAttributeValueFormService.getTrackedEntityAttributeValue).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trackedEntityAttributeValueService.update).toHaveBeenCalledWith(expect.objectContaining(trackedEntityAttributeValue));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityAttributeValue>>();
      const trackedEntityAttributeValue = { id: 123 };
      jest.spyOn(trackedEntityAttributeValueFormService, 'getTrackedEntityAttributeValue').mockReturnValue({ id: null });
      jest.spyOn(trackedEntityAttributeValueService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityAttributeValue: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityAttributeValue }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityAttributeValueFormService.getTrackedEntityAttributeValue).toHaveBeenCalled();
      expect(trackedEntityAttributeValueService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityAttributeValue>>();
      const trackedEntityAttributeValue = { id: 123 };
      jest.spyOn(trackedEntityAttributeValueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityAttributeValue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trackedEntityAttributeValueService.update).toHaveBeenCalled();
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
