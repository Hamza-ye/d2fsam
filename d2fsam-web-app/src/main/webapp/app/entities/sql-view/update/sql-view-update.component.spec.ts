import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SqlViewFormService } from './sql-view-form.service';
import { SqlViewService } from '../service/sql-view.service';
import { ISqlView } from '../sql-view.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { SqlViewUpdateComponent } from './sql-view-update.component';

describe('SqlView Management Update Component', () => {
  let comp: SqlViewUpdateComponent;
  let fixture: ComponentFixture<SqlViewUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sqlViewFormService: SqlViewFormService;
  let sqlViewService: SqlViewService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SqlViewUpdateComponent],
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
      .overrideTemplate(SqlViewUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SqlViewUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sqlViewFormService = TestBed.inject(SqlViewFormService);
    sqlViewService = TestBed.inject(SqlViewService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const sqlView: ISqlView = { id: 456 };
      const createdBy: IUser = { id: 86160 };
      sqlView.createdBy = createdBy;
      const updatedBy: IUser = { id: 90649 };
      sqlView.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 22997 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sqlView });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const sqlView: ISqlView = { id: 456 };
      const createdBy: IUser = { id: 61810 };
      sqlView.createdBy = createdBy;
      const updatedBy: IUser = { id: 53865 };
      sqlView.updatedBy = updatedBy;

      activatedRoute.data = of({ sqlView });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.sqlView).toEqual(sqlView);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISqlView>>();
      const sqlView = { id: 123 };
      jest.spyOn(sqlViewFormService, 'getSqlView').mockReturnValue(sqlView);
      jest.spyOn(sqlViewService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sqlView });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sqlView }));
      saveSubject.complete();

      // THEN
      expect(sqlViewFormService.getSqlView).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sqlViewService.update).toHaveBeenCalledWith(expect.objectContaining(sqlView));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISqlView>>();
      const sqlView = { id: 123 };
      jest.spyOn(sqlViewFormService, 'getSqlView').mockReturnValue({ id: null });
      jest.spyOn(sqlViewService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sqlView: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sqlView }));
      saveSubject.complete();

      // THEN
      expect(sqlViewFormService.getSqlView).toHaveBeenCalled();
      expect(sqlViewService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISqlView>>();
      const sqlView = { id: 123 };
      jest.spyOn(sqlViewService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sqlView });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sqlViewService.update).toHaveBeenCalled();
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
