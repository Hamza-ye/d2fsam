import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProgramStageInstanceFilterDetailComponent } from './program-stage-instance-filter-detail.component';

describe('ProgramStageInstanceFilter Management Detail Component', () => {
  let comp: ProgramStageInstanceFilterDetailComponent;
  let fixture: ComponentFixture<ProgramStageInstanceFilterDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProgramStageInstanceFilterDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ programStageInstanceFilter: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProgramStageInstanceFilterDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProgramStageInstanceFilterDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load programStageInstanceFilter on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.programStageInstanceFilter).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
