import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RelationshipFormService } from './relationship-form.service';
import { RelationshipService } from '../service/relationship.service';
import { IRelationship } from '../relationship.model';
import { IRelationshipType } from 'app/entities/relationship-type/relationship-type.model';
import { RelationshipTypeService } from 'app/entities/relationship-type/service/relationship-type.service';
import { IRelationshipItem } from 'app/entities/relationship-item/relationship-item.model';
import { RelationshipItemService } from 'app/entities/relationship-item/service/relationship-item.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { RelationshipUpdateComponent } from './relationship-update.component';

describe('Relationship Management Update Component', () => {
  let comp: RelationshipUpdateComponent;
  let fixture: ComponentFixture<RelationshipUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let relationshipFormService: RelationshipFormService;
  let relationshipService: RelationshipService;
  let relationshipTypeService: RelationshipTypeService;
  let relationshipItemService: RelationshipItemService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RelationshipUpdateComponent],
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
      .overrideTemplate(RelationshipUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelationshipUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    relationshipFormService = TestBed.inject(RelationshipFormService);
    relationshipService = TestBed.inject(RelationshipService);
    relationshipTypeService = TestBed.inject(RelationshipTypeService);
    relationshipItemService = TestBed.inject(RelationshipItemService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call RelationshipType query and add missing value', () => {
      const relationship: IRelationship = { id: 456 };
      const relationshipType: IRelationshipType = { id: 96460 };
      relationship.relationshipType = relationshipType;

      const relationshipTypeCollection: IRelationshipType[] = [{ id: 41302 }];
      jest.spyOn(relationshipTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: relationshipTypeCollection })));
      const additionalRelationshipTypes = [relationshipType];
      const expectedCollection: IRelationshipType[] = [...additionalRelationshipTypes, ...relationshipTypeCollection];
      jest.spyOn(relationshipTypeService, 'addRelationshipTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationship });
      comp.ngOnInit();

      expect(relationshipTypeService.query).toHaveBeenCalled();
      expect(relationshipTypeService.addRelationshipTypeToCollectionIfMissing).toHaveBeenCalledWith(
        relationshipTypeCollection,
        ...additionalRelationshipTypes.map(expect.objectContaining)
      );
      expect(comp.relationshipTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call RelationshipItem query and add missing value', () => {
      const relationship: IRelationship = { id: 456 };
      const from: IRelationshipItem = { id: 89986 };
      relationship.from = from;
      const to: IRelationshipItem = { id: 49419 };
      relationship.to = to;

      const relationshipItemCollection: IRelationshipItem[] = [{ id: 85263 }];
      jest.spyOn(relationshipItemService, 'query').mockReturnValue(of(new HttpResponse({ body: relationshipItemCollection })));
      const additionalRelationshipItems = [from, to];
      const expectedCollection: IRelationshipItem[] = [...additionalRelationshipItems, ...relationshipItemCollection];
      jest.spyOn(relationshipItemService, 'addRelationshipItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationship });
      comp.ngOnInit();

      expect(relationshipItemService.query).toHaveBeenCalled();
      expect(relationshipItemService.addRelationshipItemToCollectionIfMissing).toHaveBeenCalledWith(
        relationshipItemCollection,
        ...additionalRelationshipItems.map(expect.objectContaining)
      );
      expect(comp.relationshipItemsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const relationship: IRelationship = { id: 456 };
      const updatedBy: IUser = { id: 58752 };
      relationship.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 12640 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relationship });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const relationship: IRelationship = { id: 456 };
      const relationshipType: IRelationshipType = { id: 33906 };
      relationship.relationshipType = relationshipType;
      const from: IRelationshipItem = { id: 42432 };
      relationship.from = from;
      const to: IRelationshipItem = { id: 48497 };
      relationship.to = to;
      const updatedBy: IUser = { id: 88992 };
      relationship.updatedBy = updatedBy;

      activatedRoute.data = of({ relationship });
      comp.ngOnInit();

      expect(comp.relationshipTypesSharedCollection).toContain(relationshipType);
      expect(comp.relationshipItemsSharedCollection).toContain(from);
      expect(comp.relationshipItemsSharedCollection).toContain(to);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.relationship).toEqual(relationship);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationship>>();
      const relationship = { id: 123 };
      jest.spyOn(relationshipFormService, 'getRelationship').mockReturnValue(relationship);
      jest.spyOn(relationshipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationship });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relationship }));
      saveSubject.complete();

      // THEN
      expect(relationshipFormService.getRelationship).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(relationshipService.update).toHaveBeenCalledWith(expect.objectContaining(relationship));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationship>>();
      const relationship = { id: 123 };
      jest.spyOn(relationshipFormService, 'getRelationship').mockReturnValue({ id: null });
      jest.spyOn(relationshipService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationship: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relationship }));
      saveSubject.complete();

      // THEN
      expect(relationshipFormService.getRelationship).toHaveBeenCalled();
      expect(relationshipService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationship>>();
      const relationship = { id: 123 };
      jest.spyOn(relationshipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationship });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(relationshipService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRelationshipType', () => {
      it('Should forward to relationshipTypeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(relationshipTypeService, 'compareRelationshipType');
        comp.compareRelationshipType(entity, entity2);
        expect(relationshipTypeService.compareRelationshipType).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareRelationshipItem', () => {
      it('Should forward to relationshipItemService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(relationshipItemService, 'compareRelationshipItem');
        comp.compareRelationshipItem(entity, entity2);
        expect(relationshipItemService.compareRelationshipItem).toHaveBeenCalledWith(entity, entity2);
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
