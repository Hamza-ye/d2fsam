import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DataElementFormService } from './data-element-form.service';
import { DataElementService } from '../service/data-element.service';
import { IDataElement } from '../data-element.model';
import { IOptionSet } from 'app/entities/option-set/option-set.model';
import { OptionSetService } from 'app/entities/option-set/service/option-set.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { DataElementUpdateComponent } from './data-element-update.component';

describe('DataElement Management Update Component', () => {
  let comp: DataElementUpdateComponent;
  let fixture: ComponentFixture<DataElementUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let dataElementFormService: DataElementFormService;
  let dataElementService: DataElementService;
  let optionSetService: OptionSetService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DataElementUpdateComponent],
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
      .overrideTemplate(DataElementUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DataElementUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    dataElementFormService = TestBed.inject(DataElementFormService);
    dataElementService = TestBed.inject(DataElementService);
    optionSetService = TestBed.inject(OptionSetService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call OptionSet query and add missing value', () => {
      const dataElement: IDataElement = { id: 456 };
      const optionSet: IOptionSet = { id: 78123 };
      dataElement.optionSet = optionSet;

      const optionSetCollection: IOptionSet[] = [{ id: 44832 }];
      jest.spyOn(optionSetService, 'query').mockReturnValue(of(new HttpResponse({ body: optionSetCollection })));
      const additionalOptionSets = [optionSet];
      const expectedCollection: IOptionSet[] = [...additionalOptionSets, ...optionSetCollection];
      jest.spyOn(optionSetService, 'addOptionSetToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dataElement });
      comp.ngOnInit();

      expect(optionSetService.query).toHaveBeenCalled();
      expect(optionSetService.addOptionSetToCollectionIfMissing).toHaveBeenCalledWith(
        optionSetCollection,
        ...additionalOptionSets.map(expect.objectContaining)
      );
      expect(comp.optionSetsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const dataElement: IDataElement = { id: 456 };
      const createdBy: IUser = { id: 91900 };
      dataElement.createdBy = createdBy;
      const updatedBy: IUser = { id: 20795 };
      dataElement.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 74583 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dataElement });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const dataElement: IDataElement = { id: 456 };
      const optionSet: IOptionSet = { id: 59140 };
      dataElement.optionSet = optionSet;
      const createdBy: IUser = { id: 3360 };
      dataElement.createdBy = createdBy;
      const updatedBy: IUser = { id: 28083 };
      dataElement.updatedBy = updatedBy;

      activatedRoute.data = of({ dataElement });
      comp.ngOnInit();

      expect(comp.optionSetsSharedCollection).toContain(optionSet);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.dataElement).toEqual(dataElement);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDataElement>>();
      const dataElement = { id: 123 };
      jest.spyOn(dataElementFormService, 'getDataElement').mockReturnValue(dataElement);
      jest.spyOn(dataElementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dataElement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dataElement }));
      saveSubject.complete();

      // THEN
      expect(dataElementFormService.getDataElement).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(dataElementService.update).toHaveBeenCalledWith(expect.objectContaining(dataElement));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDataElement>>();
      const dataElement = { id: 123 };
      jest.spyOn(dataElementFormService, 'getDataElement').mockReturnValue({ id: null });
      jest.spyOn(dataElementService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dataElement: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dataElement }));
      saveSubject.complete();

      // THEN
      expect(dataElementFormService.getDataElement).toHaveBeenCalled();
      expect(dataElementService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDataElement>>();
      const dataElement = { id: 123 };
      jest.spyOn(dataElementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dataElement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(dataElementService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareOptionSet', () => {
      it('Should forward to optionSetService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(optionSetService, 'compareOptionSet');
        comp.compareOptionSet(entity, entity2);
        expect(optionSetService.compareOptionSet).toHaveBeenCalledWith(entity, entity2);
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
