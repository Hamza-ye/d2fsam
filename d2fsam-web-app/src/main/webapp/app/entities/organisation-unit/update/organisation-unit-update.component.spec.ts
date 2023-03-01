import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrganisationUnitFormService } from './organisation-unit-form.service';
import { OrganisationUnitService } from '../service/organisation-unit.service';
import { IOrganisationUnit } from '../organisation-unit.model';
import { IFileResource } from 'app/entities/file-resource/file-resource.model';
import { FileResourceService } from 'app/entities/file-resource/service/file-resource.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IChv } from 'app/entities/chv/chv.model';
import { ChvService } from 'app/entities/chv/service/chv.service';

import { OrganisationUnitUpdateComponent } from './organisation-unit-update.component';

describe('OrganisationUnit Management Update Component', () => {
  let comp: OrganisationUnitUpdateComponent;
  let fixture: ComponentFixture<OrganisationUnitUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let organisationUnitFormService: OrganisationUnitFormService;
  let organisationUnitService: OrganisationUnitService;
  let fileResourceService: FileResourceService;
  let userService: UserService;
  let chvService: ChvService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrganisationUnitUpdateComponent],
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
      .overrideTemplate(OrganisationUnitUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrganisationUnitUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    organisationUnitFormService = TestBed.inject(OrganisationUnitFormService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);
    fileResourceService = TestBed.inject(FileResourceService);
    userService = TestBed.inject(UserService);
    chvService = TestBed.inject(ChvService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call OrganisationUnit query and add missing value', () => {
      const organisationUnit: IOrganisationUnit = { id: 456 };
      const parent: IOrganisationUnit = { id: 71497 };
      organisationUnit.parent = parent;
      const hfHomeSubVillage: IOrganisationUnit = { id: 95615 };
      organisationUnit.hfHomeSubVillage = hfHomeSubVillage;
      const servicingHf: IOrganisationUnit = { id: 85775 };
      organisationUnit.servicingHf = servicingHf;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 70094 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [parent, hfHomeSubVillage, servicingHf];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ organisationUnit });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call FileResource query and add missing value', () => {
      const organisationUnit: IOrganisationUnit = { id: 456 };
      const image: IFileResource = { id: 42187 };
      organisationUnit.image = image;

      const fileResourceCollection: IFileResource[] = [{ id: 5356 }];
      jest.spyOn(fileResourceService, 'query').mockReturnValue(of(new HttpResponse({ body: fileResourceCollection })));
      const additionalFileResources = [image];
      const expectedCollection: IFileResource[] = [...additionalFileResources, ...fileResourceCollection];
      jest.spyOn(fileResourceService, 'addFileResourceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ organisationUnit });
      comp.ngOnInit();

      expect(fileResourceService.query).toHaveBeenCalled();
      expect(fileResourceService.addFileResourceToCollectionIfMissing).toHaveBeenCalledWith(
        fileResourceCollection,
        ...additionalFileResources.map(expect.objectContaining)
      );
      expect(comp.fileResourcesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const organisationUnit: IOrganisationUnit = { id: 456 };
      const createdBy: IUser = { id: 21694 };
      organisationUnit.createdBy = createdBy;
      const updatedBy: IUser = { id: 90199 };
      organisationUnit.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 55424 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ organisationUnit });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Chv query and add missing value', () => {
      const organisationUnit: IOrganisationUnit = { id: 456 };
      const assignedChv: IChv = { id: 76826 };
      organisationUnit.assignedChv = assignedChv;

      const chvCollection: IChv[] = [{ id: 82396 }];
      jest.spyOn(chvService, 'query').mockReturnValue(of(new HttpResponse({ body: chvCollection })));
      const additionalChvs = [assignedChv];
      const expectedCollection: IChv[] = [...additionalChvs, ...chvCollection];
      jest.spyOn(chvService, 'addChvToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ organisationUnit });
      comp.ngOnInit();

      expect(chvService.query).toHaveBeenCalled();
      expect(chvService.addChvToCollectionIfMissing).toHaveBeenCalledWith(chvCollection, ...additionalChvs.map(expect.objectContaining));
      expect(comp.chvsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const organisationUnit: IOrganisationUnit = { id: 456 };
      const parent: IOrganisationUnit = { id: 86550 };
      organisationUnit.parent = parent;
      const hfHomeSubVillage: IOrganisationUnit = { id: 67176 };
      organisationUnit.hfHomeSubVillage = hfHomeSubVillage;
      const servicingHf: IOrganisationUnit = { id: 87812 };
      organisationUnit.servicingHf = servicingHf;
      const image: IFileResource = { id: 90665 };
      organisationUnit.image = image;
      const createdBy: IUser = { id: 71255 };
      organisationUnit.createdBy = createdBy;
      const updatedBy: IUser = { id: 15215 };
      organisationUnit.updatedBy = updatedBy;
      const assignedChv: IChv = { id: 43173 };
      organisationUnit.assignedChv = assignedChv;

      activatedRoute.data = of({ organisationUnit });
      comp.ngOnInit();

      expect(comp.organisationUnitsSharedCollection).toContain(parent);
      expect(comp.organisationUnitsSharedCollection).toContain(hfHomeSubVillage);
      expect(comp.organisationUnitsSharedCollection).toContain(servicingHf);
      expect(comp.fileResourcesSharedCollection).toContain(image);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.chvsSharedCollection).toContain(assignedChv);
      expect(comp.organisationUnit).toEqual(organisationUnit);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnit>>();
      const organisationUnit = { id: 123 };
      jest.spyOn(organisationUnitFormService, 'getOrganisationUnit').mockReturnValue(organisationUnit);
      jest.spyOn(organisationUnitService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisationUnit }));
      saveSubject.complete();

      // THEN
      expect(organisationUnitFormService.getOrganisationUnit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(organisationUnitService.update).toHaveBeenCalledWith(expect.objectContaining(organisationUnit));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnit>>();
      const organisationUnit = { id: 123 };
      jest.spyOn(organisationUnitFormService, 'getOrganisationUnit').mockReturnValue({ id: null });
      jest.spyOn(organisationUnitService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisationUnit }));
      saveSubject.complete();

      // THEN
      expect(organisationUnitFormService.getOrganisationUnit).toHaveBeenCalled();
      expect(organisationUnitService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnit>>();
      const organisationUnit = { id: 123 };
      jest.spyOn(organisationUnitService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(organisationUnitService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareOrganisationUnit', () => {
      it('Should forward to organisationUnitService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(organisationUnitService, 'compareOrganisationUnit');
        comp.compareOrganisationUnit(entity, entity2);
        expect(organisationUnitService.compareOrganisationUnit).toHaveBeenCalledWith(entity, entity2);
      });
    });

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

    describe('compareChv', () => {
      it('Should forward to chvService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(chvService, 'compareChv');
        comp.compareChv(entity, entity2);
        expect(chvService.compareChv).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
