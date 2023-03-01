import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProgramTrackedEntityAttributeDetailComponent } from './program-tracked-entity-attribute-detail.component';

describe('ProgramTrackedEntityAttribute Management Detail Component', () => {
  let comp: ProgramTrackedEntityAttributeDetailComponent;
  let fixture: ComponentFixture<ProgramTrackedEntityAttributeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProgramTrackedEntityAttributeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ programTrackedEntityAttribute: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProgramTrackedEntityAttributeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProgramTrackedEntityAttributeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load programTrackedEntityAttribute on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.programTrackedEntityAttribute).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
