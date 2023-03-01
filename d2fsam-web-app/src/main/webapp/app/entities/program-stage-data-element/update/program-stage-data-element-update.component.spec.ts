import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProgramStageDataElementFormService } from './program-stage-data-element-form.service';
import { ProgramStageDataElementService } from '../service/program-stage-data-element.service';
import { IProgramStageDataElement } from '../program-stage-data-element.model';
import { IProgramStage } from 'app/entities/program-stage/program-stage.model';
import { ProgramStageService } from 'app/entities/program-stage/service/program-stage.service';
import { IDataElement } from 'app/entities/data-element/data-element.model';
import { DataElementService } from 'app/entities/data-element/service/data-element.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ProgramStageDataElementUpdateComponent } from './program-stage-data-element-update.component';

describe('ProgramStageDataElement Management Update Component', () => {
  let comp: ProgramStageDataElementUpdateComponent;
  let fixture: ComponentFixture<ProgramStageDataElementUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let programStageDataElementFormService: ProgramStageDataElementFormService;
  let programStageDataElementService: ProgramStageDataElementService;
  let programStageService: ProgramStageService;
  let dataElementService: DataElementService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProgramStageDataElementUpdateComponent],
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
      .overrideTemplate(ProgramStageDataElementUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramStageDataElementUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    programStageDataElementFormService = TestBed.inject(ProgramStageDataElementFormService);
    programStageDataElementService = TestBed.inject(ProgramStageDataElementService);
    programStageService = TestBed.inject(ProgramStageService);
    dataElementService = TestBed.inject(DataElementService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ProgramStage query and add missing value', () => {
      const programStageDataElement: IProgramStageDataElement = { id: 456 };
      const programStage: IProgramStage = { id: 68596 };
      programStageDataElement.programStage = programStage;

      const programStageCollection: IProgramStage[] = [{ id: 21822 }];
      jest.spyOn(programStageService, 'query').mockReturnValue(of(new HttpResponse({ body: programStageCollection })));
      const additionalProgramStages = [programStage];
      const expectedCollection: IProgramStage[] = [...additionalProgramStages, ...programStageCollection];
      jest.spyOn(programStageService, 'addProgramStageToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStageDataElement });
      comp.ngOnInit();

      expect(programStageService.query).toHaveBeenCalled();
      expect(programStageService.addProgramStageToCollectionIfMissing).toHaveBeenCalledWith(
        programStageCollection,
        ...additionalProgramStages.map(expect.objectContaining)
      );
      expect(comp.programStagesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call DataElement query and add missing value', () => {
      const programStageDataElement: IProgramStageDataElement = { id: 456 };
      const dataElement: IDataElement = { id: 12876 };
      programStageDataElement.dataElement = dataElement;

      const dataElementCollection: IDataElement[] = [{ id: 10846 }];
      jest.spyOn(dataElementService, 'query').mockReturnValue(of(new HttpResponse({ body: dataElementCollection })));
      const additionalDataElements = [dataElement];
      const expectedCollection: IDataElement[] = [...additionalDataElements, ...dataElementCollection];
      jest.spyOn(dataElementService, 'addDataElementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStageDataElement });
      comp.ngOnInit();

      expect(dataElementService.query).toHaveBeenCalled();
      expect(dataElementService.addDataElementToCollectionIfMissing).toHaveBeenCalledWith(
        dataElementCollection,
        ...additionalDataElements.map(expect.objectContaining)
      );
      expect(comp.dataElementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const programStageDataElement: IProgramStageDataElement = { id: 456 };
      const createdBy: IUser = { id: 9451 };
      programStageDataElement.createdBy = createdBy;
      const updatedBy: IUser = { id: 93779 };
      programStageDataElement.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 89885 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ programStageDataElement });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const programStageDataElement: IProgramStageDataElement = { id: 456 };
      const programStage: IProgramStage = { id: 45729 };
      programStageDataElement.programStage = programStage;
      const dataElement: IDataElement = { id: 55289 };
      programStageDataElement.dataElement = dataElement;
      const createdBy: IUser = { id: 98309 };
      programStageDataElement.createdBy = createdBy;
      const updatedBy: IUser = { id: 50273 };
      programStageDataElement.updatedBy = updatedBy;

      activatedRoute.data = of({ programStageDataElement });
      comp.ngOnInit();

      expect(comp.programStagesSharedCollection).toContain(programStage);
      expect(comp.dataElementsSharedCollection).toContain(dataElement);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.programStageDataElement).toEqual(programStageDataElement);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStageDataElement>>();
      const programStageDataElement = { id: 123 };
      jest.spyOn(programStageDataElementFormService, 'getProgramStageDataElement').mockReturnValue(programStageDataElement);
      jest.spyOn(programStageDataElementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStageDataElement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programStageDataElement }));
      saveSubject.complete();

      // THEN
      expect(programStageDataElementFormService.getProgramStageDataElement).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(programStageDataElementService.update).toHaveBeenCalledWith(expect.objectContaining(programStageDataElement));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStageDataElement>>();
      const programStageDataElement = { id: 123 };
      jest.spyOn(programStageDataElementFormService, 'getProgramStageDataElement').mockReturnValue({ id: null });
      jest.spyOn(programStageDataElementService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStageDataElement: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: programStageDataElement }));
      saveSubject.complete();

      // THEN
      expect(programStageDataElementFormService.getProgramStageDataElement).toHaveBeenCalled();
      expect(programStageDataElementService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgramStageDataElement>>();
      const programStageDataElement = { id: 123 };
      jest.spyOn(programStageDataElementService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ programStageDataElement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(programStageDataElementService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProgramStage', () => {
      it('Should forward to programStageService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(programStageService, 'compareProgramStage');
        comp.compareProgramStage(entity, entity2);
        expect(programStageService.compareProgramStage).toHaveBeenCalledWith(entity, entity2);
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
