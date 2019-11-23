import { Injectable } from '@angular/core';


@Injectable()
export class localService {
    constructor() {}

    getAPI() {
        return "http://localhost:1337/api";
    }
}