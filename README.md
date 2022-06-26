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
    <td>S10205140B</td> 
  </tr> 
  <tr>
    <td>Tan Zhen Xuan Raiden</td> 
    <td>S10223522</td> 
  </tr>
  <tr> 
    <td>Bryan Koh</td> 
    <td>S10223617</td> 
  </tr> 
  <tr> 
    <td>Ho Kuan Zher</td> 
    <td>S10223870</td> 
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
        <li align = "justify">Ensured that both the stopwatch and the countdown timer in the Run and PushUp activity is synchronized, meaning that the stopwatch would be able to start and stop upon the user's interaction with the run activity, and the countdown timer would be able to reset itself and go back to the initial value of 60 seconds for the countdown timer within the SitUp activity</li> 
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

### Application screenshots
<table>
  <tr> 
    <th>Screenshot name</th> 
    <th>Image</th> 
    <th>Screenshot description</th> 
  </tr> 
  <tr> 
    <td>Screenshot of the login page within the application</td> 
    <td><img src = "https://play-lh.googleusercontent.com/mSvZNqmucHyV_ftZbTvu2sV8FGsIkkgeXSxaFfHX_5JVwYz741PpWcJbuQzCHVWsjA=w5120-h2880-rw" alt = "Screenshot 1(Login page)"/></td> 
    <td align = "justify">To your left shows an image where the user would be able to interact with the application and logs the user into the application. How does the user actually logs into application? The user can just simply tap on the 'Sign in with google' button to sign into the application with a valid google account. If the google account has not been registered with the application, the user can choose to register their gmail with the application</td> 
  </tr> 
  <tr> 
    <td>Screenshot of the home page within the application</td> 
    <td><img src = "https://play-lh.googleusercontent.com/cc2GvZyHtNu7-SPxvSrT817E2aYz7yL1jKKkyd7aRKv4Acm2jdLQWWHAwIFtWUz9nqc=w5120-h2880-rw" alt = "Screenshot 2(Home page)"</td> 
    <td>To your left shows an image of the application's home page when the user has logged into the application. The user would be able to click on one of those icons and the user would be redirected to one of those activities within the application</td> 
  </tr> 
  <tr> 
    <td>Screenshot of a portion of the recycler's view in the information activity</td> 
    <td><img src = "https://play-lh.googleusercontent.com/kcG9Whtl7AHdPIkBH-F8hRI7lfpPcWyaE8_xydYtJgM4G1FVnpisChAnjvv42FN6pQ=w5120-h2880-rw" alt = "Screenshot 3(Information activity recycler's view)" /></td> 
    <td align = "justify">To your left shows an image where the user would be able to learn more about the scoring criteria of the IPPT physical examination, what are some of the awards that the user can obtain upon meeting certain criteria for the IPPT physical assessment, and what are some of the tips that the user can read up on to ensure that they will be able to pass the IPPT assessment. Other information related to the IPPT assessment which includes the computation of the scores can also be found within the Information Activity </td> 
  </tr> 
  <tr> 
    <td>Screenshot of a portion of the recycler's view in the video activity</td> 
    <td><img src = "https://play-lh.googleusercontent.com/IGpNuPOQCt_sCw9xQM2kh9YsbL3Uj-I3TuzXikQaGMvGPgzAW6RlgXf01mpQX2tqTiU=w1052-h592-rw" alt = "Screenshot 4(Video activity recycler's view)"/></td> 
    <td align = "justify">Users would be able to learn more about how to execute sit-ups and push-ups the standard way. Don't know how to train for the 2.4 km run? Don't worry. Just watch those videos and you should be good to go for the IPPT assessment</td> 
  </tr> 
</table> 

### Diagrams that we did to help us with our MAD assignment
<table> 
  <tr> 
    <th>Diagram type</th> 
    <th>Images</th> 
    <th>Diagram description</th> 
  </tr> 
  <tr> 
    <td>Use case diagram</td> 
    <td>
      <img src = "https://static.wixstatic.com/media/3e6e93_36ce8a343c3e453a9922ed0f1d74a2c5~mv2.png" alt = "Use case diagram for MAD assignment" />
    </td> 
    <td align = "justify">This is an image of our use case diagram as we did to help us in completing our MAD assignment. It is important to identify what are some of the main actors that we have</td> 
  </tr> 
  <tr> 
    <td>User story mapping</td> 
    <td>
      <img src = "https://static.wixstatic.com/media/3e6e93_2b6e704125994c3892abccda3b16f180~mv2.png" alt = "User story mapping for MAD assignment" /> 
    </td> 
    <td align = "justify">This is an image of our use</td> 
  </tr> 
  <tr> 
    <td>Class domain diagram</td> 
    <td>
      <img src = "https://static.wixstatic.com/media/3e6e93_2f33d0386d5d4da4af1fc6868dd6d211~mv2.png" alt = "Class domain diagram" />
    </td> 
    <td align = "justify"></td> 
  </tr> 
  <tr> 
    <td>Use case specifications</td> 
    <td> 
      <h3>Use case specification table no.1</h3>
      <table> 
        <tr> 
          <td><b>Use case name</b></td> 
          <td align = "justify">Login Account</td> 
        </tr> 
        <tr> 
          <td><b>Brief Description</b></td> 
          <td align = "justify">The user logs and validates into his/her account and credentials respectively using Google. </td>
        </tr>
        <tr> 
          <td><b>Actor(s)</b></td> 
          <td align = "justify">User, Firebase, Google </td>
        </tr>
        <tr>
          <td><b>Pre-condition(s)</b></td> 
          <td align = "justify">The user must have a valid google account. </td> 
        </tr>
        <tr>
          <td><b>Post-condition(s)</b></td>
          <td align = "justify">The user is taken to the main page. </td>
        </tr>
        <tr>
          <td><b>Basic Flow</b></td>
          <td align = "justify">
            <ol>
              <li> UC starts when user presses the “Sign In with Google” button on the login page. </li> 
              <li> Takes User to UC, “Validate google account”, point 2 of Basic Flow. </li>
              <li> Firebase checks for users with specified email address and password. </li> 
              <li> UC Ends. </li> 
            </ol> 
          </td>
        </tr>
        <tr> 
          <td><b>Alternate Flows</b></td> 
          <td align = "justify">
            <ol> 
              <li align = "justify">Firebase does not have an account with the specified email address and password provided by the user.</li>  
              <li align = "justify">Takes user to UC, “Create Account”, point 5 of Basic Flow. </li> 
              <li align = "justify">UC Ends.</li>  
            </ol> 
          </td>
        </tr> 
      </table> 
      <br> 
      <h3> Use case specification no. 2 </h3>
    </td> 
    <td align = "justify">We did the use case specifications to ensure that we are always going to our plan</td> 
  </tr>
</table> 

<br> 
<hr> 
<br> 

## Research 

<br> 
<hr> 
<br> 

## Link to our application within the google play store
<a href = "https://play.google.com/store/apps/details?id=sg.np.edu.mad.ipptready">Go to our application in google play store!</a>

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
