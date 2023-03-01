import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ITrackedEntityInstanceFilter } from '../tracked-entity-instance-filter.model';
import { TrackedEntityInstanceFilterService } from '../service/tracked-entity-instance-filter.service';

import { TrackedEntityInstanceFilterRoutingResolveService } from './tracked-entity-instance-filter-routing-resolve.service';

describe('TrackedEntityInstanceFilter routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: TrackedEntityInstanceFilterRoutingResolveService;
  let service: TrackedEntityInstanceFilterService;
  let resultTrackedEntityInstanceFilter: ITrackedEntityInstanceFilter | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(TrackedEntityInstanceFilterRoutingResolveService);
    service = TestBed.inject(TrackedEntityInstanceFilterService);
    resultTrackedEntityInstanceFilter = undefined;
  });

  describe('resolve', () => {
    it('should return ITrackedEntityInstanceFilter returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTrackedEntityInstanceFilter = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTrackedEntityInstanceFilter).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTrackedEntityInstanceFilter = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTrackedEntityInstanceFilter).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<ITrackedEntityInstanceFilter>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTrackedEntityInstanceFilter = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTrackedEntityInstanceFilter).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
