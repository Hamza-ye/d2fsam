import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TrackedEntityInstanceAuditDetailComponent } from './tracked-entity-instance-audit-detail.component';

describe('TrackedEntityInstanceAudit Management Detail Component', () => {
  let comp: TrackedEntityInstanceAuditDetailComponent;
  let fixture: ComponentFixture<TrackedEntityInstanceAuditDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TrackedEntityInstanceAuditDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ trackedEntityInstanceAudit: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TrackedEntityInstanceAuditDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TrackedEntityInstanceAuditDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load trackedEntityInstanceAudit on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.trackedEntityInstanceAudit).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
