import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IOrganisationUnitLevel } from '../organisation-unit-level.model';
import { OrganisationUnitLevelService } from '../service/organisation-unit-level.service';

import { OrganisationUnitLevelRoutingResolveService } from './organisation-unit-level-routing-resolve.service';

describe('OrganisationUnitLevel routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: OrganisationUnitLevelRoutingResolveService;
  let service: OrganisationUnitLevelService;
  let resultOrganisationUnitLevel: IOrganisationUnitLevel | null | undefined;

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
    routingResolveService = TestBed.inject(OrganisationUnitLevelRoutingResolveService);
    service = TestBed.inject(OrganisationUnitLevelService);
    resultOrganisationUnitLevel = undefined;
  });

  describe('resolve', () => {
    it('should return IOrganisationUnitLevel returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultOrganisationUnitLevel = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultOrganisationUnitLevel).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultOrganisationUnitLevel = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultOrganisationUnitLevel).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IOrganisationUnitLevel>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultOrganisationUnitLevel = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultOrganisationUnitLevel).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
