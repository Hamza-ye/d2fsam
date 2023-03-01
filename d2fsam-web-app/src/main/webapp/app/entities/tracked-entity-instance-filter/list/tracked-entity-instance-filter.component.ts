import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { combineLatest, filter, Observable, switchMap, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITrackedEntityInstanceFilter } from '../tracked-entity-instance-filter.model';
import { ASC, DESC, SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { EntityArrayResponseType, TrackedEntityInstanceFilterService } from '../service/tracked-entity-instance-filter.service';
import { TrackedEntityInstanceFilterDeleteDialogComponent } from '../delete/tracked-entity-instance-filter-delete-dialog.component';
import { SortService } from 'app/shared/sort/sort.service';

@Component({
  selector: 'app-tracked-entity-instance-filter',
  templateUrl: './tracked-entity-instance-filter.component.html',
})
export class TrackedEntityInstanceFilterComponent implements OnInit {
  trackedEntityInstanceFilters?: ITrackedEntityInstanceFilter[];
  isLoading = false;

  predicate = 'id';
  ascending = true;

  constructor(
    protected trackedEntityInstanceFilterService: TrackedEntityInstanceFilterService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected sortService: SortService,
    protected modalService: NgbModal
  ) {}

  trackId = (_index: number, item: ITrackedEntityInstanceFilter): number =>
    this.trackedEntityInstanceFilterService.getTrackedEntityInstanceFilterIdentifier(item);

  ngOnInit(): void {
    this.load();
  }

  delete(trackedEntityInstanceFilter: ITrackedEntityInstanceFilter): void {
    const modalRef = this.modalService.open(TrackedEntityInstanceFilterDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.trackedEntityInstanceFilter = trackedEntityInstanceFilter;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        switchMap(() => this.loadFromBackendWithRouteInformations())
      )
      .subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
        },
      });
  }

  load(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.predicate, this.ascending);
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.predicate, this.ascending))
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.trackedEntityInstanceFilters = this.refineData(dataFromBody);
  }

  protected refineData(data: ITrackedEntityInstanceFilter[]): ITrackedEntityInstanceFilter[] {
    return data.sort(this.sortService.startSort(this.predicate, this.ascending ? 1 : -1));
  }

  protected fillComponentAttributesFromResponseBody(data: ITrackedEntityInstanceFilter[] | null): ITrackedEntityInstanceFilter[] {
    return data ?? [];
  }

  protected queryBackend(predicate?: string, ascending?: boolean): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject = {
      eagerload: true,
      sort: this.getSortQueryParam(predicate, ascending),
    };
    return this.trackedEntityInstanceFilterService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(predicate?: string, ascending?: boolean): void {
    const queryParamsObj = {
      sort: this.getSortQueryParam(predicate, ascending),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }
}
