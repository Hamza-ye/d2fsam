import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PeriodService } from '../service/period.service';

import { PeriodComponent } from './period.component';

describe('Period Management Component', () => {
  let comp: PeriodComponent;
  let fixture: ComponentFixture<PeriodComponent>;
  let service: PeriodService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'period', component: PeriodComponent }]), HttpClientTestingModule],
      declarations: [PeriodComponent],
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
      .overrideTemplate(PeriodComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PeriodComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PeriodService);

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
    expect(comp.periods?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to periodService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getPeriodIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getPeriodIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
