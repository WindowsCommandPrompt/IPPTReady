# MAD-Assignment
We have developed a mobile application called <b> IPPTReady </b> for Android for our MAD Assignment in 2022.

<br> 
<hr> 
<br> 

## Collaborators 
<table> 
  <tr> 
    <th>Group member name</th> 
    <th>Group member student ID</th> 
  </tr> 
  <tr> 
    <td>Li Zhe Yun</td> 
    <td>S10222023J</td> 
  </tr> 
  <tr> 
    <td>Joshua Ng</td> 
    <td> </td> 
  </tr> 
  <tr>
    <td>Tan Zhen Xuan Raiden</td> 
    <td> </td> 
  </tr>
  <tr> 
    <td>Bryan Koh</td> 
    <td> </td> 
  </tr> 
  <tr> 
    <td>Ho Kuan Zher</td> 
    <td> </td> 
  </tr> 
</table> 

<br> 
<hr>
<br> 

## Notable bugs during the entire development process
<ol> 
  <li align = "justify"> The Firebase Firestore database is unstable. Sometimes it works and sometimes it does not. This would leave room for inconsistency within our application as this is because we cannot 100% guarantee that the application would be able to perform and produce the desired output. If the Firebase Firestore fails in between while the application is being used by the user, then there will be at least a 50% chance that the application will not be able to function as expected </li>
  <li align = "justify"> <b> NULLPOINTEREXCEPTION! </b>. Really annoying error. </li> 
  <li align = "justify"> The values that were supposed to pass from one activity to the other via the intent, is not functioning, ,data is not passed over and then it would result in us not being able to get the expected results from the application. There were some instances where the application was not able to return the correct results when the application has run to a point whereby it is required to return a back a result from some logic based handling.  </li> 
</ol> 

<br>
<hr> 
<br> 


## Features of our application 
<ol> 
  <li align = "justify">Create and complete your cycles and routines to track and record your scores!</li> 
  <li align = "justify">Track your 2.4km, Sit-up, Push-up in the routines and calculate your score according to the Individual Physical Proficiency Test standard!</li>
  <li align = "justify">View your past cycles and routines to know how well you did in the past!</li>
  <li align = "justify">Watch videos related to the Individual Physical Proficiency Test to hone your skills!</li>
  <li align = "justify">Read about the rules and guidelines of the Individual Physical Proficiency Test to know more about it!</li>
  <li align = "justify">View your profile to see your details!</li> 
</ol> 


<br> 
<hr> 
<br> 

