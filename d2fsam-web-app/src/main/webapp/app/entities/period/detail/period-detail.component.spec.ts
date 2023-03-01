import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PeriodDetailComponent } from './period-detail.component';

describe('Period Management Detail Component', () => {
  let comp: PeriodDetailComponent;
  let fixture: ComponentFixture<PeriodDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PeriodDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ period: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PeriodDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PeriodDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load period on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.period).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
