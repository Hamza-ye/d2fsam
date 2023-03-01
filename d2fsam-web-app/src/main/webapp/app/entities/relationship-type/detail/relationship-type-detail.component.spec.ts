import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RelationshipTypeDetailComponent } from './relationship-type-detail.component';

describe('RelationshipType Management Detail Component', () => {
  let comp: RelationshipTypeDetailComponent;
  let fixture: ComponentFixture<RelationshipTypeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RelationshipTypeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ relationshipType: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RelationshipTypeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RelationshipTypeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load relationshipType on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.relationshipType).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
