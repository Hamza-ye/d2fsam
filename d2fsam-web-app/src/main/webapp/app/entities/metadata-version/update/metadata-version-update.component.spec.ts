import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MetadataVersionFormService } from './metadata-version-form.service';
import { MetadataVersionService } from '../service/metadata-version.service';
import { IMetadataVersion } from '../metadata-version.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { MetadataVersionUpdateComponent } from './metadata-version-update.component';

describe('MetadataVersion Management Update Component', () => {
  let comp: MetadataVersionUpdateComponent;
  let fixture: ComponentFixture<MetadataVersionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let metadataVersionFormService: MetadataVersionFormService;
  let metadataVersionService: MetadataVersionService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MetadataVersionUpdateComponent],
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
      .overrideTemplate(MetadataVersionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MetadataVersionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    metadataVersionFormService = TestBed.inject(MetadataVersionFormService);
    metadataVersionService = TestBed.inject(MetadataVersionService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const metadataVersion: IMetadataVersion = { id: 456 };
      const updatedBy: IUser = { id: 97355 };
      metadataVersion.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 68631 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ metadataVersion });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const metadataVersion: IMetadataVersion = { id: 456 };
      const updatedBy: IUser = { id: 60197 };
      metadataVersion.updatedBy = updatedBy;

      activatedRoute.data = of({ metadataVersion });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.metadataVersion).toEqual(metadataVersion);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetadataVersion>>();
      const metadataVersion = { id: 123 };
      jest.spyOn(metadataVersionFormService, 'getMetadataVersion').mockReturnValue(metadataVersion);
      jest.spyOn(metadataVersionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metadataVersion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: metadataVersion }));
      saveSubject.complete();

      // THEN
      expect(metadataVersionFormService.getMetadataVersion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(metadataVersionService.update).toHaveBeenCalledWith(expect.objectContaining(metadataVersion));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetadataVersion>>();
      const metadataVersion = { id: 123 };
      jest.spyOn(metadataVersionFormService, 'getMetadataVersion').mockReturnValue({ id: null });
      jest.spyOn(metadataVersionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metadataVersion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: metadataVersion }));
      saveSubject.complete();

      // THEN
      expect(metadataVersionFormService.getMetadataVersion).toHaveBeenCalled();
      expect(metadataVersionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMetadataVersion>>();
      const metadataVersion = { id: 123 };
      jest.spyOn(metadataVersionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ metadataVersion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(metadataVersionService.update).toHaveBeenCalled();
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
