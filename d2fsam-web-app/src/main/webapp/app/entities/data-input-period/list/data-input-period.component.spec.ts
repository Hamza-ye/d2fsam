import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DataInputPeriodService } from '../service/data-input-period.service';

import { DataInputPeriodComponent } from './data-input-period.component';

describe('DataInputPeriod Management Component', () => {
  let comp: DataInputPeriodComponent;
  let fixture: ComponentFixture<DataInputPeriodComponent>;
  let service: DataInputPeriodService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'data-input-period', component: DataInputPeriodComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [DataInputPeriodComponent],
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
      .overrideTemplate(DataInputPeriodComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DataInputPeriodComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DataInputPeriodService);

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
    expect(comp.dataInputPeriods?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to dataInputPeriodService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getDataInputPeriodIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getDataInputPeriodIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
