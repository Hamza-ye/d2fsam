import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RelativePeriodsService } from '../service/relative-periods.service';

import { RelativePeriodsComponent } from './relative-periods.component';

describe('RelativePeriods Management Component', () => {
  let comp: RelativePeriodsComponent;
  let fixture: ComponentFixture<RelativePeriodsComponent>;
  let service: RelativePeriodsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'relative-periods', component: RelativePeriodsComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [RelativePeriodsComponent],
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
      .overrideTemplate(RelativePeriodsComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelativePeriodsComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RelativePeriodsService);

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
    expect(comp.relativePeriods?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to relativePeriodsService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getRelativePeriodsIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getRelativePeriodsIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
