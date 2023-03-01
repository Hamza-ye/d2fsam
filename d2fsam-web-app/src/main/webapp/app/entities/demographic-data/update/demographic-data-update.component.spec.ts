import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DemographicDataFormService } from './demographic-data-form.service';
import { DemographicDataService } from '../service/demographic-data.service';
import { IDemographicData } from '../demographic-data.model';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IDemographicDataSource } from 'app/entities/demographic-data-source/demographic-data-source.model';
import { DemographicDataSourceService } from 'app/entities/demographic-data-source/service/demographic-data-source.service';

import { DemographicDataUpdateComponent } from './demographic-data-update.component';

describe('DemographicData Management Update Component', () => {
  let comp: DemographicDataUpdateComponent;
  let fixture: ComponentFixture<DemographicDataUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let demographicDataFormService: DemographicDataFormService;
  let demographicDataService: DemographicDataService;
  let organisationUnitService: OrganisationUnitService;
  let userService: UserService;
  let demographicDataSourceService: DemographicDataSourceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DemographicDataUpdateComponent],
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
      .overrideTemplate(DemographicDataUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DemographicDataUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    demographicDataFormService = TestBed.inject(DemographicDataFormService);
    demographicDataService = TestBed.inject(DemographicDataService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);
    userService = TestBed.inject(UserService);
    demographicDataSourceService = TestBed.inject(DemographicDataSourceService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call OrganisationUnit query and add missing value', () => {
      const demographicData: IDemographicData = { id: 456 };
      const organisationUnit: IOrganisationUnit = { id: 1939 };
      demographicData.organisationUnit = organisationUnit;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 96230 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [organisationUnit];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ demographicData });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const demographicData: IDemographicData = { id: 456 };
      const createdBy: IUser = { id: 56296 };
      demographicData.createdBy = createdBy;
      const updatedBy: IUser = { id: 78577 };
      demographicData.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 28649 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ demographicData });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call DemographicDataSource query and add missing value', () => {
      const demographicData: IDemographicData = { id: 456 };
      const source: IDemographicDataSource = { id: 93995 };
      demographicData.source = source;

      const demographicDataSourceCollection: IDemographicDataSource[] = [{ id: 53923 }];
      jest.spyOn(demographicDataSourceService, 'query').mockReturnValue(of(new HttpResponse({ body: demographicDataSourceCollection })));
      const additionalDemographicDataSources = [source];
      const expectedCollection: IDemographicDataSource[] = [...additionalDemographicDataSources, ...demographicDataSourceCollection];
      jest.spyOn(demographicDataSourceService, 'addDemographicDataSourceToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ demographicData });
      comp.ngOnInit();

      expect(demographicDataSourceService.query).toHaveBeenCalled();
      expect(demographicDataSourceService.addDemographicDataSourceToCollectionIfMissing).toHaveBeenCalledWith(
        demographicDataSourceCollection,
        ...additionalDemographicDataSources.map(expect.objectContaining)
      );
      expect(comp.demographicDataSourcesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const demographicData: IDemographicData = { id: 456 };
      const organisationUnit: IOrganisationUnit = { id: 36331 };
      demographicData.organisationUnit = organisationUnit;
      const createdBy: IUser = { id: 900 };
      demographicData.createdBy = createdBy;
      const updatedBy: IUser = { id: 45158 };
      demographicData.updatedBy = updatedBy;
      const source: IDemographicDataSource = { id: 15129 };
      demographicData.source = source;

      activatedRoute.data = of({ demographicData });
      comp.ngOnInit();

      expect(comp.organisationUnitsSharedCollection).toContain(organisationUnit);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.demographicDataSourcesSharedCollection).toContain(source);
      expect(comp.demographicData).toEqual(demographicData);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDemographicData>>();
      const demographicData = { id: 123 };
      jest.spyOn(demographicDataFormService, 'getDemographicData').mockReturnValue(demographicData);
      jest.spyOn(demographicDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demographicData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: demographicData }));
      saveSubject.complete();

      // THEN
      expect(demographicDataFormService.getDemographicData).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(demographicDataService.update).toHaveBeenCalledWith(expect.objectContaining(demographicData));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDemographicData>>();
      const demographicData = { id: 123 };
      jest.spyOn(demographicDataFormService, 'getDemographicData').mockReturnValue({ id: null });
      jest.spyOn(demographicDataService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demographicData: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: demographicData }));
      saveSubject.complete();

      // THEN
      expect(demographicDataFormService.getDemographicData).toHaveBeenCalled();
      expect(demographicDataService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDemographicData>>();
      const demographicData = { id: 123 };
      jest.spyOn(demographicDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ demographicData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(demographicDataService.update).toHaveBeenCalled();
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

    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareDemographicDataSource', () => {
      it('Should forward to demographicDataSourceService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(demographicDataSourceService, 'compareDemographicDataSource');
        comp.compareDemographicDataSource(entity, entity2);
        expect(demographicDataSourceService.compareDemographicDataSource).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
