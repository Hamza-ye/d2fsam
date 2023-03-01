import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProgramStageDataElementService } from '../service/program-stage-data-element.service';

import { ProgramStageDataElementComponent } from './program-stage-data-element.component';

describe('ProgramStageDataElement Management Component', () => {
  let comp: ProgramStageDataElementComponent;
  let fixture: ComponentFixture<ProgramStageDataElementComponent>;
  let service: ProgramStageDataElementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'program-stage-data-element', component: ProgramStageDataElementComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [ProgramStageDataElementComponent],
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
      .overrideTemplate(ProgramStageDataElementComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgramStageDataElementComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProgramStageDataElementService);

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
    expect(comp.programStageDataElements?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to programStageDataElementService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getProgramStageDataElementIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getProgramStageDataElementIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
