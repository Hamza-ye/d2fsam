import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DemographicDataSourceDetailComponent } from './demographic-data-source-detail.component';

describe('DemographicDataSource Management Detail Component', () => {
  let comp: DemographicDataSourceDetailComponent;
  let fixture: ComponentFixture<DemographicDataSourceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DemographicDataSourceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ demographicDataSource: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DemographicDataSourceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DemographicDataSourceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load demographicDataSource on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.demographicDataSource).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
