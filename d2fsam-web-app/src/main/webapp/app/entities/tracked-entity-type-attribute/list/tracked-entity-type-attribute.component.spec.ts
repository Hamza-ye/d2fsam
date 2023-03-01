import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TrackedEntityTypeAttributeService } from '../service/tracked-entity-type-attribute.service';

import { TrackedEntityTypeAttributeComponent } from './tracked-entity-type-attribute.component';

describe('TrackedEntityTypeAttribute Management Component', () => {
  let comp: TrackedEntityTypeAttributeComponent;
  let fixture: ComponentFixture<TrackedEntityTypeAttributeComponent>;
  let service: TrackedEntityTypeAttributeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'tracked-entity-type-attribute', component: TrackedEntityTypeAttributeComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [TrackedEntityTypeAttributeComponent],
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
      .overrideTemplate(TrackedEntityTypeAttributeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityTypeAttributeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TrackedEntityTypeAttributeService);

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
    expect(comp.trackedEntityTypeAttributes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to trackedEntityTypeAttributeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTrackedEntityTypeAttributeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTrackedEntityTypeAttributeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
