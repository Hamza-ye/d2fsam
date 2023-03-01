import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TrackedEntityAttributeValueAuditService } from '../service/tracked-entity-attribute-value-audit.service';

import { TrackedEntityAttributeValueAuditComponent } from './tracked-entity-attribute-value-audit.component';

describe('TrackedEntityAttributeValueAudit Management Component', () => {
  let comp: TrackedEntityAttributeValueAuditComponent;
  let fixture: ComponentFixture<TrackedEntityAttributeValueAuditComponent>;
  let service: TrackedEntityAttributeValueAuditService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'tracked-entity-attribute-value-audit', component: TrackedEntityAttributeValueAuditComponent },
        ]),
        HttpClientTestingModule,
      ],
      declarations: [TrackedEntityAttributeValueAuditComponent],
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
      .overrideTemplate(TrackedEntityAttributeValueAuditComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TrackedEntityAttributeValueAuditComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TrackedEntityAttributeValueAuditService);

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
    expect(comp.trackedEntityAttributeValueAudits?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to trackedEntityAttributeValueAuditService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTrackedEntityAttributeValueAuditIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTrackedEntityAttributeValueAuditIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
