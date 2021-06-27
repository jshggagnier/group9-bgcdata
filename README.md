Group 9 - Project Proposal
CMPT 276 - D100

Ashrey Jairath - Ashreyjairath@gmail.com
Joshua - jshggagnier@gmail.com
Kevin Liu - kevinliu8260@hotmail.com
Maria - mka162@sfu.ca
Ronald Mundell - ronniemundell8@gmail.com

Project Overview

Name of the Application : BGC Data Analysis App
Heroku Webapp Link: https://group9-bgcdata.herokuapp.com/
Github repo link: https://github.com/jshggagnier/group9-bgcdata

How is the problem currently solved?
Currently BGC Engineering uses MS Excel spreadsheet to keep track of work items and headcount. It generates graphs that show how planned capacity lines up with planned work.

Since the spreadsheet is big and hard to maintain, this project aims to create an application that will provide the same functionality as spreadsheet but with intuitive navigation and ability to CRUD work items and positions.

How will this project make life better?
This will help to plan and overview work for BGC Engineering employers. This application is an educational project and will provide the staff with the enhanced visualization of data through several interactive charts.

*This project will reduce the amount of manual data entry in their current system, and provide a more organized platform for BGC employees to view their internal data.

Project features: 

CRUD features for work items and positions.
Work Items consist of:
Name
Date Range
Teams assigned and weights of those teams
Dev or QA
Funding Information
Positions Consist of:
Name (of role or of person)
Starting Date of Position
Team
Role
Whether or not the position is filled
Whether or not the position is permanent
If it is not permanent, when the position will end
Generate 4 graphs (2 optional):
First graph shows planned hiring versus existing employees.
Second graph shows available labour and active projects versus where capacity would be according to the hiring plan. The productivity will be calculated using the productivity data and measures provided by the client at BGC Engineering
Third and fourth graph will visualize a chronological overview of the date ranges of work items and employee durations, and are optional additions that were requested by the employer
Azure Active Directory login granting the users permission to modify and view the database

Target audience
The application is designed for the staff at BGC Engineering Corporation

API
We would like to attempt to use Azure Active Directory as a login API, allowing users with sufficient permissions easy access to the online database. This will integrate with their existing infrastructure and minimize the amount of excess passwords for the employees.


Scope of the project
This project will include research and design strategies that will help build a web-application custom to the client’s requirements. The scope of the web-application will contain a login feature, a graph visualization feature  and variable-length forms that submit data to two separate databases. 


Feasibility
With a team of 5 developers, 2 proficient in front-end, and 3 proficient in back-end, we believe this project is within the scope of our capabilities. This project will provide a challenge,  requiring the team to explore new technologies, but we believe it is an achievable goal given our team’s experience and ability to learn. The use of multiple databases may make this difficult, as well as integrating with an already pre-existing Azure Directory deployment, but with some communication with BGC, and a deeper understanding , we will be able to determine some feasible solution that will fulfill the customers needs. The timeframe given allows for a few deployments, and is rather short, but as long as we maintain an agile approach, we should be able to deploy features of this project rather quickly. Additionally, applying a reuse-oriented process towards graphing, should greatly reduce the amount of time spent developing visualization elements.

Sample stories:
Viewing the Headcount Graph/ Navigating the Site (*Navigation will be developed Iter 1)
Authorized user connects to the main webpage for the project and is prompted to log in, once he has logged in using Azure Active directory, he will be able to see a dropdown bar at the top of the screen, with multiple tabs. There will be three categories, each with two subheadings:
Submit
New Work Item
New Position
View/Edit
Work Item
Position
View Graph
Headcount Graph
Work Item Graph

Upon Clicking the headcount graph, the user will be shown a line graph, with two lines. These lines will be calculated based on the quantity of filled and unfilled positions, as shown below:
The orange line is based on both the confirmed and the planned positions, whereas the blue line ONLY tracks the confirmed filled positions. The points are calculated on a week-by-week basis, and are not scaled by productivity (which we will talk about later).

Adding Positions (* All Submissions will be completed Iteration 1)
	Authorized BGC Engineering employee logs into the application. They may want to add a new empty position, or a finalized hire to the team.
	If they have a position in mind, the authorized employee will click ‘Submit’ and choose ‘New position’. The form will have inputs for:
‘Name of Role/Employee’
‘Starting Date of Position’
‘Team’
‘Role’
Additionally, if the role is already filled, a checkbox would be available, allowing them to submit a finalized hire
Additionally, if the role is a Co-op or other Non-Permanent Role, a checkbox would be available, allowing him to also enter an ending date for the position. 

After filling in information, the employee submits the form and proceeds to see changes in ‘Positions’ tab as well as ‘Head Count’ and ‘Work Item’ graphs. 
If the added position was a finalized hire the ‘Headcount’ graph will show an increase in ‘Headcount Confirmed’ line by 1 for starting and ending dates.  ‘Work Item’ graph will show a gradual increase in “Available Labour” line for projects and dates related to the new hire, which follows a productivity multiplier provided by the employer.
	If the added position was not a finalized hire, after submitting the form, changes will be visible in ‘Positions’ tab, as well as the ‘Headcount’ graph, as the line showing ‘Headcount Planning’ will be raised by 1, and in the ‘Work Item’ graph the line showing ‘Potential Productivity’ will be raised based on the productivity multipliers provided by the employer.

Adding Work Order (* All Submissions will be completed Iteration 1)
The Employee will click on “Submit” and then “New Work Item” which will open a form with input details for the same. The User will have to fill out the following details:
Name of Job
Date range (Week to Week)
Name of Team(s) assigned 
Dev or QA
A table of individually assigned weights, based on the date range, that allows the user to weight each work week
Each team will have a separate row of the table, and can be assigned individually
The row will have 1 column for each week the project proceeds
An input section for funding information
These individually assigned weights will be applied to the work item graph, and form the bar chart that the labour force is compared against.

Viewing the Work Item Graph
The workforce will have different productivity depending on whether they are Co-op students or full time employees. For example - the newly hired Co-op students will be functioning at about 10% of their productivity because they’ll be in the onboarding process and will be learning about the workflow of the company. It will increase gradually with the highest possible productivity for a permanent full time employee being .875, and the highest for a Co-op student being 0.65. This gradual change will be incorporated in the work orders graph and this will modify the productivity line.


Editing a Work Item/Unfilled Position/Filled Position
	Authorized user will complete the login procedure, and navigate to View/Edit -> Work Item (or Position). The page will list all valid Work Items or Positions in a table and filter them based on the current year (6 months before and after). Each item will be available to modify and will actively change the visualization shown below the items in the table in the form of a gantt chart.
	Upon modifying one of the work orders or positions, the user will be able to change any piece of data from the element, as if they are submitting a brand new element. From here the user should have the ability to either submit changes, or to cancel and return to the view/edit page.
	Additionally, if possible, clicking upon one of the elements from one of the charts should link to that element’s editing page.

Iteration 1:
For Iteration 1, we will be developing the various classes and form submissions required for the two databases. Additionally, we will work on the azure directory login and the navigation bar for the website.
Group Meetings
Every Tuesday at 11 AM via discord
Additionally, We have arranged meetings for Wendsdays at 10:30 AM with the BGC representative, to go over progress reports and such.