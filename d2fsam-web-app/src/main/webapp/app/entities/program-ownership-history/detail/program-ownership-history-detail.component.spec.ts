import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProgramOwnershipHistoryDetailComponent } from './program-ownership-history-detail.component';

describe('ProgramOwnershipHistory Management Detail Component', () => {
  let comp: ProgramOwnershipHistoryDetailComponent;
  let fixture: ComponentFixture<ProgramOwnershipHistoryDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProgramOwnershipHistoryDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ programOwnershipHistory: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProgramOwnershipHistoryDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProgramOwnershipHistoryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load programOwnershipHistory on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.programOwnershipHistory).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
