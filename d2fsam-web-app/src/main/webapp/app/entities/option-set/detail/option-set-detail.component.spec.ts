import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OptionSetDetailComponent } from './option-set-detail.component';

describe('OptionSet Management Detail Component', () => {
  let comp: OptionSetDetailComponent;
  let fixture: ComponentFixture<OptionSetDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OptionSetDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ optionSet: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OptionSetDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OptionSetDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load optionSet on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.optionSet).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
