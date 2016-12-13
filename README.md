## Synopsis

This is the Balansio Smart application for Android. Purpose of the application is to assist people with chronic illnesses by creating goals for weight, exercise, blood glucose etc. and help in achieving them. The assisting is done by showing the progress of the goals, and reminding and motivating with notifications to stay on track.


## Installation

Clone the master branch and open on Android Studio.

## Documentation

Project documentations can be found here: https://drive.google.com/drive/folders/0Bxww918WATbTVWNaYzRCZEdrTWM?usp=sharing

Documentation for Realm can be found here: https://realm.io/docs/java/latest/

## Project Structure

There are four Activities: 
    - ProgressViewActivity(the main) for the main view/progress view
    - GoalComposerActivity for the goal creation wizard
    - GoalDetailViewActivity for the detailed goal view wchich is accessed from progress view
    - WelcomeSliderActivity from the welcome screens

Progress view 
shows a list of users current goals in a recycler view. Each item in the recycler view represents a goal and shows its status. Clicking an item opens the detailed goal view for that goal. There is a button for adding new goals through the goal composer. It has a card stack element wchich is imported. It is used for displaying missed notifications. Card stack is located inside the cardstack package. 

goalComposer package
has the GoalComposerActivity which is used for creating and editing goals. It has multiple fragments that are passed through horizontally in order, and each fragement has elements for selecting values for the goal. These fragments are also located in the goalComposer package.

dialogs package 
has dialogs for errors and debugging the app.

model package 
has all model classes and interfaces. Realm is used for creating and persisting the model.

goalDetails package 
has activity for the detailed goal view and a recyclerview adpter for displaying measurements in a list.

goalList package 
has classes for progress views goal list's elements.

notifications package
has the NotificationIntentService class that handles notification logic and notification creation in the background. Service is run every fifteen minutes to check the need for fireing new notifications. NotificationEventReceiver is responsible for launching the intent.

storage package 
has Storage class for simplifying the usage of realm transaction writes.

welcomeScreens package
has WelcomsSliderActivity with initializer classes.


## License

Created by Julius Niiniranta, Eemeli Heinonen, Mortti Aittokoski and Rubing Mao