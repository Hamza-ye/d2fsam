import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SqlViewDetailComponent } from './sql-view-detail.component';

describe('SqlView Management Detail Component', () => {
  let comp: SqlViewDetailComponent;
  let fixture: ComponentFixture<SqlViewDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SqlViewDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ sqlView: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SqlViewDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SqlViewDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load sqlView on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.sqlView).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
