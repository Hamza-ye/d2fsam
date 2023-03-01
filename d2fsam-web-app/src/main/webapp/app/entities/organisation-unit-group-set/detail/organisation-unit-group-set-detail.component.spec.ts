import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrganisationUnitGroupSetDetailComponent } from './organisation-unit-group-set-detail.component';

describe('OrganisationUnitGroupSet Management Detail Component', () => {
  let comp: OrganisationUnitGroupSetDetailComponent;
  let fixture: ComponentFixture<OrganisationUnitGroupSetDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrganisationUnitGroupSetDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ organisationUnitGroupSet: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OrganisationUnitGroupSetDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrganisationUnitGroupSetDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load organisationUnitGroupSet on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.organisationUnitGroupSet).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
