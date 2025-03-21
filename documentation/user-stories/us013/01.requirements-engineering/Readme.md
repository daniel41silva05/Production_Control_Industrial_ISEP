# US013 - Register a Component

## 1. Requirements Engineering

### 1.1. User Story Description

As a Production Manager, I want to register a component, so that it can be used in product manufacturing.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

>   The production manager must fill in all the fields for component registration.

>	Each component is characterized by having a unique ID, a name and a description.

### 1.3. Acceptance Criteria

* **AC01:** Component ID, Name, and Description must be provided by the Production Manager.
* **AC02:** All mandatory fields must be filled in.
* **AC03:** System must ensure that the component’s’s ID is unique.

### 1.4. Found out Dependencies

* No dependencies.

### 1.5 Input and Output Data

**Input Data:**

* Typed data:
  * Component ID
  * Name
  * Description

**Output Data:**

* List of components
* (In)Success of the operation
* All data of the new registered component

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/us013-system-sequence-diagram.svg)

### 1.7 Other Relevant Remarks

* n/a