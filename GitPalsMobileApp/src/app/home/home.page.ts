import { Component } from '@angular/core';
import axios from 'axios';
import { localService } from './../localService';
import { AlertController } from '@ionic/angular';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {

  PROJECT_LIMIT = 25;
  projects = [];

  user: any;

  constructor(private service: localService, private alertCtrl: AlertController) {}

  ionViewDidEnter() {
    this.fetchProjects(null);

    if(localStorage.getItem('user') != null) {
      this.user = JSON.parse(localStorage.getItem('user'));
    }
  }

  fetchProjects(event) {
    axios.get(this.service.getAPI() + '/projects/getAmount/' + this.PROJECT_LIMIT)
      .then(response => {
        this.projects = response.data.reverse();

        if(event != null) {
          event.target.complete();
        }
      });
  }

  displayReqs(project) {
    return project.requirements.join(', ');
  }

  getProjectInfo(project) {
    console.log(project);
  }

  auth() {
    this.alertCtrl.create({
      header: 'Sign In to GitHub',
      inputs: [
        {
          name: 'username',
          placeholder: 'Username',
        },
        {
          name: 'password',
          placeholder: 'Password',
          type: 'password'
        }
      ],
      buttons: [
        {
          text: 'OK',
          handler: (data) => {
            axios('https://api.github.com/user', {
              method: 'GET',
              auth: {
                username: data.username,
                password: data.password
              }
            }).then(response => {
              this.user = response.data;
              localStorage.setItem('user', JSON.stringify(this.user));
            }).catch(err => this.wrongCredentialsAlert());
          }
        },
        {
          text: 'Cancel',
          role: 'cancel'
        }
      ]
    }).then(alert => alert.present());
  }

  wrongCredentialsAlert() {
    this.alertCtrl.create({
      header: 'Wrong Credentials',
      buttons: ['OK']
    }).then(alert => alert.present());
  }
}
