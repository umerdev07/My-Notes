
# My-Todo

## Overview

The My-Todo app is an Android application that allows users to create, view, update, and delete tasks, with Firebase Authentication for secure login and Google Sign-In integration. Users can manage their tasks, search through them, and customize the app's language and logout settings.

## Features

### Authentication
- **Email/Password Authentication**: Users can sign in using their email and password.
- **Google Sign-In**: Users can authenticate using their Google account.

### Task Management
- **Create Tasks**: Users can create new tasks with a title, description, and date-time.
- **Update Tasks**: Users can update the title, description, and date-time of existing tasks.
- **Delete Tasks**: Users can delete tasks.
- **Share Tasks**: Users can share tasks via text.
  
### Search
- **Search Tasks**: Users can search for tasks by title or description using a search bar.
  
### Calendar and Time Picker
- **Date and Time Selection**: Users can pick a date and time for tasks using a calendar and time picker dialog.

### Multi-language Support
- **Language Selection**: Users can select from a variety of languages.

## Dependencies

    // Firebase BoM for managing Firebase library versions
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    // Firebase Authentication library
    implementation("com.google.firebase:firebase-auth-ktx")

    // Google Play services for authentication
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Material Design library
    implementation("com.google.android.material:material:1.12.0")

    // Firebase Realtime Database
    implementation("com.google.firebase:firebase-database:21.0.0")

## Usage

### Authentication
1. **Email/Password Sign-In**: Enter your email and password to sign in.
2. **Google Sign-In**: Click the "Sign in with Google" button and follow the prompts.

### Task Management
1. **Creating Tasks**: Click the "Create Task" button, enter the title, description, and select a date-time, then save the task.
2. **Updating Tasks**: Click the "Update" button on a task, modify the title, description, or date-time, and save changes.
3. **Deleting Tasks**: Click the "Delete" button on a task and confirm the deletion.
4. **Sharing Tasks**: Click the "Share" button to share the task via text.

### Search
1. **Searching Tasks**: Use the search bar to find tasks by title or description. Enter keywords to filter the task list accordingly.

### Calendar and Time Picker
- Use the calendar and time picker dialogs to select date and time when creating or updating tasks.

### Multi-language Support
- Click the "Menu" button, select "Language", and choose your preferred language.

## Screenshots
![IMG-20240804-WA0007](https://github.com/user-attachments/assets/5e63aa0e-d95f-40b7-b7d5-95d55156e825)    ![IMG-20240804-WA0005](https://github.com/user-attachments/assets/ed4dcafb-30d9-4529-8fb6-4bc9c2d84e8b)      ![IMG-20240804-WA0008](https://github.com/user-attachments/assets/57ab0414-c08d-4688-89a7-643e3b39ebce)   ![IMG-20240804-WA0006](https://github.com/user-attachments/assets/5e7918f1-422a-4554-b81b-0d5566d424cf) ![IMG-20240804-WA0009](https://github.com/user-attachments/assets/3bfd0b32-1266-4660-b094-e696e640eab0)  ![IMG-20240804-WA0010](https://github.com/user-attachments/assets/98c82d6b-bb76-4eca-afa1-0f23aae605b5) 

![IMG-20240804-WA0011](https://github.com/user-attachments/assets/bcd58c49-c8a5-4823-b10e-df263d501d38)  ![IMG-20240804-WA0012](https://github.com/user-attachments/assets/311e767a-385c-4c9b-a239-912b03e23ce4)   ![IMG-20240804-WA0013](https://github.com/user-attachments/assets/72a4b076-0021-4831-ae81-47ee8b89ade3)   ![IMG-20240804-WA0014](https://github.com/user-attachments/assets/7ecdd37e-67d1-472f-bd87-f1b90eaba1b9)   ![IMG-20240804-WA0015](https://github.com/user-attachments/assets/b8f5ada8-52ea-448d-8d7c-6114e8a29066)   ![IMG-20240804-WA0016](https://github.com/user-attachments/assets/2e0cc81f-b6a7-4085-9174-2501d2df3df3)

















## Contributing

If you would like to contribute to this project, please fork the repository and submit a pull request with your changes.
