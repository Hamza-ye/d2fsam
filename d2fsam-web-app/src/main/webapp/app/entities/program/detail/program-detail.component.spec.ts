import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProgramDetailComponent } from './program-detail.component';

describe('Program Management Detail Component', () => {
  let comp: ProgramDetailComponent;
  let fixture: ComponentFixture<ProgramDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProgramDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ program: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProgramDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProgramDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load program on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.program).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
