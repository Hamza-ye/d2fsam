import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrganisationUnitGroupDetailComponent } from './organisation-unit-group-detail.component';

describe('OrganisationUnitGroup Management Detail Component', () => {
  let comp: OrganisationUnitGroupDetailComponent;
  let fixture: ComponentFixture<OrganisationUnitGroupDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrganisationUnitGroupDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ organisationUnitGroup: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OrganisationUnitGroupDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrganisationUnitGroupDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load organisationUnitGroup on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.organisationUnitGroup).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
