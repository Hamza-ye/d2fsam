import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TrackedEntityProgramOwnerDetailComponent } from './tracked-entity-program-owner-detail.component';

describe('TrackedEntityProgramOwner Management Detail Component', () => {
  let comp: TrackedEntityProgramOwnerDetailComponent;
  let fixture: ComponentFixture<TrackedEntityProgramOwnerDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TrackedEntityProgramOwnerDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ trackedEntityProgramOwner: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TrackedEntityProgramOwnerDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TrackedEntityProgramOwnerDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load trackedEntityProgramOwner on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.trackedEntityProgramOwner).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
