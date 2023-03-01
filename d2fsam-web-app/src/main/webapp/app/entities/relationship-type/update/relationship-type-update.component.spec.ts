import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RelationshipTypeFormService } from './relationship-type-form.service';
import { RelationshipTypeService } from '../service/relationship-type.service';
import { IRelationshipType } from '../relationship-type.model';
import { IRelationshipConstraint } from 'app/entities/relationship-constraint/relationship-constraint.model';
import { RelationshipConstraintService } from 'app/entities/relationship-constraint/service/relationship-constraint.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { RelationshipTypeUpdateComponent } from './relationship-type-update.component';

describe('RelationshipType Management Update Component', () => {
  let comp: RelationshipTypeUpdateComponent;
  let fixture: ComponentFixture<RelationshipTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let relationshipTypeFormService: RelationshipTypeFormService;
  let relationshipTypeService: RelationshipTypeService;
  let relationshipConstraintService: RelationshipConstraintService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RelationshipTypeUpdateComponent],
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
      .overrideTemplate(RelationshipTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelationshipTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    relationshipTypeFormService = TestBed.inject(RelationshipTypeFormService);
    relationshipTypeService = TestBed.inject(RelationshipTypeService);
    relationshipConstraintService = TestBed.inject(RelationshipConstraintService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call RelationshipConstraint query and add missing value', () => {
      const relationshipType: IRelationshipType = { id: 456 };
      const fromConstraint: IRelationshipConstraint = { id: 22260 };
      relationshipType.fromConstraint = fromConstraint;
      const toConstraint: IRelationshipConstraint = { id: 20489 };
      relationshipType.toConstraint = toConstraint;

      const relationshipConstraintCollection: IRelationshipConstraint[] = [{ id: 35831 }];
      jest.spyOn(relationshipConstraintService, 'query').mockReturnValue(of(new HttpResponse({ body: relationshipConstraintCollection })));
      const additionalRelationshipConstraints = [fromConstraint, toConstraint];
      const expectedCollection: IRelationshipConstraint[] = [...additionalRelationshipConstraints, ...relationshipConstraintCollection];
      jest.spyOn(relationshipConstraintService, 'addRelationshipConstraintToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationshipType });
      comp.ngOnInit();

      expect(relationshipConstraintService.query).toHaveBeenCalled();
      expect(relationshipConstraintService.addRelationshipConstraintToCollectionIfMissing).toHaveBeenCalledWith(
        relationshipConstraintCollection,
        ...additionalRelationshipConstraints.map(expect.objectContaining)
      );
      expect(comp.relationshipConstraintsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const relationshipType: IRelationshipType = { id: 456 };
      const createdBy: IUser = { id: 71455 };
      relationshipType.createdBy = createdBy;
      const updatedBy: IUser = { id: 8354 };
      relationshipType.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 48883 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationshipType });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const relationshipType: IRelationshipType = { id: 456 };
      const fromConstraint: IRelationshipConstraint = { id: 94100 };
      relationshipType.fromConstraint = fromConstraint;
      const toConstraint: IRelationshipConstraint = { id: 7276 };
      relationshipType.toConstraint = toConstraint;
      const createdBy: IUser = { id: 35435 };
      relationshipType.createdBy = createdBy;
      const updatedBy: IUser = { id: 59907 };
      relationshipType.updatedBy = updatedBy;

      activatedRoute.data = of({ relationshipType });
      comp.ngOnInit();

      expect(comp.relationshipConstraintsSharedCollection).toContain(fromConstraint);
      expect(comp.relationshipConstraintsSharedCollection).toContain(toConstraint);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.relationshipType).toEqual(relationshipType);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationshipType>>();
      const relationshipType = { id: 123 };
      jest.spyOn(relationshipTypeFormService, 'getRelationshipType').mockReturnValue(relationshipType);
      jest.spyOn(relationshipTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationshipType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relationshipType }));
      saveSubject.complete();

      // THEN
      expect(relationshipTypeFormService.getRelationshipType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(relationshipTypeService.update).toHaveBeenCalledWith(expect.objectContaining(relationshipType));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationshipType>>();
      const relationshipType = { id: 123 };
      jest.spyOn(relationshipTypeFormService, 'getRelationshipType').mockReturnValue({ id: null });
      jest.spyOn(relationshipTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationshipType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relationshipType }));
      saveSubject.complete();

      // THEN
      expect(relationshipTypeFormService.getRelationshipType).toHaveBeenCalled();
      expect(relationshipTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationshipType>>();
      const relationshipType = { id: 123 };
      jest.spyOn(relationshipTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationshipType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(relationshipTypeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRelationshipConstraint', () => {
      it('Should forward to relationshipConstraintService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(relationshipConstraintService, 'compareRelationshipConstraint');
        comp.compareRelationshipConstraint(entity, entity2);
        expect(relationshipConstraintService.compareRelationshipConstraint).toHaveBeenCalledWith(entity, entity2);
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
