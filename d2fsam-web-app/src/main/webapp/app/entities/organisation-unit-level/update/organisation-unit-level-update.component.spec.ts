import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrganisationUnitLevelFormService } from './organisation-unit-level-form.service';
import { OrganisationUnitLevelService } from '../service/organisation-unit-level.service';
import { IOrganisationUnitLevel } from '../organisation-unit-level.model';

import { OrganisationUnitLevelUpdateComponent } from './organisation-unit-level-update.component';

describe('OrganisationUnitLevel Management Update Component', () => {
  let comp: OrganisationUnitLevelUpdateComponent;
  let fixture: ComponentFixture<OrganisationUnitLevelUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let organisationUnitLevelFormService: OrganisationUnitLevelFormService;
  let organisationUnitLevelService: OrganisationUnitLevelService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrganisationUnitLevelUpdateComponent],
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
      .overrideTemplate(OrganisationUnitLevelUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrganisationUnitLevelUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    organisationUnitLevelFormService = TestBed.inject(OrganisationUnitLevelFormService);
    organisationUnitLevelService = TestBed.inject(OrganisationUnitLevelService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const organisationUnitLevel: IOrganisationUnitLevel = { id: 456 };

      activatedRoute.data = of({ organisationUnitLevel });
      comp.ngOnInit();

      expect(comp.organisationUnitLevel).toEqual(organisationUnitLevel);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnitLevel>>();
      const organisationUnitLevel = { id: 123 };
      jest.spyOn(organisationUnitLevelFormService, 'getOrganisationUnitLevel').mockReturnValue(organisationUnitLevel);
      jest.spyOn(organisationUnitLevelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnitLevel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisationUnitLevel }));
      saveSubject.complete();

      // THEN
      expect(organisationUnitLevelFormService.getOrganisationUnitLevel).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(organisationUnitLevelService.update).toHaveBeenCalledWith(expect.objectContaining(organisationUnitLevel));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnitLevel>>();
      const organisationUnitLevel = { id: 123 };
      jest.spyOn(organisationUnitLevelFormService, 'getOrganisationUnitLevel').mockReturnValue({ id: null });
      jest.spyOn(organisationUnitLevelService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnitLevel: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: organisationUnitLevel }));
      saveSubject.complete();

      // THEN
      expect(organisationUnitLevelFormService.getOrganisationUnitLevel).toHaveBeenCalled();
      expect(organisationUnitLevelService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrganisationUnitLevel>>();
      const organisationUnitLevel = { id: 123 };
      jest.spyOn(organisationUnitLevelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ organisationUnitLevel });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(organisationUnitLevelService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
