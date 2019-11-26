import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { ViewProjectPage } from './view-project.page';

describe('ViewProjectPage', () => {
  let component: ViewProjectPage;
  let fixture: ComponentFixture<ViewProjectPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewProjectPage ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(ViewProjectPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
