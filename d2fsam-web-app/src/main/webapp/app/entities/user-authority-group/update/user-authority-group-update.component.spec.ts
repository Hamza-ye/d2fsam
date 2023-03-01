import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { UserAuthorityGroupFormService } from './user-authority-group-form.service';
import { UserAuthorityGroupService } from '../service/user-authority-group.service';
import { IUserAuthorityGroup } from '../user-authority-group.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { UserAuthorityGroupUpdateComponent } from './user-authority-group-update.component';

describe('UserAuthorityGroup Management Update Component', () => {
  let comp: UserAuthorityGroupUpdateComponent;
  let fixture: ComponentFixture<UserAuthorityGroupUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userAuthorityGroupFormService: UserAuthorityGroupFormService;
  let userAuthorityGroupService: UserAuthorityGroupService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [UserAuthorityGroupUpdateComponent],
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
      .overrideTemplate(UserAuthorityGroupUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserAuthorityGroupUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userAuthorityGroupFormService = TestBed.inject(UserAuthorityGroupFormService);
    userAuthorityGroupService = TestBed.inject(UserAuthorityGroupService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const userAuthorityGroup: IUserAuthorityGroup = { id: 456 };
      const createdBy: IUser = { id: 833 };
      userAuthorityGroup.createdBy = createdBy;
      const updatedBy: IUser = { id: 74412 };
      userAuthorityGroup.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 56807 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userAuthorityGroup });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userAuthorityGroup: IUserAuthorityGroup = { id: 456 };
      const createdBy: IUser = { id: 16657 };
      userAuthorityGroup.createdBy = createdBy;
      const updatedBy: IUser = { id: 28252 };
      userAuthorityGroup.updatedBy = updatedBy;

      activatedRoute.data = of({ userAuthorityGroup });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.userAuthorityGroup).toEqual(userAuthorityGroup);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserAuthorityGroup>>();
      const userAuthorityGroup = { id: 123 };
      jest.spyOn(userAuthorityGroupFormService, 'getUserAuthorityGroup').mockReturnValue(userAuthorityGroup);
      jest.spyOn(userAuthorityGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userAuthorityGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userAuthorityGroup }));
      saveSubject.complete();

      // THEN
      expect(userAuthorityGroupFormService.getUserAuthorityGroup).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userAuthorityGroupService.update).toHaveBeenCalledWith(expect.objectContaining(userAuthorityGroup));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserAuthorityGroup>>();
      const userAuthorityGroup = { id: 123 };
      jest.spyOn(userAuthorityGroupFormService, 'getUserAuthorityGroup').mockReturnValue({ id: null });
      jest.spyOn(userAuthorityGroupService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userAuthorityGroup: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userAuthorityGroup }));
      saveSubject.complete();

      // THEN
      expect(userAuthorityGroupFormService.getUserAuthorityGroup).toHaveBeenCalled();
      expect(userAuthorityGroupService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserAuthorityGroup>>();
      const userAuthorityGroup = { id: 123 };
      jest.spyOn(userAuthorityGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userAuthorityGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userAuthorityGroupService.update).toHaveBeenCalled();
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
