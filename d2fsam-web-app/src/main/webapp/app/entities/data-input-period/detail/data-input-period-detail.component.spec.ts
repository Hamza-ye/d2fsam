import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DataInputPeriodDetailComponent } from './data-input-period-detail.component';

describe('DataInputPeriod Management Detail Component', () => {
  let comp: DataInputPeriodDetailComponent;
  let fixture: ComponentFixture<DataInputPeriodDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataInputPeriodDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ dataInputPeriod: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DataInputPeriodDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DataInputPeriodDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load dataInputPeriod on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.dataInputPeriod).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
