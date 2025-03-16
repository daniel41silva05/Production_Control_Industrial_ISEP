# US003 - Update Client Data

## 1. Requirements Engineering

### 1.1. User Story Description

As an Administrator, I want to update client data, so that their information remains accurate.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

>   The administrator must be able to modify all client fields except the ID.

>	Each client is characterized by having a unique ID, a name, an address, a type of identification document (vatin), two contact details (phone number and email), and a company type.

### 1.3. Acceptance Criteria

* **AC01:** The system must ensure that the client ID is valid before attempting to update it.
* **AC02:** Client ID cannot be modified.
* **AC03:** Administrator must be able to update Name, Address, Contact Info (Phone Number and Email), VATIN, and Company Type.
* **AC04:** Phone number must contain exactly 9 digits.
* **AC05:** Email must contain a prefix before the "@" symbol and a domain after it.
* **AC06:** Address must include a street, zip code (in the format "xxxx-xxx"), town, and country.
* **AC07:** Company type must be selected from a predefined list (Individual or Company).

### 1.4. Found out Dependencies

* There is a dependency on "US001 - Register a client" as the client must be registered before it can be updated.

### 1.5 Input and Output Data

**Input Data:**

* Typed data:
  * Client ID
  * Name
  * Vatin
  * Street
  * Zip Code
  * Town
  * Country
  * Phone Number
  * Email

* Selected data:
  * Company Type

**Output Data:**

* List of clients
* (In)Success of the operation
* Updated client data

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/us003-system-sequence-diagram.svg)

### 1.7 Other Relevant Remarks

* n/a