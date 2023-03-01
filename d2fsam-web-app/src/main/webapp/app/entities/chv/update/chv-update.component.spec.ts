import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ChvFormService } from './chv-form.service';
import { ChvService } from '../service/chv.service';
import { IChv } from '../chv.model';
import { IUserData } from 'app/entities/user-data/user-data.model';
import { UserDataService } from 'app/entities/user-data/service/user-data.service';
import { IOrganisationUnit } from 'app/entities/organisation-unit/organisation-unit.model';
import { OrganisationUnitService } from 'app/entities/organisation-unit/service/organisation-unit.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ChvUpdateComponent } from './chv-update.component';

describe('Chv Management Update Component', () => {
  let comp: ChvUpdateComponent;
  let fixture: ComponentFixture<ChvUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let chvFormService: ChvFormService;
  let chvService: ChvService;
  let userDataService: UserDataService;
  let organisationUnitService: OrganisationUnitService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ChvUpdateComponent],
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
      .overrideTemplate(ChvUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ChvUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    chvFormService = TestBed.inject(ChvFormService);
    chvService = TestBed.inject(ChvService);
    userDataService = TestBed.inject(UserDataService);
    organisationUnitService = TestBed.inject(OrganisationUnitService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call assignedTo query and add missing value', () => {
      const chv: IChv = { id: 456 };
      const assignedTo: IUserData = { id: 76165 };
      chv.assignedTo = assignedTo;

      const assignedToCollection: IUserData[] = [{ id: 4933 }];
      jest.spyOn(userDataService, 'query').mockReturnValue(of(new HttpResponse({ body: assignedToCollection })));
      const expectedCollection: IUserData[] = [assignedTo, ...assignedToCollection];
      jest.spyOn(userDataService, 'addUserDataToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ chv });
      comp.ngOnInit();

      expect(userDataService.query).toHaveBeenCalled();
      expect(userDataService.addUserDataToCollectionIfMissing).toHaveBeenCalledWith(assignedToCollection, assignedTo);
      expect(comp.assignedTosCollection).toEqual(expectedCollection);
    });

    it('Should call OrganisationUnit query and add missing value', () => {
      const chv: IChv = { id: 456 };
      const district: IOrganisationUnit = { id: 59321 };
      chv.district = district;
      const homeSubvillage: IOrganisationUnit = { id: 35786 };
      chv.homeSubvillage = homeSubvillage;
      const managingHf: IOrganisationUnit = { id: 27546 };
      chv.managingHf = managingHf;

      const organisationUnitCollection: IOrganisationUnit[] = [{ id: 27495 }];
      jest.spyOn(organisationUnitService, 'query').mockReturnValue(of(new HttpResponse({ body: organisationUnitCollection })));
      const additionalOrganisationUnits = [district, homeSubvillage, managingHf];
      const expectedCollection: IOrganisationUnit[] = [...additionalOrganisationUnits, ...organisationUnitCollection];
      jest.spyOn(organisationUnitService, 'addOrganisationUnitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ chv });
      comp.ngOnInit();

      expect(organisationUnitService.query).toHaveBeenCalled();
      expect(organisationUnitService.addOrganisationUnitToCollectionIfMissing).toHaveBeenCalledWith(
        organisationUnitCollection,
        ...additionalOrganisationUnits.map(expect.objectContaining)
      );
      expect(comp.organisationUnitsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const chv: IChv = { id: 456 };
      const createdBy: IUser = { id: 44806 };
      chv.createdBy = createdBy;
      const updatedBy: IUser = { id: 81668 };
      chv.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 22554 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ chv });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const chv: IChv = { id: 456 };
      const assignedTo: IUserData = { id: 92876 };
      chv.assignedTo = assignedTo;
      const district: IOrganisationUnit = { id: 17822 };
      chv.district = district;
      const homeSubvillage: IOrganisationUnit = { id: 55557 };
      chv.homeSubvillage = homeSubvillage;
      const managingHf: IOrganisationUnit = { id: 49632 };
      chv.managingHf = managingHf;
      const createdBy: IUser = { id: 7006 };
      chv.createdBy = createdBy;
      const updatedBy: IUser = { id: 94159 };
      chv.updatedBy = updatedBy;

      activatedRoute.data = of({ chv });
      comp.ngOnInit();

      expect(comp.assignedTosCollection).toContain(assignedTo);
      expect(comp.organisationUnitsSharedCollection).toContain(district);
      expect(comp.organisationUnitsSharedCollection).toContain(homeSubvillage);
      expect(comp.organisationUnitsSharedCollection).toContain(managingHf);
      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.chv).toEqual(chv);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IChv>>();
      const chv = { id: 123 };
      jest.spyOn(chvFormService, 'getChv').mockReturnValue(chv);
      jest.spyOn(chvService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ chv });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: chv }));
      saveSubject.complete();

      // THEN
      expect(chvFormService.getChv).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(chvService.update).toHaveBeenCalledWith(expect.objectContaining(chv));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IChv>>();
      const chv = { id: 123 };
      jest.spyOn(chvFormService, 'getChv').mockReturnValue({ id: null });
      jest.spyOn(chvService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ chv: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: chv }));
      saveSubject.complete();

      // THEN
      expect(chvFormService.getChv).toHaveBeenCalled();
      expect(chvService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IChv>>();
      const chv = { id: 123 };
      jest.spyOn(chvService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ chv });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(chvService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUserData', () => {
      it('Should forward to userDataService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userDataService, 'compareUserData');
        comp.compareUserData(entity, entity2);
        expect(userDataService.compareUserData).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
  });
});
