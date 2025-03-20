# US009 - Register a Product

## 1. Requirements Engineering

### 1.1. User Story Description

As a Production Manager, I want to register a product, so that it can be used in production orders.

### 1.2. Customer Specifications and Clarifications

**From the specifications document:**

>   The production manager must fill in all the fields for product registration.

>	Each product is characterized by having a unique ID, a name, a description, a category, a capacity, a size, a color and the price.

>   The product category can be registered at the time of product registration or already contained in the system.

### 1.3. Acceptance Criteria

* **AC01:** Product ID, Name, Description, Category, Capacity, Size, Color, and Price must be provided by the Production Manager.
* **AC02:** All mandatory fields must be filled in.
* **AC03:** System must ensure that the product’s ID is unique.
* **AC04:** Category must either already exist in the system or be provided during registration.
* **AC05:** Size, Capacity and Price must be positive values.

### 1.4. Found out Dependencies

* No dependencies.

### 1.5 Input and Output Data

**Input Data:**

* Typed data:
  * Product ID
  * Name
  * Description
  * Category
  * Capacity
  * Size
  * Color
  * Price

**Output Data:**

* List of categories
* (In)Success of the operation
* All data of the new registered product

### 1.6. System Sequence Diagram (SSD)

![System Sequence Diagram](svg/us009-system-sequence-diagram.svg)

### 1.7 Other Relevant Remarks

* n/a