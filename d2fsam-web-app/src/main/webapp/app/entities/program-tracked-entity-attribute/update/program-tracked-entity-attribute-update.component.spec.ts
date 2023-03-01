import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProgramTrackedEntityAttributeFormService } from './program-tracked-entity-attribute-form.service';
import { ProgramTrackedEntityAttributeService } from '../service/program-tracked-entity-attribute.service';
import { IProgramTrackedEntityAttribute } from '../program-tracked-entity-attribute.model';
import { IProgram } from 'app/entities/program/program.model';
import { ProgramService } from 'app/entities/program/service/program.service';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ProgramTrackedEntityAttributeUpdateComponent } from './program-tracked-entity-attribute-update.component';

describe('ProgramTrackedEntityAttribute Management Update Component', () => {
  let comp: ProgramTrackedEntityAttributeUpdateComponent;
  let fixture: ComponentFixture<ProgramTrackedEntityAttributeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let programTrackedEntityAttributeFormService: ProgramTrackedEntityAttributeFormService;
  let programTrackedEntityAttributeService: ProgramTrackedEntityAttributeService;
  let programService: ProgramService;
  let dataElementService: DataElementService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProgramTrackedEntityAttributeUpdateComponent],
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
      .overrideTemplate(ProgramTrackedEntityAttributeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramTrackedEntityAttributeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    programTrackedEntityAttributeFormService = TestBed.inject(ProgramTrackedEntityAttributeFormService);
    programTrackedEntityAttributeService = TestBed.inject(ProgramTrackedEntityAttributeService);
    programService = TestBed.inject(ProgramService);
    dataElementService = TestBed.inject(DataElementService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Program query and add missing value', () => {
      const programTrackedEntityAttribute: IProgramTrackedEntityAttribute = { id: 456 };
      const program: IProgram = { id: 91368 };
      programTrackedEntityAttribute.program = program;

      const programCollection: IProgram[] = [{ id: 75184 }];
      jest.spyOn(programService, 'query').mockReturnValue(of(new HttpResponse({ body: programCollection })));
      const additionalPrograms = [program];
      const expectedCollection: IProgram[] = [...additionalPrograms, ...programCollection];
      jest.spyOn(programService, 'addProgramToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programTrackedEntityAttribute });
      comp.ngOnInit();

      expect(programService.query).toHaveBeenCalled();
      expect(programService.addProgramToCollectionIfMissing).toHaveBeenCalledWith(
        programCollection,
        ...additionalPrograms.map(expect.objectContaining)
      );
      expect(comp.programsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call DataElement query and add missing value', () => {
      const programTrackedEntityAttribute: IProgramTrackedEntityAttribute = { id: 456 };
      const attribute: IDataElement = { id: 75130 };
      programTrackedEntityAttribute.attribute = attribute;

      const dataElementCollection: IDataElement[] = [{ id: 4755 }];
      jest.spyOn(dataElementService, 'query').mockReturnValue(of(new HttpResponse({ body: dataElementCollection })));
      const additionalDataElements = [attribute];
      const expectedCollection: IDataElement[] = [...additionalDataElements, ...dataElementCollection];
      jest.spyOn(dataElementService, 'addDataElementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programTrackedEntityAttribute });
      comp.ngOnInit();

      expect(dataElementService.query).toHaveBeenCalled();
      expect(dataElementService.addDataElementToCollectionIfMissing).toHaveBeenCalledWith(
        dataElementCollection,
        ...additionalDataElements.map(expect.objectContaining)
      );
      expect(comp.dataElementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const programTrackedEntityAttribute: IProgramTrackedEntityAttribute = { id: 456 };
      const createdBy: IUser = { id: 90606 };
      programTrackedEntityAttribute.createdBy = createdBy;
      const updatedBy: IUser = { id: 91307 };
      programTrackedEntityAttribute.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 13880 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programTrackedEntityAttribute });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const programTrackedEntityAttribute: IProgramTrackedEntityAttribute = { id: 456 };
      const program: IProgram = { id: 47080 };
      programTrackedEntityAttribute.program = program;
      const attribute: IDataElement = { id: 20132 };
      programTrackedEntityAttribute.attribute = attribute;
      const createdBy: IUser = { id: 51593 };
      programTrackedEntityAttribute.createdBy = createdBy;
      const updatedBy: IUser = { id: 79430 };
      programTrackedEntityAttribute.updatedBy = updatedBy;

      activatedRoute.data = of({ programTrackedEntityAttribute });
      comp.ngOnInit();

      expect(comp.programsSharedCollection).toContain(program);
      expect(comp.dataElementsSharedCollection).toContain(attribute);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.programTrackedEntityAttribute).toEqual(programTrackedEntityAttribute);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramTrackedEntityAttribute>>();
      const programTrackedEntityAttribute = { id: 123 };
      jest
        .spyOn(programTrackedEntityAttributeFormService, 'getProgramTrackedEntityAttribute')
        .mockReturnValue(programTrackedEntityAttribute);
      jest.spyOn(programTrackedEntityAttributeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programTrackedEntityAttribute });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programTrackedEntityAttribute }));
      saveSubject.complete();

      // THEN
      expect(programTrackedEntityAttributeFormService.getProgramTrackedEntityAttribute).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(programTrackedEntityAttributeService.update).toHaveBeenCalledWith(expect.objectContaining(programTrackedEntityAttribute));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramTrackedEntityAttribute>>();
      const programTrackedEntityAttribute = { id: 123 };
      jest.spyOn(programTrackedEntityAttributeFormService, 'getProgramTrackedEntityAttribute').mockReturnValue({ id: null });
      jest.spyOn(programTrackedEntityAttributeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programTrackedEntityAttribute: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programTrackedEntityAttribute }));
      saveSubject.complete();

      // THEN
      expect(programTrackedEntityAttributeFormService.getProgramTrackedEntityAttribute).toHaveBeenCalled();
      expect(programTrackedEntityAttributeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramTrackedEntityAttribute>>();
      const programTrackedEntityAttribute = { id: 123 };
      jest.spyOn(programTrackedEntityAttributeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programTrackedEntityAttribute });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(programTrackedEntityAttributeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProgram', () => {
      it('Should forward to programService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(programService, 'compareProgram');
        comp.compareProgram(entity, entity2);
        expect(programService.compareProgram).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareDataElement', () => {
      it('Should forward to dataElementService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(dataElementService, 'compareDataElement');
        comp.compareDataElement(entity, entity2);
        expect(dataElementService.compareDataElement).toHaveBeenCalledWith(entity, entity2);
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
