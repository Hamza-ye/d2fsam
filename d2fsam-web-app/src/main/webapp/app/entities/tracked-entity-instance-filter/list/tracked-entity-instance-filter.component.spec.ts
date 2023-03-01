import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TrackedEntityInstanceFilterService } from '../service/tracked-entity-instance-filter.service';

import { TrackedEntityInstanceFilterComponent } from './tracked-entity-instance-filter.component';

describe('TrackedEntityInstanceFilter Management Component', () => {
  let comp: TrackedEntityInstanceFilterComponent;
  let fixture: ComponentFixture<TrackedEntityInstanceFilterComponent>;
  let service: TrackedEntityInstanceFilterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'tracked-entity-instance-filter', component: TrackedEntityInstanceFilterComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [TrackedEntityInstanceFilterComponent],
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
      .overrideTemplate(TrackedEntityInstanceFilterComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityInstanceFilterComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TrackedEntityInstanceFilterService);

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
    expect(comp.trackedEntityInstanceFilters?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to trackedEntityInstanceFilterService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTrackedEntityInstanceFilterIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTrackedEntityInstanceFilterIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
