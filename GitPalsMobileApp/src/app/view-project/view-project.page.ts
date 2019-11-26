import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import axios from 'axios';
import { localService } from '../localService';
import { AlertController } from '@ionic/angular';

@Component({
  selector: 'app-view-project',
  templateUrl: './view-project.page.html',
  styleUrls: ['./view-project.page.scss'],
})
export class ViewProjectPage implements OnInit {

  constructor(private route: ActivatedRoute, 
    private service: localService, 
    private router: Router,
    private alertCtrl: AlertController) { }

  project: any;
  user: any;

  ngOnInit() {
    this.route.queryParams.subscribe(data => {
      this.project = JSON.parse(data.project);

      if(localStorage.getItem('user') != null) {
        this.user = JSON.parse(localStorage.getItem('user'));
      }
    })
  }

  removeProject() {
    this.alertCtrl.create({
      header: 'Delete',
      subHeader: 'Are you sure?',
      buttons: [
        {
          text: 'Yes',
          handler: () => {
            axios.post(this.service.getAPI() + '/projects/deleteProject', {
              projectName: this.project.title,
              username: this.user.login
            }).then(response => {
              if(response.data.status == 'OK') {
                this.showAlert('Success!');
                this.router.navigateByUrl('/');
              } else {
                this.showAlert('Error');
              }
            });
          }
        },
        {
          text: 'No'
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
}