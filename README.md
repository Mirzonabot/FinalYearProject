# FinalYearProject


## This repository contains the practical part of the final year project


Homestay Booking Mobile Application
Overview
The Homestay Booking mobile application allows users to browse, search, and book homestays in various locations. The application is structured in a modular manner, separating concerns for easy understanding, modification, and testing. The application uses a local database to store user data and application data, and it communicates with a server-side application for fetching and updating homestay details. The application supports offline (no internat connection but there is mobile set) booking as well.

Repository Structure
The project is structured into various folders, each representing a specific functionality in the application. The primary folders include:

Fragments: This folder contains all the fragment classes which represent various screens in the application.

Database Classes: This folder holds classes that define the structure of the database and its tables. These classes represent the schema of the application's database.

Main Activities: This folder contains the main activity classes of the application. These activities host the various fragments and manage the navigation between them.

Database Helper Classes: This folder includes classes that help in performing database operations like insert, update, delete and fetch.

SMS Management Classes: This folder contains classes responsible for handling SMS operations. This could be for sending booking confirmations or OTPs.

API Repository: This is a separate repository that contains the endpoints for the server-side application. This application interfaces with the mobile application for all data that needs to be fetched or updated from the server.

How to Use
1. Installation
Download the Homestay Booking application from the Github repository. 

Once installed, you will find the app icon in your device's app drawer or on your home screen. Click on the icon to open the application.

2. Setup
When you open the app for the first time, you will be guided through a setup process. This will involve accepting any necessary permissions and creating an account or logging in if you already have one.

To create an account, click on "Sign Up", fill in the required details, and submit. To log in, click on "Log In", fill in your credentials, and click on "Submit".

3. Basic Usage
Home Screen: Upon successful login, you will be taken to the home screen. Here you can browse through various homestays. You can also search for homestays by location, price, and other criteria using the search bar at the top.

Booking a Homestay: To book a homestay, click on the homestay you are interested in. This will take you to a detail page, where you can see more information about the homestay and choose to book it. Click on "Book Now", select your dates, and confirm.

Managing Bookings: To view your bookings, navigate to the "Bookings" tab from the bottom navigation bar. Here you can view your upcoming and past bookings. You can also cancel any upcoming bookings if necessary.

Profile and Settings: To manage your profile and settings, navigate to the "Profile" tab from the bottom navigation bar. Here you can update your profile information, change your password, and adjust app settings.

4. Troubleshooting
If you encounter any issues while using the application, you can refer to the "Help & Support" section in the "Profile" tab. Here you can find frequently asked questions, and contact support if needed.

Remember to always keep your application updated to the latest version to enjoy new features and improvements.

API Documentation
Documentation for the server-side application's API can be found in the API repository. This includes detailed information about each endpoint, including the required parameters, the response format, and any potential error codes.

Contribution Guidelines

To contribute to this project you should consider forking it.
overriding broardcastLister


License
Details of the license under which the project is released.

