import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { CreateProjectPage } from './create-project.page';

const routes: Routes = [
  {
    path: '',
    component: CreateProjectPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CreateProjectPageRoutingModule {}
