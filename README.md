# How to run project

# Architecture
*Text about architecture. Overarching vision/purpose of the following elements.*

## Frameworks/languages (and essential libraries)
*And what they are used for*

## List of Components
*And what they are used for*

## API calls (SOAP, REST or *Internal Logic*)
1.1) Warehouse receives "start production" command signal&emsp; (SOAP) <br>
1.3) Warehouse sends task completion signal with item id &emsp; (INTR) <br>
2.1) AGV receives 'component pick-up' command signal &emsp; (REST) <br>
2.3) AGV sends 'movement complete' signal &emsp; (INTR) <br>
2.4) AGV receives pick-up signal &emsp; (REST) <br>
2.6) AGV sends 'confirm pick-up' signal &emsp; (INTR) <br>
2.7) AGV receives movement instruction signal &emsp; (REST) <br>
2.9) AGV sends 'movement complete' signal &emsp; (INTR) <br>
2.11) AGV sends task completion signal &emsp; (INTR) <br>
3.1) AssemblyLine receives "execute assembly" command signal &emsp; (MQTT) <br>
3.3) AssemblyLine sends confirmation signal &emsp; (INTR) <br>
3.8) AssemblyLine sends task completion signal &emsp; (INTR) <br>
4.1) Warehouse receives “storage” command signal &emsp; (SOAP) <br>
4.3A) Warehouse sends 'tray ready' signal &emsp; (INTR) <br>
4.3B) AGV receives pick-up signal &emsp; (REST) <br>
4.5B) AGV sends 'movement complete' signal &emsp; (INTR) <br>
4.7B) AGV receives movement instructions &emsp; (REST)  <br>
4.10) AGV sends task completion signal &emsp; (INTR)  <br>
5.1) Warehouse receives "deposit" command signal &emsp; (SOAP)  <br>
5.2) Warehouse sends confirmation signal &emsp; (INTR) <br>
5.4) Warehouse sends task completion signal &emsp; (INTR) <br>

### Sequence "diagram": Continuous Production
#### Assumptions:
* AGV can contain 1 item (component or finishedProduct)
* Assembly needs [1;N] components to build a finishedProduct
* Assembly can only build 1 drone at a time
* Warehouse have 5 trays, each can hold 1 item
* There is only 1 AGV, 1 Warehouse, 1 AssemblyLine


#### Flags:
AGV:
* **Status**: Boolean (available/busy)
* **Task**: String (MovingToDestination, PickingUp, PuttingDown)
* **Position**: String (Warehouse/Inbetween/Assembly)

Warehouse:
* **AvailableTray**: Boolean
* **Status**: Boolean (available/busy)
* **Task**: String (depositingItem, withdrawingItem)

Assembly:
* **AvailableTray**: Boolean
* **NeedsMoreComponents**: Boolean
* **Assembling**: Boolean
* **ProductReadyForPickup**: Boolean

**Design philosophy**:<br>
Through the use of flags, passing a request through a series of ‘if’-statements, <br>
it should be possible to prompt the next step

#### Actions
1) Warehouse withdraws component
2) AGV picks up component and delivers to Assembly
3) Assembly assemble product
4) AGV picks up product and delivers to Warehouse
5) Warehouse deposits product

Will lead to the following sequence of actions:<br>
(whiteboard) 1-2-3-4-.-5-6-7-.-8-9-10-.-11-12-13-.-14-15-16-.-...<br>
(actions)    1-2-1-3-4-5-2-1-3-4-5-2-1-3-4–5-2-1-3-4-5-2-1-3-4-5

Derived rhythm:<br>
**Start**: 1-2-1 <br>
**Loop**: 3-4-5 &emsp; 2-1 &emsp; 3-4-5 &emsp; 2-1 	&emsp; 3-4-5 &emsp; 2-1

Derived rules, in priority:
1) Present new component for pickup if 2-or-more trays empty
2) Deliver product to Warehouse upon completion
3) Always deposit product into warehouse upon delivery
4) Deliver component to Assembly if more components needed

#### Sequence (actions) with checks
*Each step within an action requires previous steps are successfully completed*
##### 1) Warehouse withdraws component
    1.1) Warehouse receives "start production" command signal
    1.2) Warehouse checks at least 2 trays available
    1.2) Warehouse places requested component into a tray
    1.3) Warehouse moves the tray to the pickup area
    1.3) Warehouse sends task completion signal with item id

