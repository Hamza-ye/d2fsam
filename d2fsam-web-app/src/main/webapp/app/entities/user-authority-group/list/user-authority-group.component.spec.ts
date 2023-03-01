import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { UserAuthorityGroupService } from '../service/user-authority-group.service';

import { UserAuthorityGroupComponent } from './user-authority-group.component';

describe('UserAuthorityGroup Management Component', () => {
  let comp: UserAuthorityGroupComponent;
  let fixture: ComponentFixture<UserAuthorityGroupComponent>;
  let service: UserAuthorityGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'user-authority-group', component: UserAuthorityGroupComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [UserAuthorityGroupComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(UserAuthorityGroupComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserAuthorityGroupComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UserAuthorityGroupService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.userAuthorityGroups?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to userAuthorityGroupService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getUserAuthorityGroupIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getUserAuthorityGroupIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
