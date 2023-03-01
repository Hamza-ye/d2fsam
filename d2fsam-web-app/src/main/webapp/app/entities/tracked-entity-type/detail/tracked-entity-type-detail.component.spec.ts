import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TrackedEntityTypeDetailComponent } from './tracked-entity-type-detail.component';

describe('TrackedEntityType Management Detail Component', () => {
  let comp: TrackedEntityTypeDetailComponent;
  let fixture: ComponentFixture<TrackedEntityTypeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TrackedEntityTypeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ trackedEntityType: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TrackedEntityTypeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TrackedEntityTypeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load trackedEntityType on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.trackedEntityType).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
