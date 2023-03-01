import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProgramStageInstanceFilterService } from '../service/program-stage-instance-filter.service';

import { ProgramStageInstanceFilterComponent } from './program-stage-instance-filter.component';

describe('ProgramStageInstanceFilter Management Component', () => {
  let comp: ProgramStageInstanceFilterComponent;
  let fixture: ComponentFixture<ProgramStageInstanceFilterComponent>;
  let service: ProgramStageInstanceFilterService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'program-stage-instance-filter', component: ProgramStageInstanceFilterComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [ProgramStageInstanceFilterComponent],
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
      .overrideTemplate(ProgramStageInstanceFilterComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramStageInstanceFilterComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProgramStageInstanceFilterService);

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
    expect(comp.programStageInstanceFilters?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to programStageInstanceFilterService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getProgramStageInstanceFilterIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getProgramStageInstanceFilterIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
