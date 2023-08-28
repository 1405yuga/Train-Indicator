# Train-Indicator
"Train Indicator" is an Android app that provides users with info on Mumbai local train stations, including the closest station and details about stops for slow and fast trains. It helps users plan commutes, select routes, and manage travel time effectively. An essential tool for Mumbai train passengers, enhancing their travel experience.

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/8e28307a-fcef-4de2-b712-ed9f131dd2ac"/>

## Use-case
"Train Indicator" Android app assists Mumbai local train users by offering information about nearby train stations, and detailing stops for slow and fast trains. This aids in efficient commute planning, route selection, and time management. It benefits both regular commuters and tourists, enabling them to choose stations and trains based on their preferences and urgency. The app enhances navigation, helps in emergencies, and provides insights to avoid crowded trains. It could potentially offer real-time service updates too.

## Guide
Following guide will help to know the steps to use the Train Indicator app.

### Permissions :
Upon installing the aforementioned app, two permissions are essential ie. Location Access and Internet Connection.

<b>Location Access (optional):</b> The app needs permission to access your current location. This allows it to offer a personalized experience, such as providing guidance about the closest fast and slow train stations based on where you are.

<b>Internet Connection (compulsory):</b> An internet connection is required to retrieve the list of Mumbai local train stations from the Firestore database. This ensures that the app has access to the latest and accurate information about the stations.

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/20aec4da-ba15-4d7e-842a-10a8f800ce79" width="350" height="700">

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/78ac6d83-0b5a-493a-8c2b-e2fa828875ba"  width="350" height="700">

### Display Stations :
`Display Station` is a screen where Mumbai local train stations are showcased on a map, each marked with specific icons representing their station status (Slow or Fast station). On each marker station code is written to get the station name in one glance.

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/ee902ade-3b05-459c-93b4-ad30e9d89b48" width="25" height="25">  represents the Slow station and <img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/8f62c213-dc82-412d-820b-d189a64423f9" width="25" height="25"> represents the Fast station.

Simultaneously, the user's location is pinpointed using a marker on the map. This setup offers a visual guide for users to identify stations based on their railway lines and effectively locate their own position within the Mumbai train network. User location is pointed by <img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/dd575b8a-9150-4bb9-8433-e5008181efca" width="25" height="25">

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/f170f930-fb9c-4c5d-a276-40cbe3bae9a4" width="350" height="700">

### Change Railway Line :
The Mumbai local train system consists of three primary railway lines: Western Railway, Central Railway, and Harbour Railway. Users can switch between these lines by clicking on the three dots located in the top-right corner of the app. This feature empowers users to easily toggle between different railway lines, enabling them to access information relevant to their chosen route within the Mumbai local train network.The selected railway line is stored as the user's preference. Even after completely closing and reopening the app, the Display screen will show the stations of the last chosen railway line. This feature provides continuity in user experience by retaining the user's chosen settings.

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/bc0822f1-b4d9-403a-b16f-ffd13d831595" width="350" height="700">

### Navigation View : 
The Navigation View can be accessed through the three-dashed icon in the app's top-left corner. This drawer offers user-centric options, including current location, nearest station details (both slow and fast) and also includes different menus like an app guide, help resources, and an exit option. This feature enhances user interaction and provides easy access to important app functionalities.

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/1a12f9f6-00ca-462e-9fa9-be4de78a63cd" width="350" height="700">

### Nearest Station :
The app offers a `Nearest Station` feature that reveals the closest station based on the selected railway line. This includes both the nearest slow and fast stations. To access this, users should click on the three-dash icon located in the top left corner of the app. The navigation view displays the user's current location, along with details about the nearest slow and fast stations.

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/274f98b5-6101-4b27-bcac-3b2928fa55f0" width="350" height="400">

By clicking on any of these station names, users can view the station name and marker on the 'Display Station` screen. This streamlined feature expedites the station selection process, aiding users in making quick decisions about the right station to choose. To enable this functionality, users need to grant location access, ensuring accurate and timely information.

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/ff9836fd-ff0e-4574-aa20-d6f10544c62c" width="350" height="700">

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/5d0c55d9-0a4d-4f06-8f5a-9228494e2988" width="350" height="700">

### App Guide : 
The `App Guide` feature within the app showcases markers and symbols used throughout the interface, along with their respective meanings. This guide serves to explain the visual cues utilized within the app to users. By providing clear explanations for these markers and symbols, users can better understand the app's visual language, improving their overall navigation and usage experience.

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/55789393-006f-4b6e-9e3a-19d658fa7fbe" width="350" height="700">

### Help :
The `Help` menu option within the app guides users to the app's documentation page. This documentation page provides comprehensive instructions and guidance on how to effectively utilize the app's features and functionalities. This resource assists users in getting the most out of the app and ensures a smoother user experience.

<img src="https://github.com/1405yuga/Train-Indicator/assets/82303711/65603b9a-fc36-425f-affc-0906c2ae06c7" width="350" height="700">

### Exit :
The `Exit` menu option serves to close the app. Users can select this option to exit and close the application completely.
