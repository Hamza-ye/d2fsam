import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ExternalFileResourceFormService } from './external-file-resource-form.service';
import { ExternalFileResourceService } from '../service/external-file-resource.service';
import { IExternalFileResource } from '../external-file-resource.model';
import { IFileResource } from 'app/entities/file-resource/file-resource.model';
import { FileResourceService } from 'app/entities/file-resource/service/file-resource.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ExternalFileResourceUpdateComponent } from './external-file-resource-update.component';

describe('ExternalFileResource Management Update Component', () => {
  let comp: ExternalFileResourceUpdateComponent;
  let fixture: ComponentFixture<ExternalFileResourceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let externalFileResourceFormService: ExternalFileResourceFormService;
  let externalFileResourceService: ExternalFileResourceService;
  let fileResourceService: FileResourceService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ExternalFileResourceUpdateComponent],
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
      .overrideTemplate(ExternalFileResourceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExternalFileResourceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    externalFileResourceFormService = TestBed.inject(ExternalFileResourceFormService);
    externalFileResourceService = TestBed.inject(ExternalFileResourceService);
    fileResourceService = TestBed.inject(FileResourceService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call FileResource query and add missing value', () => {
      const externalFileResource: IExternalFileResource = { id: 456 };
      const fileResource: IFileResource = { id: 69515 };
      externalFileResource.fileResource = fileResource;

      const fileResourceCollection: IFileResource[] = [{ id: 48077 }];
      jest.spyOn(fileResourceService, 'query').mockReturnValue(of(new HttpResponse({ body: fileResourceCollection })));
      const additionalFileResources = [fileResource];
      const expectedCollection: IFileResource[] = [...additionalFileResources, ...fileResourceCollection];
      jest.spyOn(fileResourceService, 'addFileResourceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ externalFileResource });
      comp.ngOnInit();

      expect(fileResourceService.query).toHaveBeenCalled();
      expect(fileResourceService.addFileResourceToCollectionIfMissing).toHaveBeenCalledWith(
        fileResourceCollection,
        ...additionalFileResources.map(expect.objectContaining)
      );
      expect(comp.fileResourcesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const externalFileResource: IExternalFileResource = { id: 456 };
      const createdBy: IUser = { id: 68718 };
      externalFileResource.createdBy = createdBy;
      const updatedBy: IUser = { id: 46939 };
      externalFileResource.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 37321 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ externalFileResource });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const externalFileResource: IExternalFileResource = { id: 456 };
      const fileResource: IFileResource = { id: 37859 };
      externalFileResource.fileResource = fileResource;
      const createdBy: IUser = { id: 51400 };
      externalFileResource.createdBy = createdBy;
      const updatedBy: IUser = { id: 36385 };
      externalFileResource.updatedBy = updatedBy;

      activatedRoute.data = of({ externalFileResource });
      comp.ngOnInit();

      expect(comp.fileResourcesSharedCollection).toContain(fileResource);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.externalFileResource).toEqual(externalFileResource);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExternalFileResource>>();
      const externalFileResource = { id: 123 };
      jest.spyOn(externalFileResourceFormService, 'getExternalFileResource').mockReturnValue(externalFileResource);
      jest.spyOn(externalFileResourceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ externalFileResource });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: externalFileResource }));
      saveSubject.complete();

      // THEN
      expect(externalFileResourceFormService.getExternalFileResource).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(externalFileResourceService.update).toHaveBeenCalledWith(expect.objectContaining(externalFileResource));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExternalFileResource>>();
      const externalFileResource = { id: 123 };
      jest.spyOn(externalFileResourceFormService, 'getExternalFileResource').mockReturnValue({ id: null });
      jest.spyOn(externalFileResourceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ externalFileResource: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: externalFileResource }));
      saveSubject.complete();

      // THEN
      expect(externalFileResourceFormService.getExternalFileResource).toHaveBeenCalled();
      expect(externalFileResourceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExternalFileResource>>();
      const externalFileResource = { id: 123 };
      jest.spyOn(externalFileResourceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ externalFileResource });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(externalFileResourceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareFileResource', () => {
      it('Should forward to fileResourceService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(fileResourceService, 'compareFileResource');
        comp.compareFileResource(entity, entity2);
        expect(fileResourceService.compareFileResource).toHaveBeenCalledWith(entity, entity2);
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
