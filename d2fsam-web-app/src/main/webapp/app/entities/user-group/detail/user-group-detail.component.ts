import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserGroup } from '../user-group.model';

@Component({
  selector: 'app-user-group-detail',
  templateUrl: './user-group-detail.component.html',
})
export class UserGroupDetailComponent implements OnInit {
  userGroup: IUserGroup | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userGroup }) => {
      this.userGroup = userGroup;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
