import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProgramTempOwnerDetailComponent } from './program-temp-owner-detail.component';

describe('ProgramTempOwner Management Detail Component', () => {
  let comp: ProgramTempOwnerDetailComponent;
  let fixture: ComponentFixture<ProgramTempOwnerDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProgramTempOwnerDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ programTempOwner: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProgramTempOwnerDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProgramTempOwnerDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load programTempOwner on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.programTempOwner).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
