import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProgramTempOwnershipAudit } from '../program-temp-ownership-audit.model';

@Component({
  selector: 'app-program-temp-ownership-audit-detail',
  templateUrl: './program-temp-ownership-audit-detail.component.html',
})
export class ProgramTempOwnershipAuditDetailComponent implements OnInit {
  programTempOwnershipAudit: IProgramTempOwnershipAudit | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ programTempOwnershipAudit }) => {
      this.programTempOwnershipAudit = programTempOwnershipAudit;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
