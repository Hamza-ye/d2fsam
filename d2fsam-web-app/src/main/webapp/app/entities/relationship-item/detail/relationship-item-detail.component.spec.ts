import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RelationshipItemDetailComponent } from './relationship-item-detail.component';

describe('RelationshipItem Management Detail Component', () => {
  let comp: RelationshipItemDetailComponent;
  let fixture: ComponentFixture<RelationshipItemDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RelationshipItemDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ relationshipItem: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RelationshipItemDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RelationshipItemDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load relationshipItem on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.relationshipItem).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
