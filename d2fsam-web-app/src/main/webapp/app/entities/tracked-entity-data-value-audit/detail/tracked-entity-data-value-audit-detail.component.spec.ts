import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TrackedEntityDataValueAuditDetailComponent } from './tracked-entity-data-value-audit-detail.component';

describe('TrackedEntityDataValueAudit Management Detail Component', () => {
  let comp: TrackedEntityDataValueAuditDetailComponent;
  let fixture: ComponentFixture<TrackedEntityDataValueAuditDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TrackedEntityDataValueAuditDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ trackedEntityDataValueAudit: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TrackedEntityDataValueAuditDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TrackedEntityDataValueAuditDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load trackedEntityDataValueAudit on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.trackedEntityDataValueAudit).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
