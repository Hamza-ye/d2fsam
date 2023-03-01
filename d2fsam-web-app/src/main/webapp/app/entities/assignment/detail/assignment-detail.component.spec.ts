import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AssignmentDetailComponent } from './assignment-detail.component';

describe('Assignment Management Detail Component', () => {
  let comp: AssignmentDetailComponent;
  let fixture: ComponentFixture<AssignmentDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AssignmentDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ assignment: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AssignmentDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AssignmentDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load assignment on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.assignment).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
