import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { MetadataVersionService } from '../service/metadata-version.service';

import { MetadataVersionComponent } from './metadata-version.component';

describe('MetadataVersion Management Component', () => {
  let comp: MetadataVersionComponent;
  let fixture: ComponentFixture<MetadataVersionComponent>;
  let service: MetadataVersionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'metadata-version', component: MetadataVersionComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [MetadataVersionComponent],
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
      .overrideTemplate(MetadataVersionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MetadataVersionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MetadataVersionService);

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
    expect(comp.metadataVersions?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to metadataVersionService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getMetadataVersionIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getMetadataVersionIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
