import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TrackedEntityTypeAttributeFormService } from './tracked-entity-type-attribute-form.service';
import { TrackedEntityTypeAttributeService } from '../service/tracked-entity-type-attribute.service';
import { ITrackedEntityTypeAttribute } from '../tracked-entity-type-attribute.model';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';
import { ITrackedEntityType } from 'app/entities/tracked-entity-type/tracked-entity-type.model';
import { TrackedEntityTypeService } from 'app/entities/tracked-entity-type/service/tracked-entity-type.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { TrackedEntityTypeAttributeUpdateComponent } from './tracked-entity-type-attribute-update.component';

describe('TrackedEntityTypeAttribute Management Update Component', () => {
  let comp: TrackedEntityTypeAttributeUpdateComponent;
  let fixture: ComponentFixture<TrackedEntityTypeAttributeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trackedEntityTypeAttributeFormService: TrackedEntityTypeAttributeFormService;
  let trackedEntityTypeAttributeService: TrackedEntityTypeAttributeService;
  let dataElementService: DataElementService;
  let trackedEntityTypeService: TrackedEntityTypeService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TrackedEntityTypeAttributeUpdateComponent],
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
      .overrideTemplate(TrackedEntityTypeAttributeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityTypeAttributeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trackedEntityTypeAttributeFormService = TestBed.inject(TrackedEntityTypeAttributeFormService);
    trackedEntityTypeAttributeService = TestBed.inject(TrackedEntityTypeAttributeService);
    dataElementService = TestBed.inject(DataElementService);
    trackedEntityTypeService = TestBed.inject(TrackedEntityTypeService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call DataElement query and add missing value', () => {
      const trackedEntityTypeAttribute: ITrackedEntityTypeAttribute = { id: 456 };
      const trackedEntityAttribute: IDataElement = { id: 71888 };
      trackedEntityTypeAttribute.trackedEntityAttribute = trackedEntityAttribute;

      const dataElementCollection: IDataElement[] = [{ id: 28676 }];
      jest.spyOn(dataElementService, 'query').mockReturnValue(of(new HttpResponse({ body: dataElementCollection })));
      const additionalDataElements = [trackedEntityAttribute];
      const expectedCollection: IDataElement[] = [...additionalDataElements, ...dataElementCollection];
      jest.spyOn(dataElementService, 'addDataElementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityTypeAttribute });
      comp.ngOnInit();

      expect(dataElementService.query).toHaveBeenCalled();
      expect(dataElementService.addDataElementToCollectionIfMissing).toHaveBeenCalledWith(
        dataElementCollection,
        ...additionalDataElements.map(expect.objectContaining)
      );
      expect(comp.dataElementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TrackedEntityType query and add missing value', () => {
      const trackedEntityTypeAttribute: ITrackedEntityTypeAttribute = { id: 456 };
      const trackedEntityType: ITrackedEntityType = { id: 81777 };
      trackedEntityTypeAttribute.trackedEntityType = trackedEntityType;

      const trackedEntityTypeCollection: ITrackedEntityType[] = [{ id: 81069 }];
      jest.spyOn(trackedEntityTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: trackedEntityTypeCollection })));
      const additionalTrackedEntityTypes = [trackedEntityType];
      const expectedCollection: ITrackedEntityType[] = [...additionalTrackedEntityTypes, ...trackedEntityTypeCollection];
      jest.spyOn(trackedEntityTypeService, 'addTrackedEntityTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityTypeAttribute });
      comp.ngOnInit();

      expect(trackedEntityTypeService.query).toHaveBeenCalled();
      expect(trackedEntityTypeService.addTrackedEntityTypeToCollectionIfMissing).toHaveBeenCalledWith(
        trackedEntityTypeCollection,
        ...additionalTrackedEntityTypes.map(expect.objectContaining)
      );
      expect(comp.trackedEntityTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const trackedEntityTypeAttribute: ITrackedEntityTypeAttribute = { id: 456 };
      const updatedBy: IUser = { id: 15756 };
      trackedEntityTypeAttribute.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 57849 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityTypeAttribute });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const trackedEntityTypeAttribute: ITrackedEntityTypeAttribute = { id: 456 };
      const trackedEntityAttribute: IDataElement = { id: 96440 };
      trackedEntityTypeAttribute.trackedEntityAttribute = trackedEntityAttribute;
      const trackedEntityType: ITrackedEntityType = { id: 28120 };
      trackedEntityTypeAttribute.trackedEntityType = trackedEntityType;
      const updatedBy: IUser = { id: 67027 };
      trackedEntityTypeAttribute.updatedBy = updatedBy;

      activatedRoute.data = of({ trackedEntityTypeAttribute });
      comp.ngOnInit();

      expect(comp.dataElementsSharedCollection).toContain(trackedEntityAttribute);
      expect(comp.trackedEntityTypesSharedCollection).toContain(trackedEntityType);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.trackedEntityTypeAttribute).toEqual(trackedEntityTypeAttribute);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityTypeAttribute>>();
      const trackedEntityTypeAttribute = { id: 123 };
      jest.spyOn(trackedEntityTypeAttributeFormService, 'getTrackedEntityTypeAttribute').mockReturnValue(trackedEntityTypeAttribute);
      jest.spyOn(trackedEntityTypeAttributeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityTypeAttribute });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityTypeAttribute }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityTypeAttributeFormService.getTrackedEntityTypeAttribute).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trackedEntityTypeAttributeService.update).toHaveBeenCalledWith(expect.objectContaining(trackedEntityTypeAttribute));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityTypeAttribute>>();
      const trackedEntityTypeAttribute = { id: 123 };
      jest.spyOn(trackedEntityTypeAttributeFormService, 'getTrackedEntityTypeAttribute').mockReturnValue({ id: null });
      jest.spyOn(trackedEntityTypeAttributeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityTypeAttribute: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityTypeAttribute }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityTypeAttributeFormService.getTrackedEntityTypeAttribute).toHaveBeenCalled();
      expect(trackedEntityTypeAttributeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityTypeAttribute>>();
      const trackedEntityTypeAttribute = { id: 123 };
      jest.spyOn(trackedEntityTypeAttributeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityTypeAttribute });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trackedEntityTypeAttributeService.update).toHaveBeenCalled();
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

    describe('compareTrackedEntityType', () => {
      it('Should forward to trackedEntityTypeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(trackedEntityTypeService, 'compareTrackedEntityType');
        comp.compareTrackedEntityType(entity, entity2);
        expect(trackedEntityTypeService.compareTrackedEntityType).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
