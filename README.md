#DKA-robo
DKA-robo is a system that allows to use a mobile agent to update statements of a knowledge base that have lost validity in time.

This code allows reproducing the experiments presented in : 
> Tiddi, I., Daga, E., Bastianelli, E. and d’Aquin, M. (2016) [*Update of time-invalid information in Knowledge Bases through Mobile Agents*](http://aass.oru.se/Agora/MIRROR-2016/papers/MIRROR16_paper_2.pdf), Workshop: Integrating Multiple Knowledge Representation and Reasoning Techniques in Robotics (MIRROR-16) at 2016 IEEE/RSJ International Conference on Intelligent Robots and Systems (IROS 2016), Deajeon, South Korea  


## General Idea ##
The general problem is that dynamic information of knowledge bases (e.g. temperature of the room) might not be valid anymore after some time. The idea of this work is to update such information using an autonomous agent as mobile sensor that sensed the outdate information **on demand**.

## System description ##
The system works as follows:
- **Query** : the user asks a SPARQL query to the KB 
- **Invalid Information Collection**: the collector gathers the triples of the knowledge base that are no longer valid 
- **Planning** : the planner receives the invalid triples and makes a plan based on the location of the robot
- **Knowledge Base Update**: the robot receives and executes the plan, and sends the new collected information to the KB, which is updated

## How-tos ##

###Start the server ###

Clone the repo in your desired location, then compile (you need maven for that) as follows:

```
mvn clean install
```
then move in the server/ directory and run:
```
sh run.sh -l KB_partial.nq
```

this will start your server on port 8080, loading the knowledge base “KB_partial.nq” into a triple store. Note that after first time there is no need to run the -l flag, but just use 
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

###Connect with the robot###
You need to set a connection with the robot which will execute the plan computed by the system.
For this, simply run:
```
sh ./dka.sh setbot "ip_of_your_robot"
```
You can also set a dummy bot which will return fake data. 

Extend the DummyRobot.java class in the dka.bot package if you need.

###Send a query ###

If you simply want to send a SPARQL query to the KB, run
```
sh ./dka.sh query "your_query"
```
This will return the answer to your query. "-1" values mean that the triple is "expired", i.e. it is not valid anymore.

###Show the plan###
You can obtain the plan that the planner would send to the robot in order to update the information of the KB, given a specific query. Simply run
```
sh ./dka.sh plan "your_query"
```
This will return the plan that is computed 

###Execute plan and KB update###
You can send the plan directly to the robot, and consequently update the knowledge base on demand, running 
```
sh ./dka.sh send "your query"
```
###Ask the robot###
-  Where it is on the map: 
```
sh ./dka.sh wru
```
-  Which action it is performing:
```
sh ./dka.sh doing
``` 
-  Whether it is busy: 
```
sh ./dka.sh isbusy
```
Note that you need to have a server installed on your robot, e.g. https://github.com/McKracken/kmi_ros/blob/master/dynamic_knowledge_acquisition/scripts/robot_server.py

##Additional Info##
###Query examples###

Which is the best wi-fi signal in the open space activities?
```
select ?room ?wifiSignal where {graph ?expiryDateInMs { VALUES(?room) { ( <http://data.open.ac.uk/kmi/location/Activity2> ) (<http://data.open.ac.uk/kmi/location/Activity3>  )(<http://data.open.ac.uk/kmi/location/Activity4> ) (<http://data.open.ac.uk/kmi/location/Activity5> ) }  ?room <http://data.open.ac.uk/kmi/robo/hasWiFiSignal> ?wifiSignal. } } ORDER BY DESC(?t) LIMIT 1
```

Which is the temperature of room 20 and room 22?
```
select ?room ?temp where {graph ?expiryDateInMs { VALUES(?room) { ( <http://data.open.ac.uk/kmi/location/Room22> ) (<http://data.open.ac.uk/kmi/location/Room20>) }  ?room <http://data.open.ac.uk/kmi/robo/hasTemperature> ?temp.  } }
```
What is the most comfortable meeting room between Room 22 and the Podium? (comfort score= (temp*hum)/nbOfPeople) 
```
select ?room ( (<http://www.w3.org/2001/XMLSchema#float>(?temp)+<http://www.w3.org/2001/XMLSchema#float>(?h))/<http://www.w3.org/2001/XMLSchema#float>(?ppl) AS ?comfort)  where {
graph ?g { VALUES(?room) {  (<http://data.open.ac.uk/kmi/location/Room22> ) (<http://data.open.ac.uk/kmi/location/Podium> ) } .  ?room <http://data.open.ac.uk/kmi/robo/hasPeopleCount> ?ppl }.  graph ?g1 { VALUES(?room) {  (<http://data.open.ac.uk/kmi/location/Room22> ) (<http://data.open.ac.uk/kmi/location/Podium> )} .  ?room <http://data.open.ac.uk/kmi/robo/hasHumidity> ?h }.
graph ?g2 { VALUES(?room) {  (<http://data.open.ac.uk/kmi/location/Room22> ) (<http://data.open.ac.uk/kmi/location/Podium> ) } .  ?room <http://data.open.ac.uk/kmi/robo/hasTemperature> ?temp }. }
```

###Use your own knowledge base###
You can create your own knowledge base describing your environment and load it into the triple store, using the java class KnowledgeBaseBuilder.java in the dka.sparql package. Simply pass to it a file in the format  <room,coordinateX,coordinateY>

###Change your time-validity rules###
Some rules loaded at start will tell the DKA if a trpile should be considerer valid or not. You can add, amend or delete the rules in the file rules.csv in the server directory

##Enquiries
Sumbit issues and suggestions on this repo, or simply e-mail the authors name.surname@open.ac.uk
- Enrico Daga
- Ilaria Tiddi
- Emanuele Bastianelli
