import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DemographicDataDetailComponent } from './demographic-data-detail.component';

describe('DemographicData Management Detail Component', () => {
  let comp: DemographicDataDetailComponent;
  let fixture: ComponentFixture<DemographicDataDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DemographicDataDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ demographicData: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DemographicDataDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DemographicDataDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load demographicData on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.demographicData).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
