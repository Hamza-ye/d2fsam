import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProgramStageInstanceDetailComponent } from './program-stage-instance-detail.component';

describe('ProgramStageInstance Management Detail Component', () => {
  let comp: ProgramStageInstanceDetailComponent;
  let fixture: ComponentFixture<ProgramStageInstanceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProgramStageInstanceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ programStageInstance: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProgramStageInstanceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProgramStageInstanceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load programStageInstance on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.programStageInstance).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
