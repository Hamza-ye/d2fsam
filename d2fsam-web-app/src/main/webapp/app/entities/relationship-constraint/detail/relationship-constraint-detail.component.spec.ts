import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RelationshipConstraintDetailComponent } from './relationship-constraint-detail.component';

describe('RelationshipConstraint Management Detail Component', () => {
  let comp: RelationshipConstraintDetailComponent;
  let fixture: ComponentFixture<RelationshipConstraintDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RelationshipConstraintDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ relationshipConstraint: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RelationshipConstraintDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RelationshipConstraintDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load relationshipConstraint on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.relationshipConstraint).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
