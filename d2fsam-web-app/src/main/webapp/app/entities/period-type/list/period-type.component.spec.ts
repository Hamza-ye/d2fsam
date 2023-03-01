import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PeriodTypeService } from '../service/period-type.service';

import { PeriodTypeComponent } from './period-type.component';

describe('PeriodType Management Component', () => {
  let comp: PeriodTypeComponent;
  let fixture: ComponentFixture<PeriodTypeComponent>;
  let service: PeriodTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'period-type', component: PeriodTypeComponent }]), HttpClientTestingModule],
      declarations: [PeriodTypeComponent],
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
      .overrideTemplate(PeriodTypeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PeriodTypeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PeriodTypeService);

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
    expect(comp.periodTypes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to periodTypeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getPeriodTypeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getPeriodTypeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
