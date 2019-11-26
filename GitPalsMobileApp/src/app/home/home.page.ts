import { Component } from '@angular/core';
import axios from 'axios';
import { localService } from './../localService';
import { AlertController, ActionSheetController, NavController } from '@ionic/angular';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {

  PROJECT_LIMIT = 25;
  projects = [];

  user: any;

  constructor(private service: localService, 
    private alertCtrl: AlertController, 
    private actionCtrl: ActionSheetController,
    private router: Router,
    private navCtrl: NavController) {}

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
    this.router.navigate(['view-project'], { queryParams: { project: JSON.stringify(project)} });
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

              this.showAlert('Auth success!');
            }).catch(err => this.showAlert('Wrong Credentials'));
          }
        },
        {
          text: 'Cancel',
          role: 'cancel'
        }
      ]
    }).then(alert => alert.present());
  }

  showAlert(text) {
    this.alertCtrl.create({
      header: text,
      buttons: ['OK']
    }).then(alert => alert.present());
  }

  showActions() {
    this.actionCtrl.create({
      header: 'Action',
      buttons: [
        {
          text: 'Submit Project',
          icon: 'checkmark-circle-outline',
          handler: () => {
            if(this.user != null) {
              this.router.navigate(['create-project']);
            } else {
              this.showAlert('You need to Sign In first');
            }
          }
        },
        {
          text: 'Logout',
          icon: 'log-out',
          role: 'destructive',
          handler: () => {
            if(localStorage.getItem('user') != null) {
              localStorage.clear();
              this.showAlert('Success!');
              this.user = null;
            } else {
              this.showAlert('You need to Sign In first');
            }
          }
        }
      ]
    }).then(alert => alert.present());
  }
}
