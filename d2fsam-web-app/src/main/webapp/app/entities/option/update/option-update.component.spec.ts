import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OptionFormService } from './option-form.service';
import { OptionService } from '../service/option.service';
import { IOption } from '../option.model';
import { IOptionSet } from 'app/entities/option-set/option-set.model';
import { OptionSetService } from 'app/entities/option-set/service/option-set.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { OptionUpdateComponent } from './option-update.component';

describe('Option Management Update Component', () => {
  let comp: OptionUpdateComponent;
  let fixture: ComponentFixture<OptionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let optionFormService: OptionFormService;
  let optionService: OptionService;
  let optionSetService: OptionSetService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OptionUpdateComponent],
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
      .overrideTemplate(OptionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OptionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    optionFormService = TestBed.inject(OptionFormService);
    optionService = TestBed.inject(OptionService);
    optionSetService = TestBed.inject(OptionSetService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call OptionSet query and add missing value', () => {
      const option: IOption = { id: 456 };
      const optionSet: IOptionSet = { id: 6781 };
      option.optionSet = optionSet;

      const optionSetCollection: IOptionSet[] = [{ id: 44462 }];
      jest.spyOn(optionSetService, 'query').mockReturnValue(of(new HttpResponse({ body: optionSetCollection })));
      const additionalOptionSets = [optionSet];
      const expectedCollection: IOptionSet[] = [...additionalOptionSets, ...optionSetCollection];
      jest.spyOn(optionSetService, 'addOptionSetToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ option });
      comp.ngOnInit();

      expect(optionSetService.query).toHaveBeenCalled();
      expect(optionSetService.addOptionSetToCollectionIfMissing).toHaveBeenCalledWith(
        optionSetCollection,
        ...additionalOptionSets.map(expect.objectContaining)
      );
      expect(comp.optionSetsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const option: IOption = { id: 456 };
      const createdBy: IUser = { id: 84858 };
      option.createdBy = createdBy;
      const updatedBy: IUser = { id: 90215 };
      option.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 20485 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ option });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const option: IOption = { id: 456 };
      const optionSet: IOptionSet = { id: 29649 };
      option.optionSet = optionSet;
      const createdBy: IUser = { id: 63572 };
      option.createdBy = createdBy;
      const updatedBy: IUser = { id: 32254 };
      option.updatedBy = updatedBy;

      activatedRoute.data = of({ option });
      comp.ngOnInit();

      expect(comp.optionSetsSharedCollection).toContain(optionSet);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.option).toEqual(option);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOption>>();
      const option = { id: 123 };
      jest.spyOn(optionFormService, 'getOption').mockReturnValue(option);
      jest.spyOn(optionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ option });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: option }));
      saveSubject.complete();

      // THEN
      expect(optionFormService.getOption).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(optionService.update).toHaveBeenCalledWith(expect.objectContaining(option));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOption>>();
      const option = { id: 123 };
      jest.spyOn(optionFormService, 'getOption').mockReturnValue({ id: null });
      jest.spyOn(optionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ option: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: option }));
      saveSubject.complete();

      // THEN
      expect(optionFormService.getOption).toHaveBeenCalled();
      expect(optionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOption>>();
      const option = { id: 123 };
      jest.spyOn(optionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ option });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(optionService.update).toHaveBeenCalled();
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
