import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrganisationUnitDetailComponent } from './organisation-unit-detail.component';

describe('OrganisationUnit Management Detail Component', () => {
  let comp: OrganisationUnitDetailComponent;
  let fixture: ComponentFixture<OrganisationUnitDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrganisationUnitDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ organisationUnit: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OrganisationUnitDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrganisationUnitDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load organisationUnit on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.organisationUnit).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