## Individual group members contributions
<table> 
  <tr> 
    <th>Group member name</th> 
    <th>Group member contributions</th>
  </tr> 
  <tr> 
    <td>Li Zhe Yun</td> 
    <td> 
      <ol>
        <li align = "justify">Conceptualized the initial design of implementing a stopwatch in the Run, and a countdown timer in the SitUp and PushUp activities</li> 
        <li align = "justify">Conceptualized the initial design for the Run activity, including where the stopwatch should be positioned with the Run activity, that includes the start and stop buttons needed for the user to start and stop the timer</li>
        <li align = "justify">Implemented the stopwatch in the Run activity, and then implemented a countdown timer in the SitUp and the PushUp activities</li> 
        <li align = "justify">Implemented the rough user interface for the Run activity, PushUp activity based on the initial conceptualized design</li> 
        <li align = "justify">Ensured that the user is able to exit the Run and PushUp activity to navigate to other activities within the application</li> 
        <li align = "justify">Added multiple user dialogs which prompts the user for permissions before actually performing an action programmatically within the application. This is to let the user what actually happens once the user clicks on a certain button, container, present within the layout</li> 
        <li align = "justify">Ensured that both the stopwatch and the countdown timer in the Run and PushUp activity is synchronized</li> 
        <li align = "justify">Ensured that the timing that is recorded within the Run activity and the number of push ups done from the PushUp activity has been successfully recorded and transferred from one intent to another and eventually into the Firebase Firestore database</li> 
      </ol> 
    </td> 
  </tr> 
  <tr> 
    <td>Joshua Ng</td> 
    <td>
      <ol> 
        <li align = "justify">Managed the entire incoming and outgoing traffic into and out of the Firebase Firestore database to ensure that all the required data has been recorded in the correct collection and the correct document within the Firebase Firestore database</li> 
        <li align = "justify">Helped to create the classes and the layouts needed to develop the skeleton for our mobile application for the first-iteration, to ensure that our application will be up and running</li> 
        <li align = "justify">Conceptualized the initial design of the home page of the application when the user has logged into the application using a google account</li>
        <li align = "justify">Helped to ensure that our codes are efficient, through reducing the need for writing redundant code, or repeated code, over and over again</li>
        <li align = "justify">Implemented the idea of using a google sign in services that would enable the user to be able to sign into the application using  valid google account</li> 
        <li align = "justify">Implemented the account handling procedure within the application, which includes the creation of new IPPTReacy user profile accounts for the mobile application, and logging user in if they have already registered themselves under the application</li>
        <li align = "justify">Conceptualized the initial design of the account creation activity that the user can use to register with the application</li> 
        <li align = "justify">Implemented the initial design of the account creation activity</li>
      </ol> 
    </td> 
  </tr> 
  <tr> 
    <td>Bryan Koh</td> 
    <td>
      <ol> 
        <li align = "justify">Helped to refine the code that has been written for the PushUp, SitUp and the Run activities, such that the codes were more efficient</li> 
        <li align = "justify">Helped to implement the count down timer within the SitUp activity</li>
        <li align = "justify">Implemented the user interface for the SitUp activity</li> 
        <li align = "justify">Ensured that the user would be able to exit the SitUp activity and navigate to the other activities</li> 
        <li align = "justify">Ensured that the data is correctly passed from one activity to another and eventually posted to the Firebase Firestore database through the usage of the <code>Intent</code> class</li> 
        <li align = "justify">Implemented the YouTube API and ensured that the videos that were needed is able to show up within the recycler's view under the Video Activity</li>
        <li align = "justify">Conceptualized the initial design of the Video activity</li>
      </ol> 
    </td> 
  </tr> 
  <tr> 
    <td>Tan Zhen Xuan Raiden</td> 
    <td> 
      <ol> 
        <li align = "justify">Conceptualized the initial design of the Information activity</li> 
        <li align = "justify">Implemented the user interface for the information activity based on the initial conceptualized design made earlier</li> 
        <li align = "justify">Ensured that the user is able to exit the information activity and navigate to the other activities</li>
        <li align = "justify">Provided all the drawables (vector assets) needed to be implemented into the information activity layout</li>  
      </ol> 
    </td> 
  </tr> 
  <tr> 
    <td>Ho Kuan Zher</td> 
    <td> 
      <ol> 
        <li align = "justify">Conceptualized the initial design of the profile activity</li> 
        <li align = "justify">Implemented the user interface for the profile activity based on the initial conceptualized design made earlier</li> 
        <li align = "justify">Ensured that the user profile information is extracted from the Firebase Firestore database from the correct collection and the correct document and then displaying the required information in the correct TextView(s) within the activity layout</li> 
        <li align = "justify">Ensured that the user is able to exit the profile activity</li> 
      </ol> 
    </td> 
  </tr>  
</table> 

<br> 
<hr> 
<br> 

## Appendices
<ol> 
  <li align = "justify"></li>
  <li align = "justify"></li>
</ol> 

<br> 
<hr> 
<br> 

## Acknowledgements 
<p align = "justify">The development would like to express their words of thanks to the organisation(s), individual(s) for providing their code, frameworks, software, application programming interface(s) (APIs) for making this app development project possible. </p> 
<ol> 
  <li align = "justify"><b>Android studio</b></li> 
  <ol> 
    <li align = "justify"> Developer:<b> JetBrains </b></li> 
    <li align = "justify"> Software Version: <b> Bumblebee 2020 </b></li> 
    <li align = "justify"> Software Type: <b> Integrated Development Environment Software (IDES) </b> </li> 
  </ol> 
  <li align = "justify"><b>YouTube API </b></li> 
  <ol> 
    <li align = "justify"> Developer:<b> Google LLC </b></li> 
    <li align = "justify"> Software Version: <b>2.0.0</b></li> 
    <li align = "justify"> Software Type: <b> Application Programming Interface (API) </b></li> 
  </ol> 
</ol>

<br> 
<hr> 
<br> 
