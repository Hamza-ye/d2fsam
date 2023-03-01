import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProgramStageDetailComponent } from './program-stage-detail.component';

describe('ProgramStage Management Detail Component', () => {
  let comp: ProgramStageDetailComponent;
  let fixture: ComponentFixture<ProgramStageDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProgramStageDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ programStage: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProgramStageDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProgramStageDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load programStage on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.programStage).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
