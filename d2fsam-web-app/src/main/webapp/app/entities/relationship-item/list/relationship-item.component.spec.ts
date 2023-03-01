import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RelationshipItemService } from '../service/relationship-item.service';

import { RelationshipItemComponent } from './relationship-item.component';

describe('RelationshipItem Management Component', () => {
  let comp: RelationshipItemComponent;
  let fixture: ComponentFixture<RelationshipItemComponent>;
  let service: RelationshipItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'relationship-item', component: RelationshipItemComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [RelationshipItemComponent],
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
      .overrideTemplate(RelationshipItemComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelationshipItemComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RelationshipItemService);

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
    expect(comp.relationshipItems?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to relationshipItemService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getRelationshipItemIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getRelationshipItemIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
