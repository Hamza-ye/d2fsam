import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RelationshipDetailComponent } from './relationship-detail.component';

describe('Relationship Management Detail Component', () => {
  let comp: RelationshipDetailComponent;
  let fixture: ComponentFixture<RelationshipDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RelationshipDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ relationship: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RelationshipDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RelationshipDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load relationship on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.relationship).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
