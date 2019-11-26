import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { CreateProjectPage } from './create-project.page';

describe('CreateProjectPage', () => {
  let component: CreateProjectPage;
  let fixture: ComponentFixture<CreateProjectPage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateProjectPage ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateProjectPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
