import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ITrackedEntityAttributeValueAudit } from '../tracked-entity-attribute-value-audit.model';
import { TrackedEntityAttributeValueAuditService } from '../service/tracked-entity-attribute-value-audit.service';

import { TrackedEntityAttributeValueAuditRoutingResolveService } from './tracked-entity-attribute-value-audit-routing-resolve.service';

describe('TrackedEntityAttributeValueAudit routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: TrackedEntityAttributeValueAuditRoutingResolveService;
  let service: TrackedEntityAttributeValueAuditService;
  let resultTrackedEntityAttributeValueAudit: ITrackedEntityAttributeValueAudit | null | undefined;

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
    routingResolveService = TestBed.inject(TrackedEntityAttributeValueAuditRoutingResolveService);
    service = TestBed.inject(TrackedEntityAttributeValueAuditService);
    resultTrackedEntityAttributeValueAudit = undefined;
  });

  describe('resolve', () => {
    it('should return ITrackedEntityAttributeValueAudit returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTrackedEntityAttributeValueAudit = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTrackedEntityAttributeValueAudit).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTrackedEntityAttributeValueAudit = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTrackedEntityAttributeValueAudit).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<ITrackedEntityAttributeValueAudit>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTrackedEntityAttributeValueAudit = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTrackedEntityAttributeValueAudit).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
