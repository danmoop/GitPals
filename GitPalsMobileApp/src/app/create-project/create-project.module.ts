import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { CreateProjectPageRoutingModule } from './create-project-routing.module';

import { CreateProjectPage } from './create-project.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    CreateProjectPageRoutingModule
  ],
  declarations: [CreateProjectPage]
})
export class CreateProjectPageModule {}
