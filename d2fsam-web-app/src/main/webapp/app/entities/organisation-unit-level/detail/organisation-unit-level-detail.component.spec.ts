import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrganisationUnitLevelDetailComponent } from './organisation-unit-level-detail.component';

describe('OrganisationUnitLevel Management Detail Component', () => {
  let comp: OrganisationUnitLevelDetailComponent;
  let fixture: ComponentFixture<OrganisationUnitLevelDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrganisationUnitLevelDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ organisationUnitLevel: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OrganisationUnitLevelDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrganisationUnitLevelDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load organisationUnitLevel on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.organisationUnitLevel).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
