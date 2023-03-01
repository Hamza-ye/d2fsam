import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISystemSetting } from '../system-setting.model';

@Component({
  selector: 'app-system-setting-detail',
  templateUrl: './system-setting-detail.component.html',
})
export class SystemSettingDetailComponent implements OnInit {
  systemSetting: ISystemSetting | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ systemSetting }) => {
      this.systemSetting = systemSetting;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
