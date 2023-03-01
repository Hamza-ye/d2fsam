import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TrackedEntityInstanceDetailComponent } from './tracked-entity-instance-detail.component';

describe('TrackedEntityInstance Management Detail Component', () => {
  let comp: TrackedEntityInstanceDetailComponent;
  let fixture: ComponentFixture<TrackedEntityInstanceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TrackedEntityInstanceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ trackedEntityInstance: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TrackedEntityInstanceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TrackedEntityInstanceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load trackedEntityInstance on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.trackedEntityInstance).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