##### 2) AGV picks up component and delivers to Assembly
    2.1) AGV receives 'component pick-up' command signal
    2.2) AGV moves to warehouse position
    2.3) AGV sends 'movement complete' signal
    2.4) AGV receives pick-up signal
    2.5) AGV picks up item
    2.6) AGV sends 'confirm pick-up' signal
    2.7) AGV receives movement instruction signal
    2.8) AGV moves to AssemblyLine position
    2.9) AGV sends 'movement complete' signal
    2.10) AGV delivers item to AssemblyLine
    2.11) AGV sends task completion signal

##### 3) Assembly assemble product
    3.1) AssemblyLine receives "execute assembly" command signal
    3.2) AssemblyLine confirms correct item is delivered
    3.3) AssemblyLine sends confirmation signal
    3.4) AssemblyLine confirms enough items have been delivered
    3.5) AssemblyLine sends confirmation signal
    3.6) AssemblyLine executes the assembly instructions
    3.7) AssemblyLine places product for pick-up
    3.8) AssemblyLine sends task completion signal

##### 4) AGV picks up product and delivers to Warehouse
    4.1) Warehouse receives “prepare” command signal
    4.2) Warehouse confirms tray available
    4.3A) Warehouse prepares storage tray
    4.3A) Warehouse sends 'tray ready' signal
    4.3B) AGV receives pick-up signal
    4.4B) AGV moves to AssemblyLine position
    4.5B) AGV sends 'movement complete' signal
    4.6B) AGV picks up item
    4.7B) AGV receives movement instructions
    4.8B) AGV moves to Warehouse
    4.9) AGV delivers item to Warehouse
    4.10) AGV sends task completion signal

##### 5) Warehouse deposits product
    5.1) Warehouse receives "deposit" command signal
    5.1) Warehouse confirms correct item is delivered
    5.2) Warehouse sends confirmation signal
    5.3) Warehouse stores item
    5.4) Warehouse sends task completion signal

### Sequence "diagram": Single Item Production
The order in which things happen, what each action does, and what (if applicable) component it relies on. <br>
To make it continuous, we have to consider how and when to implement coordination.<br>
*Numbers refer to chronology, letters refer to action-thread*

#### 1) Order is sent.
    1A) Warehouse receives "start production" signal
    1.1A) Warehouse picks requested tray and moves the tray to the pickup area
    1.2A) Warehouse sends ready signal with item id
    1B) AGV receives "start production" signal
    1.1B) AGV moves to warehouse position
    1.2B) AGV sends task completion signal
    1.3) AGV receives pick-up signal

#### 2) AGV picks up item
    2.1) AGV sends task completion signal
    2.2) AGV receives movement instruction

#### 3) AGV moves to AssemblyLine position
    3.1) AGV sends task completion signal

#### 4) AGV delivers item to AssemblyLine
    4.1) AGV sends task completion signal

#### 5) AssemblyLine confirms correct item is delivered
    5.1) AssemblyLine sends confirmation signal

#### 6) AssemblyLine executes the assembly instructions
    6.1) AssemblyLine sends task completion signal
    6.2) AGV receives pick-up signal

#### 7) AGV moves to AssemblyLine position
    7.1) AGV sends task completion signal
    7.2) AGV picks up item
    7.3) AGV receives movement instructions

#### 8) Storage preparation
    8A) AGV moves to Warehouse
    8.1A) AGV sends task completion signal
    8B) Warehouse receives “storage” signal
    8.1B)Warehouse prepares storage tray
    8.2B) Warehouse sends task completion signal

#### 9) AGV delivers item to Warehouse
    9.1) AGV sends task completion signal

#### 10) Warehouse confirms correct item is delivered
    10.1) Warehouse sends confirmation signal

#### 11) Warehouse stores item
    11.1) Warehouse sends task completion signal

Note: AGV battery. <br>
Maybe have 2 AGVs on rotation. One active, one charging. <br>
When active AGV’s battery is low, swap them.<br>
Also, possibly have AGV have a “reset” position in the middle to move to in between tasks.
