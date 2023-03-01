import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DataValueDetailComponent } from './data-value-detail.component';

describe('DataValue Management Detail Component', () => {
  let comp: DataValueDetailComponent;
  let fixture: ComponentFixture<DataValueDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataValueDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ dataValue: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DataValueDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DataValueDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load dataValue on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.dataValue).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
