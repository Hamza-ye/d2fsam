import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StockItemGroupFormService } from './stock-item-group-form.service';
import { StockItemGroupService } from '../service/stock-item-group.service';
import { IStockItemGroup } from '../stock-item-group.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IStockItem } from 'app/entities/stock-item/stock-item.model';
import { StockItemService } from 'app/entities/stock-item/service/stock-item.service';

import { StockItemGroupUpdateComponent } from './stock-item-group-update.component';

describe('StockItemGroup Management Update Component', () => {
  let comp: StockItemGroupUpdateComponent;
  let fixture: ComponentFixture<StockItemGroupUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let stockItemGroupFormService: StockItemGroupFormService;
  let stockItemGroupService: StockItemGroupService;
  let userService: UserService;
  let stockItemService: StockItemService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StockItemGroupUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(StockItemGroupUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StockItemGroupUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stockItemGroupFormService = TestBed.inject(StockItemGroupFormService);
    stockItemGroupService = TestBed.inject(StockItemGroupService);
    userService = TestBed.inject(UserService);
    stockItemService = TestBed.inject(StockItemService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const stockItemGroup: IStockItemGroup = { id: 456 };
      const createdBy: IUser = { id: 13982 };
      stockItemGroup.createdBy = createdBy;
      const updatedBy: IUser = { id: 45461 };
      stockItemGroup.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 61724 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stockItemGroup });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call StockItem query and add missing value', () => {
      const stockItemGroup: IStockItemGroup = { id: 456 };
      const items: IStockItem[] = [{ id: 38339 }];
      stockItemGroup.items = items;

      const stockItemCollection: IStockItem[] = [{ id: 89750 }];
      jest.spyOn(stockItemService, 'query').mockReturnValue(of(new HttpResponse({ body: stockItemCollection })));
      const additionalStockItems = [...items];
      const expectedCollection: IStockItem[] = [...additionalStockItems, ...stockItemCollection];
      jest.spyOn(stockItemService, 'addStockItemToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stockItemGroup });
      comp.ngOnInit();

      expect(stockItemService.query).toHaveBeenCalled();
      expect(stockItemService.addStockItemToCollectionIfMissing).toHaveBeenCalledWith(
        stockItemCollection,
        ...additionalStockItems.map(expect.objectContaining)
      );
      expect(comp.stockItemsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const stockItemGroup: IStockItemGroup = { id: 456 };
      const createdBy: IUser = { id: 23703 };
      stockItemGroup.createdBy = createdBy;
      const updatedBy: IUser = { id: 61756 };
      stockItemGroup.updatedBy = updatedBy;
      const item: IStockItem = { id: 68346 };
      stockItemGroup.items = [item];

      activatedRoute.data = of({ stockItemGroup });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.stockItemsSharedCollection).toContain(item);
      expect(comp.stockItemGroup).toEqual(stockItemGroup);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockItemGroup>>();
      const stockItemGroup = { id: 123 };
      jest.spyOn(stockItemGroupFormService, 'getStockItemGroup').mockReturnValue(stockItemGroup);
      jest.spyOn(stockItemGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockItemGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stockItemGroup }));
      saveSubject.complete();

      // THEN
      expect(stockItemGroupFormService.getStockItemGroup).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(stockItemGroupService.update).toHaveBeenCalledWith(expect.objectContaining(stockItemGroup));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockItemGroup>>();
      const stockItemGroup = { id: 123 };
      jest.spyOn(stockItemGroupFormService, 'getStockItemGroup').mockReturnValue({ id: null });
      jest.spyOn(stockItemGroupService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockItemGroup: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stockItemGroup }));
      saveSubject.complete();

      // THEN
      expect(stockItemGroupFormService.getStockItemGroup).toHaveBeenCalled();
      expect(stockItemGroupService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockItemGroup>>();
      const stockItemGroup = { id: 123 };
      jest.spyOn(stockItemGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockItemGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stockItemGroupService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStockItem', () => {
      it('Should forward to stockItemService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(stockItemService, 'compareStockItem');
        comp.compareStockItem(entity, entity2);
        expect(stockItemService.compareStockItem).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
