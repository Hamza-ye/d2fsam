import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProgramTempOwnershipAuditDetailComponent } from './program-temp-ownership-audit-detail.component';

describe('ProgramTempOwnershipAudit Management Detail Component', () => {
  let comp: ProgramTempOwnershipAuditDetailComponent;
  let fixture: ComponentFixture<ProgramTempOwnershipAuditDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProgramTempOwnershipAuditDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ programTempOwnershipAudit: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProgramTempOwnershipAuditDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProgramTempOwnershipAuditDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load programTempOwnershipAudit on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.programTempOwnershipAudit).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
