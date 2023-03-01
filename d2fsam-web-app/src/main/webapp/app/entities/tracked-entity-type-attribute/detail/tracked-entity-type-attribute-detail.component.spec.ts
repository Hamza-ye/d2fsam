import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TrackedEntityTypeAttributeDetailComponent } from './tracked-entity-type-attribute-detail.component';

describe('TrackedEntityTypeAttribute Management Detail Component', () => {
  let comp: TrackedEntityTypeAttributeDetailComponent;
  let fixture: ComponentFixture<TrackedEntityTypeAttributeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TrackedEntityTypeAttributeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ trackedEntityTypeAttribute: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TrackedEntityTypeAttributeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TrackedEntityTypeAttributeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load trackedEntityTypeAttribute on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.trackedEntityTypeAttribute).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
