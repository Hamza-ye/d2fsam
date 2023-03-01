import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TrackedEntityInstanceFilterDetailComponent } from './tracked-entity-instance-filter-detail.component';

describe('TrackedEntityInstanceFilter Management Detail Component', () => {
  let comp: TrackedEntityInstanceFilterDetailComponent;
  let fixture: ComponentFixture<TrackedEntityInstanceFilterDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TrackedEntityInstanceFilterDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ trackedEntityInstanceFilter: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TrackedEntityInstanceFilterDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TrackedEntityInstanceFilterDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load trackedEntityInstanceFilter on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.trackedEntityInstanceFilter).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
