import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OptionDetailComponent } from './option-detail.component';

describe('Option Management Detail Component', () => {
  let comp: OptionDetailComponent;
  let fixture: ComponentFixture<OptionDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OptionDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ option: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OptionDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OptionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load option on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.option).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
