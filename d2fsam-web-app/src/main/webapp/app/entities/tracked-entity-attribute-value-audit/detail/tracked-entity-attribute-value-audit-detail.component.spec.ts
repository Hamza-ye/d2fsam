import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TrackedEntityAttributeValueAuditDetailComponent } from './tracked-entity-attribute-value-audit-detail.component';

describe('TrackedEntityAttributeValueAudit Management Detail Component', () => {
  let comp: TrackedEntityAttributeValueAuditDetailComponent;
  let fixture: ComponentFixture<TrackedEntityAttributeValueAuditDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TrackedEntityAttributeValueAuditDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ trackedEntityAttributeValueAudit: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TrackedEntityAttributeValueAuditDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TrackedEntityAttributeValueAuditDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load trackedEntityAttributeValueAudit on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.trackedEntityAttributeValueAudit).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
