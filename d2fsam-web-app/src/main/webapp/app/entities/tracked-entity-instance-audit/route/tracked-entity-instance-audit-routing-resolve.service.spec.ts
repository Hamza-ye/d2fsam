import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ITrackedEntityInstanceAudit } from '../tracked-entity-instance-audit.model';
import { TrackedEntityInstanceAuditService } from '../service/tracked-entity-instance-audit.service';

import { TrackedEntityInstanceAuditRoutingResolveService } from './tracked-entity-instance-audit-routing-resolve.service';

describe('TrackedEntityInstanceAudit routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: TrackedEntityInstanceAuditRoutingResolveService;
  let service: TrackedEntityInstanceAuditService;
  let resultTrackedEntityInstanceAudit: ITrackedEntityInstanceAudit | null | undefined;

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
    routingResolveService = TestBed.inject(TrackedEntityInstanceAuditRoutingResolveService);
    service = TestBed.inject(TrackedEntityInstanceAuditService);
    resultTrackedEntityInstanceAudit = undefined;
  });

  describe('resolve', () => {
    it('should return ITrackedEntityInstanceAudit returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTrackedEntityInstanceAudit = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTrackedEntityInstanceAudit).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTrackedEntityInstanceAudit = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTrackedEntityInstanceAudit).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<ITrackedEntityInstanceAudit>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTrackedEntityInstanceAudit = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTrackedEntityInstanceAudit).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
