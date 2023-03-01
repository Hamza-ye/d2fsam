import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TrackedEntityTypeFormService } from './tracked-entity-type-form.service';
import { TrackedEntityTypeService } from '../service/tracked-entity-type.service';
import { ITrackedEntityType } from '../tracked-entity-type.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { TrackedEntityTypeUpdateComponent } from './tracked-entity-type-update.component';

describe('TrackedEntityType Management Update Component', () => {
  let comp: TrackedEntityTypeUpdateComponent;
  let fixture: ComponentFixture<TrackedEntityTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let trackedEntityTypeFormService: TrackedEntityTypeFormService;
  let trackedEntityTypeService: TrackedEntityTypeService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TrackedEntityTypeUpdateComponent],
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
      .overrideTemplate(TrackedEntityTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    trackedEntityTypeFormService = TestBed.inject(TrackedEntityTypeFormService);
    trackedEntityTypeService = TestBed.inject(TrackedEntityTypeService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const trackedEntityType: ITrackedEntityType = { id: 456 };
      const createdBy: IUser = { id: 28787 };
      trackedEntityType.createdBy = createdBy;
      const updatedBy: IUser = { id: 23527 };
      trackedEntityType.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 41138 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ trackedEntityType });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const trackedEntityType: ITrackedEntityType = { id: 456 };
      const createdBy: IUser = { id: 89079 };
      trackedEntityType.createdBy = createdBy;
      const updatedBy: IUser = { id: 40854 };
      trackedEntityType.updatedBy = updatedBy;

      activatedRoute.data = of({ trackedEntityType });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.trackedEntityType).toEqual(trackedEntityType);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityType>>();
      const trackedEntityType = { id: 123 };
      jest.spyOn(trackedEntityTypeFormService, 'getTrackedEntityType').mockReturnValue(trackedEntityType);
      jest.spyOn(trackedEntityTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityType }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityTypeFormService.getTrackedEntityType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(trackedEntityTypeService.update).toHaveBeenCalledWith(expect.objectContaining(trackedEntityType));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityType>>();
      const trackedEntityType = { id: 123 };
      jest.spyOn(trackedEntityTypeFormService, 'getTrackedEntityType').mockReturnValue({ id: null });
      jest.spyOn(trackedEntityTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: trackedEntityType }));
      saveSubject.complete();

      // THEN
      expect(trackedEntityTypeFormService.getTrackedEntityType).toHaveBeenCalled();
      expect(trackedEntityTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITrackedEntityType>>();
      const trackedEntityType = { id: 123 };
      jest.spyOn(trackedEntityTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ trackedEntityType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(trackedEntityTypeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
