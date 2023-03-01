import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DatastoreEntryFormService } from './datastore-entry-form.service';
import { DatastoreEntryService } from '../service/datastore-entry.service';
import { IDatastoreEntry } from '../datastore-entry.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { DatastoreEntryUpdateComponent } from './datastore-entry-update.component';

describe('DatastoreEntry Management Update Component', () => {
  let comp: DatastoreEntryUpdateComponent;
  let fixture: ComponentFixture<DatastoreEntryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let datastoreEntryFormService: DatastoreEntryFormService;
  let datastoreEntryService: DatastoreEntryService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DatastoreEntryUpdateComponent],
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
      .overrideTemplate(DatastoreEntryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DatastoreEntryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    datastoreEntryFormService = TestBed.inject(DatastoreEntryFormService);
    datastoreEntryService = TestBed.inject(DatastoreEntryService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const datastoreEntry: IDatastoreEntry = { id: 456 };
      const createdBy: IUser = { id: 90230 };
      datastoreEntry.createdBy = createdBy;
      const updatedBy: IUser = { id: 89901 };
      datastoreEntry.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 93568 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ datastoreEntry });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const datastoreEntry: IDatastoreEntry = { id: 456 };
      const createdBy: IUser = { id: 78493 };
      datastoreEntry.createdBy = createdBy;
      const updatedBy: IUser = { id: 55872 };
      datastoreEntry.updatedBy = updatedBy;

      activatedRoute.data = of({ datastoreEntry });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.datastoreEntry).toEqual(datastoreEntry);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDatastoreEntry>>();
      const datastoreEntry = { id: 123 };
      jest.spyOn(datastoreEntryFormService, 'getDatastoreEntry').mockReturnValue(datastoreEntry);
      jest.spyOn(datastoreEntryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ datastoreEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: datastoreEntry }));
      saveSubject.complete();

      // THEN
      expect(datastoreEntryFormService.getDatastoreEntry).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(datastoreEntryService.update).toHaveBeenCalledWith(expect.objectContaining(datastoreEntry));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDatastoreEntry>>();
      const datastoreEntry = { id: 123 };
      jest.spyOn(datastoreEntryFormService, 'getDatastoreEntry').mockReturnValue({ id: null });
      jest.spyOn(datastoreEntryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ datastoreEntry: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: datastoreEntry }));
      saveSubject.complete();

      // THEN
      expect(datastoreEntryFormService.getDatastoreEntry).toHaveBeenCalled();
      expect(datastoreEntryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDatastoreEntry>>();
      const datastoreEntry = { id: 123 };
      jest.spyOn(datastoreEntryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ datastoreEntry });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(datastoreEntryService.update).toHaveBeenCalled();
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
