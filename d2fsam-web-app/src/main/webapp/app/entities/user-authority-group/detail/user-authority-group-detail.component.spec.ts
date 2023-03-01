import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserAuthorityGroupDetailComponent } from './user-authority-group-detail.component';

describe('UserAuthorityGroup Management Detail Component', () => {
  let comp: UserAuthorityGroupDetailComponent;
  let fixture: ComponentFixture<UserAuthorityGroupDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserAuthorityGroupDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userAuthorityGroup: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserAuthorityGroupDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserAuthorityGroupDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userAuthorityGroup on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userAuthorityGroup).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
