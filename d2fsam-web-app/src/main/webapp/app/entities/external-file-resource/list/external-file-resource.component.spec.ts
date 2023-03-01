import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ExternalFileResourceService } from '../service/external-file-resource.service';

import { ExternalFileResourceComponent } from './external-file-resource.component';

describe('ExternalFileResource Management Component', () => {
  let comp: ExternalFileResourceComponent;
  let fixture: ComponentFixture<ExternalFileResourceComponent>;
  let service: ExternalFileResourceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'external-file-resource', component: ExternalFileResourceComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [ExternalFileResourceComponent],
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
      .overrideTemplate(ExternalFileResourceComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExternalFileResourceComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ExternalFileResourceService);

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
    expect(comp.externalFileResources?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to externalFileResourceService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getExternalFileResourceIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getExternalFileResourceIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
