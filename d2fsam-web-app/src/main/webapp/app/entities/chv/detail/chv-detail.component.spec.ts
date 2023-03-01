import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ChvDetailComponent } from './chv-detail.component';

describe('Chv Management Detail Component', () => {
  let comp: ChvDetailComponent;
  let fixture: ComponentFixture<ChvDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChvDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ chv: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ChvDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ChvDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load chv on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.chv).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
