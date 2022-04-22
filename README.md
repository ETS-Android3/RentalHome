# RentalHome
Rental Home

Rantal Home 
It is an Android application that is used to find homes in a particular city and also user can advertise their home. We have used JAVA and Firebase as our application's backend mechanism and XML for creating the frontend of the application. 
So application starts with authenticating users using the Firebase Authentication service which is OTP Authentication. A user is created in firebase and basic details are also taken from the user during the registration process. 
Now comes the home Screen of the application in which there is the grid-like structure of the various images of houses and their corresponding rent and location which are available for rent in that particular city. It is displayed by fetching data from the firebase database (which is NO SQL ) in JSON format and it is presented on the screen with the help of recycler view. On clicking it full detail of the house is displayed in another activity along with the owner's contact detail.
There is also an option to advertise a house for rent and it will show a Form asking for various attributes of the house and images as well, upon submitting it will store it onto the firebase database and its images are stored in firebase storage. 
In the account section, there are options to change the user details and edit the house details. At last, there is a Sign Out button.
