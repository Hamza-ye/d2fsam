import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StockItemFormService } from './stock-item-form.service';
import { StockItemService } from '../service/stock-item.service';
import { IStockItem } from '../stock-item.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { StockItemUpdateComponent } from './stock-item-update.component';

describe('StockItem Management Update Component', () => {
  let comp: StockItemUpdateComponent;
  let fixture: ComponentFixture<StockItemUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let stockItemFormService: StockItemFormService;
  let stockItemService: StockItemService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StockItemUpdateComponent],
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
      .overrideTemplate(StockItemUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StockItemUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stockItemFormService = TestBed.inject(StockItemFormService);
    stockItemService = TestBed.inject(StockItemService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const stockItem: IStockItem = { id: 456 };
      const createdBy: IUser = { id: 22753 };
      stockItem.createdBy = createdBy;
      const updatedBy: IUser = { id: 96630 };
      stockItem.updatedBy = updatedBy;

      const userCollection: IUser[] = [{ id: 5324 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy, updatedBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stockItem });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const stockItem: IStockItem = { id: 456 };
      const createdBy: IUser = { id: 73067 };
      stockItem.createdBy = createdBy;
      const updatedBy: IUser = { id: 6789 };
      stockItem.updatedBy = updatedBy;

      activatedRoute.data = of({ stockItem });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.usersSharedCollection).toContain(updatedBy);
      expect(comp.stockItem).toEqual(stockItem);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockItem>>();
      const stockItem = { id: 123 };
      jest.spyOn(stockItemFormService, 'getStockItem').mockReturnValue(stockItem);
      jest.spyOn(stockItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stockItem }));
      saveSubject.complete();

      // THEN
      expect(stockItemFormService.getStockItem).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(stockItemService.update).toHaveBeenCalledWith(expect.objectContaining(stockItem));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockItem>>();
      const stockItem = { id: 123 };
      jest.spyOn(stockItemFormService, 'getStockItem').mockReturnValue({ id: null });
      jest.spyOn(stockItemService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockItem: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stockItem }));
      saveSubject.complete();

      // THEN
      expect(stockItemFormService.getStockItem).toHaveBeenCalled();
      expect(stockItemService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStockItem>>();
      const stockItem = { id: 123 };
      jest.spyOn(stockItemService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stockItem });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stockItemService.update).toHaveBeenCalled();
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
  });
});
