import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FileResourceFormService } from './file-resource-form.service';
import { FileResourceService } from '../service/file-resource.service';
import { IFileResource } from '../file-resource.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { FileResourceUpdateComponent } from './file-resource-update.component';

describe('FileResource Management Update Component', () => {
  let comp: FileResourceUpdateComponent;
  let fixture: ComponentFixture<FileResourceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let fileResourceFormService: FileResourceFormService;
  let fileResourceService: FileResourceService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FileResourceUpdateComponent],
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
      .overrideTemplate(FileResourceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FileResourceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fileResourceFormService = TestBed.inject(FileResourceFormService);
    fileResourceService = TestBed.inject(FileResourceService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const fileResource: IFileResource = { id: 456 };
      const createdBy: IUser = { id: 35768 };
      fileResource.createdBy = createdBy;
      const updatedBy: IUser = { id: 17645 };
      fileResource.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 16722 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ fileResource });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const fileResource: IFileResource = { id: 456 };
      const createdBy: IUser = { id: 58352 };
      fileResource.createdBy = createdBy;
      const updatedBy: IUser = { id: 55927 };
      fileResource.updatedBy = updatedBy;

      activatedRoute.data = of({ fileResource });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.fileResource).toEqual(fileResource);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFileResource>>();
      const fileResource = { id: 123 };
      jest.spyOn(fileResourceFormService, 'getFileResource').mockReturnValue(fileResource);
      jest.spyOn(fileResourceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fileResource });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fileResource }));
      saveSubject.complete();

      // THEN
      expect(fileResourceFormService.getFileResource).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(fileResourceService.update).toHaveBeenCalledWith(expect.objectContaining(fileResource));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFileResource>>();
      const fileResource = { id: 123 };
      jest.spyOn(fileResourceFormService, 'getFileResource').mockReturnValue({ id: null });
      jest.spyOn(fileResourceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fileResource: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fileResource }));
      saveSubject.complete();

      // THEN
      expect(fileResourceFormService.getFileResource).toHaveBeenCalled();
      expect(fileResourceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFileResource>>();
      const fileResource = { id: 123 };
      jest.spyOn(fileResourceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fileResource });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fileResourceService.update).toHaveBeenCalled();
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
