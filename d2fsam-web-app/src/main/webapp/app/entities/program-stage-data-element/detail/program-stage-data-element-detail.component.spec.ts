import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProgramStageDataElementDetailComponent } from './program-stage-data-element-detail.component';

describe('ProgramStageDataElement Management Detail Component', () => {
  let comp: ProgramStageDataElementDetailComponent;
  let fixture: ComponentFixture<ProgramStageDataElementDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProgramStageDataElementDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ programStageDataElement: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProgramStageDataElementDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProgramStageDataElementDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load programStageDataElement on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.programStageDataElement).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
