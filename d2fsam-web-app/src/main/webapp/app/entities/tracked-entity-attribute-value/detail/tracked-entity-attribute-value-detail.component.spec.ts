import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TrackedEntityAttributeValueDetailComponent } from './tracked-entity-attribute-value-detail.component';

describe('TrackedEntityAttributeValue Management Detail Component', () => {
  let comp: TrackedEntityAttributeValueDetailComponent;
  let fixture: ComponentFixture<TrackedEntityAttributeValueDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TrackedEntityAttributeValueDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ trackedEntityAttributeValue: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TrackedEntityAttributeValueDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TrackedEntityAttributeValueDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load trackedEntityAttributeValue on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.trackedEntityAttributeValue).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
