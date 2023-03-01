import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DataElementDetailComponent } from './data-element-detail.component';

describe('DataElement Management Detail Component', () => {
  let comp: DataElementDetailComponent;
  let fixture: ComponentFixture<DataElementDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DataElementDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ dataElement: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DataElementDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DataElementDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load dataElement on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.dataElement).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
