import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SqlViewService } from '../service/sql-view.service';

import { SqlViewComponent } from './sql-view.component';

describe('SqlView Management Component', () => {
  let comp: SqlViewComponent;
  let fixture: ComponentFixture<SqlViewComponent>;
  let service: SqlViewService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'sql-view', component: SqlViewComponent }]), HttpClientTestingModule],
      declarations: [SqlViewComponent],
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
      .overrideTemplate(SqlViewComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SqlViewComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SqlViewService);

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
    expect(comp.sqlViews?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to sqlViewService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSqlViewIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSqlViewIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
