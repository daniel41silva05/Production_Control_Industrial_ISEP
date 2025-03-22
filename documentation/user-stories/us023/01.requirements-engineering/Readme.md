# US023 - Register a Workstation

## 1. Requirements Engineering

### 1.1. User Story Description

As a Plant Floor Manager, I want to register a workstation, so that production tasks can be assigned to specific machines.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

>   The Plant Floor Manager must fill in all the fields for workstation registration.

>	Each workstation is characterized by having a unique ID and the name.

>   The workstation type can be registered at the time of workstation registration or already contained in the system.

### 1.3. Acceptance Criteria

* **AC01:** Workstation ID and Name must be provided by the Plant Floor Manager.
* **AC02:** All mandatory fields must be filled in.
* **AC03:** System must ensure that the workstation’s ID is unique.
* **AC04:** Workstation Type must either already exist in the system or be provided during registration.

### 1.4. Found out Dependencies

* No dependencies.

### 1.5 Input and Output Data

**Input Data:**

* Typed data:
  * Workstation ID
  * Name
  * Workstation Type

**Output Data:**

* List of workstation types
* (In)Success of the operation
* All data of the new registered workstation

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/us023-system-sequence-diagram.svg)

### 1.7 Other Relevant Remarks

* n/a