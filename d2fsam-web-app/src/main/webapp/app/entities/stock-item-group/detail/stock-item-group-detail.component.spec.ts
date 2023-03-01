import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { StockItemGroupDetailComponent } from './stock-item-group-detail.component';

describe('StockItemGroup Management Detail Component', () => {
  let comp: StockItemGroupDetailComponent;
  let fixture: ComponentFixture<StockItemGroupDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StockItemGroupDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ stockItemGroup: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(StockItemGroupDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(StockItemGroupDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load stockItemGroup on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.stockItemGroup).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
