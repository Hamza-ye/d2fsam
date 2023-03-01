import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PeriodTypeDetailComponent } from './period-type-detail.component';

describe('PeriodType Management Detail Component', () => {
  let comp: PeriodTypeDetailComponent;
  let fixture: ComponentFixture<PeriodTypeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PeriodTypeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ periodType: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PeriodTypeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PeriodTypeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load periodType on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.periodType).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
