import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { StockItemGroupService } from '../service/stock-item-group.service';

import { StockItemGroupComponent } from './stock-item-group.component';

describe('StockItemGroup Management Component', () => {
  let comp: StockItemGroupComponent;
  let fixture: ComponentFixture<StockItemGroupComponent>;
  let service: StockItemGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'stock-item-group', component: StockItemGroupComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [StockItemGroupComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(StockItemGroupComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StockItemGroupComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(StockItemGroupService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.stockItemGroups?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to stockItemGroupService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getStockItemGroupIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getStockItemGroupIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
