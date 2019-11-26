import { Component, OnInit } from '@angular/core';
import axios from 'axios';
import { localService } from '../localService';
import { Router } from '@angular/router';
import { AlertController } from '@ionic/angular';

@Component({
  selector: 'app-create-project',
  templateUrl: './create-project.page.html',
  styleUrls: ['./create-project.page.scss'],
})
export class CreateProjectPage implements OnInit {

  constructor(private localService: localService, private router: Router, private alertCtrl: AlertController) { }

  techs = ["Web design", "Mobile design", "Java", "C++",
    "Python", "Machine learning", "Deep learning", "Ionic",
    "Photoshop", "React", "JavaScript", "Angular", "Analytics", "Ruby",
    "NodeJS", "Unreal Engine", "Unity", "Game development", "Computer architecture",
    "C", "GLSL", "OpenGL", "HTML5", "C#", "Swift", "Big Data", "CSS",
    "Game modding", "Other"
  ];

  requirements = [];
  projectTitle = '';
  projectDescription = '';
  projectRepositoryLink = '';

  ngOnInit() {
    for(var i = 0; i < this.techs.length; i++) {
      var req = {
        text: this.techs[i],
        isChecked: false
      }
      this.requirements.push(req);
    }
  }

  submitProject() {
    var project = {
      title: this.projectTitle,
      description: this.projectDescription,
      githubProjectLink: this.projectRepositoryLink,
      requirements: this.getProjectRequirementsList(),
      username: JSON.parse(localStorage.getItem('user')).login
    }
    
    axios(this.localService.getAPI() + '/projects/createProject', {
      method: 'POST',
      data: project
    }).then(response => {
      if(response.data.status == 'OK') {
        this.router.navigateByUrl('/');
      } else {
        this.showAlert('Project with such title already exists');
      }
    });
  }

  getProjectRequirementsList() {
    var reqs = [];

    for(var i = 0; i < this.requirements.length; i++) {
      if(this.requirements[i].isChecked) {
        reqs.push(this.requirements[i].text);
      }
    }

    return reqs;
  }

  showAlert(text) {
    this.alertCtrl.create({
      header: text,
      buttons: ['OK']
    }).then(alert => alert.present());
  }
}