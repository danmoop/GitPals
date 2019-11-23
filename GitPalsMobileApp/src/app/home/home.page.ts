import { Component } from '@angular/core';
import axios from 'axios';
import { localService } from './../localService';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {

  PROJECT_LIMIT = 25;
  projects = [];

  constructor(private service: localService) {}

  ionViewDidEnter() {
    this.fetchProjects(null);
  }

  fetchProjects(event) {
    axios.get(this.service.getAPI() + '/projects/getAmount/' + this.PROJECT_LIMIT)
      .then(response => {
        this.projects = response.data.reverse();
        console.log(response.data[0]);

        if(event != null) {
          event.target.complete();
        }
      });
  }

  displayReqs(project) {
    return project.requirements.join(', ');
  }


}
