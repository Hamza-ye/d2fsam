import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserAuthorityGroup } from '../user-authority-group.model';

@Component({
  selector: 'app-user-authority-group-detail',
  templateUrl: './user-authority-group-detail.component.html',
})
export class UserAuthorityGroupDetailComponent implements OnInit {
  userAuthorityGroup: IUserAuthorityGroup | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userAuthorityGroup }) => {
      this.userAuthorityGroup = userAuthorityGroup;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
