import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RelativePeriodsDetailComponent } from './relative-periods-detail.component';

describe('RelativePeriods Management Detail Component', () => {
  let comp: RelativePeriodsDetailComponent;
  let fixture: ComponentFixture<RelativePeriodsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RelativePeriodsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ relativePeriods: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RelativePeriodsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RelativePeriodsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load relativePeriods on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.relativePeriods).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
