import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FileResourceService } from '../service/file-resource.service';

import { FileResourceComponent } from './file-resource.component';

describe('FileResource Management Component', () => {
  let comp: FileResourceComponent;
  let fixture: ComponentFixture<FileResourceComponent>;
  let service: FileResourceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'file-resource', component: FileResourceComponent }]), HttpClientTestingModule],
      declarations: [FileResourceComponent],
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
      .overrideTemplate(FileResourceComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FileResourceComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FileResourceService);

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
    expect(comp.fileResources?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to fileResourceService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getFileResourceIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getFileResourceIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
