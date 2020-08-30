# GitPals

<img src="https://image.flaticon.com/icons/svg/89/89341.svg" width="150" height="150">

[Open GitPals app](https://www.gitpals.com/)

This project uses:
1. Java (Spring Framework, Thymeleaf for template rendering on a webiste, REST API used for a GitPals Mobile App)
2. HTML / CSS (Shards UI Kit)
3. MongoDB as a database

Check-list
- [x] Adding/deleting projects
- [x] Filling in your profile
- [x] Applying to projects
- [x] Writing/editing/removing comments
- [x] Forum (like a discussion board)
- [x] Creating roles for your projects (like Designer/Programmer, so people would know who you are looking for)
- [x] Searching 
- [x] Bootstrap Design (Shards UI KIT)
- [x] Realtime messaging among users
- [ ] REST API (IN PROGRESS)
- [ ] Mobile App (IN PROGRESS: React Native + UI KITTEN)

The rest of images
![image](gallery/1.png)
![image](gallery/2.png)
![image](gallery/3.png)
![image](gallery/4.png)
![image](gallery/5.png)

# Contributing:
Thank you for deciding to contribute! Download GitPals to your PC. You need to have MongoDB installed on your pc.
Open project in any IDE (I use Intellij IDEA). Then open Console Prompt and type in: mongod --dbpath=path_to_db (for example: mongod --dbpath=C:\GitPals\DB). 

[Video Guide](https://youtu.be/JbvxJyXmOEM)

**address - localhost:1337**

**MongoDB port - default 27017**

**Unless you run mongo database, web page will return error**

Executing file - GitPals/src/main/java/com/moople/gitpals/MainApplication.java

**Controller folder** is a main folder. Controllers execute all the functions (register user, add project, delete smth etc.)

**Service folder** contains just 2 files. That files save data to Mongo Database

**Model folder** contains files that have information about each object (user's name, project's title etc.)
