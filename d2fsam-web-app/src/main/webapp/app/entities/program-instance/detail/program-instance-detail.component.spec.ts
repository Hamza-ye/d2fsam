import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProgramInstanceDetailComponent } from './program-instance-detail.component';

describe('ProgramInstance Management Detail Component', () => {
  let comp: ProgramInstanceDetailComponent;
  let fixture: ComponentFixture<ProgramInstanceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProgramInstanceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ programInstance: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProgramInstanceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProgramInstanceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load programInstance on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.programInstance).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
