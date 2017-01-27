#DKA-robo
DKA-robo is a system that allows to use a mobile agent to update those statements of a knowledge base that have lost validity in time.

This code allows reproducing the experiments presented in : 
> Tiddi, I., Daga, E., Bastianelli, E. and d’Aquin, M. (2016) [*Update of time-invalid information in Knowledge Bases through Mobile Agents*](http://aass.oru.se/Agora/MIRROR-2016/papers/MIRROR16_paper_2.pdf), Workshop: Integrating Multiple Knowledge Representation and Reasoning Techniques in Robotics (MIRROR-16) at 2016 IEEE/RSJ International Conference on Intelligent Robots and Systems (IROS 2016), Deajeon, South Korea  


## General Idea ##
The general problem is that some statements in a knowledge base with both static (e.g. location of a room) and dynamic information (e.g. temperature of the room) might not be valid anymore after some time. The idea of this work is to update information of a knowledge base that is no longer valid using an autonomous agent as mobile sensor.

## System description ##
The system works as follows:
- **Query** : a user asks a SPARQL query to the KB 
- **Invalid** Information Collection: a Collector gathers the triples of the knowledge base that are no longer valid 
- **Planning** : the Planner receives the invalid triples and makes a plan based on the location of the robot
- **Knowledge Base Update**: the robot receive and execute the plan, and sends the new collected information to the KB, which is updated

## How-tos ##

###Start the server ###

Clone the repo in your desired location, cd into the server directory, then run

```
sh run.sh -l KB_partial.nq
```

this will start your server on port 8080, loading the knowledge base “KB_partial.nq” into a triple store. After the first time you can simply run 

```
sh ./run.sh 
```
to start the server. 

The server allows the following operations:
- connect the system with a robot
- query the knowledge base
- see the plan that should to be sent to the robot
- update the knowledge base through sending the plan to the robot
- ask the robot for its location, status of the execution of the plan, or if it is busy

###Send a query ###

If you simply want to send a SPARQL query to the KB, run
```
sh ./dka.sh query "your_query"
```
and it will return the answer to your query. If some values are shown as "-1” that means the triple is not valid anymore.

###Show the plan###
You can obtain the plan that the planner would compute given a specific query and the location of the robot. Simply run
```
sh ./dka.sh plan "your_query"
```
and will show you  the plan that is computed 

###Execute plan and KB update###
You can send the plan to the robot and update the knowledge base using the plan computed by the DKA, running 
```
sh ./dka.sh send “your query”
```
###Ask the robot###
-  Where it is on the map: 
```
sh.dka.sh wru
```
-  Which action it is performing:
```
sh dka.sh doing
``` 
-  Whether it is busy: 
```
sh dka.sh isbusy
```
Note that you need to have a server installed on your robot, e.g. https://github.com/McKracken/kmi_ros/tree/master/dynamic_knowledge_acquisition/scripts

##Additional Info##
###Other queries###

Which is the best wi-fi signal in the open space activities?
```
select ?room ?wifiSignal where {graph ?expiryDateInMs { VALUES(?room) { ( <http://data.open.ac.uk/kmi/location/Activity2> ) (<http://data.open.ac.uk/kmi/location/Activity3>  )(<http://data.open.ac.uk/kmi/location/Activity4> ) (<http://data.open.ac.uk/kmi/location/Activity5> ) }  ?room <http://data.open.ac.uk/kmi/robo/hasWiFiSignal> ?wifiSignal. } } ORDER BY DESC(?t) LIMIT 1
```

Which is the temperature of room 20 and room 22?
```
select ?room ?temp where {graph ?expiryDateInMs { VALUES(?room) { ( <http://data.open.ac.uk/kmi/location/Room22> ) (<http://data.open.ac.uk/kmi/location/Room20>) }  ?room <http://data.open.ac.uk/kmi/robo/hasTemperature> ?temp.  } }

What's the most comfortable meeting room between Room 22 and the Podium? (comfort score= (temp*hum)/nbOfPeople) 
```select ?room ( (<http://www.w3.org/2001/XMLSchema#float>(?temp)+<http://www.w3.org/2001/XMLSchema#float>(?h))/<http://www.w3.org/2001/XMLSchema#float>(?ppl) AS ?comfort)  where {
graph ?g { VALUES(?room) {  (<http://data.open.ac.uk/kmi/location/Room22> ) (<http://data.open.ac.uk/kmi/location/Podium> ) } .  ?room <http://data.open.ac.uk/kmi/robo/hasPeopleCount> ?ppl }.  graph ?g1 { VALUES(?room) {  (<http://data.open.ac.uk/kmi/location/Room22> ) (<http://data.open.ac.uk/kmi/location/Podium> )} .  ?room <http://data.open.ac.uk/kmi/robo/hasHumidity> ?h }.
graph ?g2 { VALUES(?room) {  (<http://data.open.ac.uk/kmi/location/Room22> ) (<http://data.open.ac.uk/kmi/location/Podium> ) } .  ?room <http://data.open.ac.uk/kmi/robo/hasTemperature> ?temp }. }
```

###Use your own Knowledge base###
You can create your own knowledge base describing your environment and load it into the triple store, using the java class KnowledgeBaseBuilder.java in the dka.sparql package. Simply pass to it a file in the format  <room,coordinateX,coordinateY>

###Change your time-validity rules###
Some rules loaded at start will tell the DKA if a trpile should be considerer valid or not. You can add, amend or delete the rules in the file rules.csv in the server directory

##Enquiries
Sumbit issues and suggestions on this repo, or simply e-mail the authors name.surname@open.ac.uk
- Enrico Daga
- Ilaria Tiddi
- Emanuele Bastianelli
