import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { OptionSetService } from '../service/option-set.service';

import { OptionSetComponent } from './option-set.component';

describe('OptionSet Management Component', () => {
  let comp: OptionSetComponent;
  let fixture: ComponentFixture<OptionSetComponent>;
  let service: OptionSetService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'option-set', component: OptionSetComponent }]), HttpClientTestingModule],
      declarations: [OptionSetComponent],
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
      .overrideTemplate(OptionSetComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OptionSetComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(OptionSetService);

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
    expect(comp.optionSets?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to optionSetService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getOptionSetIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getOptionSetIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
