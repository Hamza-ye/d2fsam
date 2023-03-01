import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserGroupDetailComponent } from './user-group-detail.component';

describe('UserGroup Management Detail Component', () => {
  let comp: UserGroupDetailComponent;
  let fixture: ComponentFixture<UserGroupDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserGroupDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ userGroup: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(UserGroupDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UserGroupDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load userGroup on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.userGroup).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
