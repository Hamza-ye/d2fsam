import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DemographicDataSourceFormService } from './demographic-data-source-form.service';
import { DemographicDataSourceService } from '../service/demographic-data-source.service';
import { IDemographicDataSource } from '../demographic-data-source.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { DemographicDataSourceUpdateComponent } from './demographic-data-source-update.component';

describe('DemographicDataSource Management Update Component', () => {
  let comp: DemographicDataSourceUpdateComponent;
  let fixture: ComponentFixture<DemographicDataSourceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let demographicDataSourceFormService: DemographicDataSourceFormService;
  let demographicDataSourceService: DemographicDataSourceService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DemographicDataSourceUpdateComponent],
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
      .overrideTemplate(DemographicDataSourceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DemographicDataSourceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    demographicDataSourceFormService = TestBed.inject(DemographicDataSourceFormService);
    demographicDataSourceService = TestBed.inject(DemographicDataSourceService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const demographicDataSource: IDemographicDataSource = { id: 456 };
      const createdBy: IUser = { id: 35206 };
      demographicDataSource.createdBy = createdBy;
      const updatedBy: IUser = { id: 11265 };
      demographicDataSource.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 24736 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ demographicDataSource });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const demographicDataSource: IDemographicDataSource = { id: 456 };
      const createdBy: IUser = { id: 76292 };
      demographicDataSource.createdBy = createdBy;
      const updatedBy: IUser = { id: 87375 };
      demographicDataSource.updatedBy = updatedBy;

      activatedRoute.data = of({ demographicDataSource });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.demographicDataSource).toEqual(demographicDataSource);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDemographicDataSource>>();
      const demographicDataSource = { id: 123 };
      jest.spyOn(demographicDataSourceFormService, 'getDemographicDataSource').mockReturnValue(demographicDataSource);
      jest.spyOn(demographicDataSourceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demographicDataSource });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: demographicDataSource }));
      saveSubject.complete();

      // THEN
      expect(demographicDataSourceFormService.getDemographicDataSource).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(demographicDataSourceService.update).toHaveBeenCalledWith(expect.objectContaining(demographicDataSource));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDemographicDataSource>>();
      const demographicDataSource = { id: 123 };
      jest.spyOn(demographicDataSourceFormService, 'getDemographicDataSource').mockReturnValue({ id: null });
      jest.spyOn(demographicDataSourceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demographicDataSource: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: demographicDataSource }));
      saveSubject.complete();

      // THEN
      expect(demographicDataSourceFormService.getDemographicDataSource).toHaveBeenCalled();
      expect(demographicDataSourceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDemographicDataSource>>();
      const demographicDataSource = { id: 123 };
      jest.spyOn(demographicDataSourceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demographicDataSource });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(demographicDataSourceService.update).toHaveBeenCalled();
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
