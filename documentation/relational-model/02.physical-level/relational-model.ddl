-- Drop tables to ensure a clean slate
DROP TABLE IF EXISTS RawMaterialSupplier CASCADE;
DROP TABLE IF EXISTS RawMaterialSupply CASCADE;
DROP TABLE IF EXISTS SupplyOffer CASCADE;
DROP TABLE IF EXISTS Supplier CASCADE;
DROP TABLE IF EXISTS RawMaterial CASCADE;
DROP TABLE IF EXISTS Component CASCADE;
DROP TABLE IF EXISTS Part CASCADE;
DROP TABLE IF EXISTS CanBeDoneAt CASCADE;
DROP TABLE IF EXISTS Workstation CASCADE;
DROP TABLE IF EXISTS WorkstationType CASCADE;
DROP TABLE IF EXISTS Operation CASCADE;
DROP TABLE IF EXISTS OperationType CASCADE;
DROP TABLE IF EXISTS ProductionTree CASCADE;
DROP TABLE IF EXISTS ProductOrder CASCADE;
DROP TABLE IF EXISTS Product CASCADE;
DROP TABLE IF EXISTS ProductCategory CASCADE;
DROP TABLE IF EXISTS "Order" CASCADE;
DROP TABLE IF EXISTS Client CASCADE;
DROP TABLE IF EXISTS Address CASCADE;

-- Table creation
CREATE TABLE Client (
    ClientID int4 NOT NULL,
    AddressID int4 NOT NULL,
    Name varchar(255) NOT NULL,
    Vatin varchar(15) NOT NULL,
    PhoneNumber int4 NOT NULL,
    EmailAddress varchar(255) NOT NULL,
    Type varchar(10) NOT NULL CHECK (Type IN ('Individual', 'Company')),
    State varchar(8) NOT NULL CHECK (State IN ('Active', 'Inactive')),
    PRIMARY KEY (ClientID)
);

CREATE TABLE Address (
    AddressID int4 NOT NULL,
    Street varchar(255) NOT NULL,
    ZipCode varchar(10) NOT NULL,
    Town varchar(50) NOT NULL,
    Country varchar(50) NOT NULL,
    PRIMARY KEY (AddressID)
);

CREATE TABLE "Order" (
    OrderID int4 NOT NULL,
    ClientID int4 NOT NULL,
    DeliveryAddressID int4 NOT NULL,
    OrderDate date NOT NULL,
    DeliveryDate date NOT NULL,
    Price int4 NOT NULL,
    State varchar(9) NOT NULL CHECK(State IN ('Pending', 'Confirmed')),
    PRIMARY KEY (OrderID)
);

CREATE TABLE Product (
    ProductID varchar(255) NOT NULL,
    CategoryID int4 NOT NULL,
    Capacity int4 NOT NULL CHECK (Capacity > 0),
    "Size" int4 NOT NULL,
    Color varchar(255) NOT NULL,
    Price int4 NOT NULL,
    PRIMARY KEY (ProductID)
);

CREATE TABLE ProductCategory (
    ProductCategoryID int4 NOT NULL,
    Name varchar(255) NOT NULL,
    PRIMARY KEY (ProductCategoryID)
);

CREATE TABLE ProductOrder (
    OrderID int4 NOT NULL,
    ProductID varchar(255) NOT NULL,
    Quantity int4 NOT NULL CHECK (Quantity > 0),
    PRIMARY KEY (OrderID, ProductID)
);

CREATE TABLE Operation (
    OperationID int4 NOT NULL,
    OperationTypeID int4 NOT NULL,
    Name varchar(255) NOT NULL,
    ExecutionTime int4 NOT NULL CHECK (ExecutionTime > 0),
    PRIMARY KEY (OperationID)
);

CREATE TABLE ProductionTree (
    ProductID varchar(255) NOT NULL,
    OperationID int4 NOT NULL,
    PartID varchar(255) NOT NULL,
    ParentOperationID int4,
    Quantity int4 NOT NULL,
    PRIMARY KEY (ProductID, OperationID, PartID)
);

CREATE TABLE OperationType (
    OperationTypeID int4 NOT NULL,
    Name varchar(255) NOT NULL,
    PRIMARY KEY (OperationTypeID)
);

CREATE TABLE WorkstationType (
    WorkstationTypeID int4 NOT NULL,
    Name varchar(255) NOT NULL,
    PRIMARY KEY (WorkstationTypeID)
);

CREATE TABLE Workstation (
    WorkstationID int4 NOT NULL,
    WorkstationTypeID int4 NOT NULL,
    Name varchar(255) NOT NULL,
    PRIMARY KEY (WorkstationID)
);

CREATE TABLE CanBeDoneAt (
    OperationTypeID int4 NOT NULL,
    WorkstationTypeID int4 NOT NULL,
    SetupTime int4 NOT NULL CHECK (SetupTime > 0),
    PRIMARY KEY (OperationTypeID, WorkstationTypeID)
);

CREATE TABLE Part (
    PartID varchar(255) NOT NULL,
    Name varchar(255) NOT NULL,
    Description varchar(255) NOT NULL,
    PRIMARY KEY (PartID)
);

CREATE TABLE Component (
    ComponentID varchar(255) NOT NULL,
    PRIMARY KEY (ComponentID)
);

CREATE TABLE RawMaterial (
    RawMaterialID varchar(255) NOT NULL,
    CurrentStock int4 NOT NULL CHECK (CurrentStock > 0),
    MinimumStock int4,
    PRIMARY KEY (RawMaterialID)
);

CREATE TABLE Supplier (
    SupplierID int4 NOT NULL,
    Name varchar(255) NOT NULL,
    PhoneNumber int4 NOT NULL,
    EmailAddress varchar(255) NOT NULL,
    State varchar(8) NOT NULL CHECK (State IN ('Active', 'Inactive')),
    PRIMARY KEY (SupplierID)
);

CREATE TABLE SupplyOffer (
    SupplyOfferID int4 NOT NULL,
    SupplierID int4 NOT NULL,
    DeliveryAddressID int4 NOT NULL,
    StartDate date NOT NULL,
    EndDate date,
    State varchar(9) NOT NULL CHECK(State IN ('Pending', 'Confirmed')),
    PRIMARY KEY (SupplyOfferID)
);

CREATE TABLE RawMaterialSupply (
    SupplyOfferID int4 NOT NULL,
    RawMaterialID varchar(255) NOT NULL,
    Quantity int4 NOT NULL CHECK (Quantity > 0),
    UnitCost int4 NOT NULL,
    PRIMARY KEY (SupplyOfferID, RawMaterialID)
);

CREATE TABLE RawMaterialSupplier (
    SupplierID int4 NOT NULL,
    RawMaterialID varchar(255) NOT NULL,
    ExpectedUnitCost int4 NOT NULL,
    PRIMARY KEY (SupplierID, RawMaterialID)
);

-- Foreign Keys
ALTER TABLE Client ADD CONSTRAINT FK_Client_Address FOREIGN KEY (AddressID) REFERENCES Address (AddressID);
ALTER TABLE "Order" ADD CONSTRAINT FK_Order_Client FOREIGN KEY (ClientID) REFERENCES Client (ClientID);
ALTER TABLE "Order" ADD CONSTRAINT FK_Order_Address FOREIGN KEY (DeliveryAddressID) REFERENCES Address (AddressID);
ALTER TABLE ProductOrder ADD CONSTRAINT FK_ProductOrder_Order FOREIGN KEY (OrderID) REFERENCES "Order" (OrderID);
ALTER TABLE ProductOrder ADD CONSTRAINT FK_ProductOrder_Product FOREIGN KEY (ProductID) REFERENCES Product (ProductID);
ALTER TABLE Product ADD CONSTRAINT FK_Product_Category FOREIGN KEY (CategoryID) REFERENCES ProductCategory (ProductCategoryID);
ALTER TABLE ProductionTree ADD CONSTRAINT FK_ProductionTree_Product FOREIGN KEY (ProductID) REFERENCES Product (ProductID);
ALTER TABLE ProductionTree ADD CONSTRAINT FK_ProductionTree_Operation FOREIGN KEY (OperationID) REFERENCES Operation (OperationID);
ALTER TABLE ProductionTree ADD CONSTRAINT FK_ProductionTree_ParentOperation FOREIGN KEY (ParentOperationID) REFERENCES Operation (OperationID);
ALTER TABLE Operation ADD CONSTRAINT FK_Operation_Type FOREIGN KEY (OperationTypeID) REFERENCES OperationType (OperationTypeID);
ALTER TABLE Workstation ADD CONSTRAINT FK_Workstation_Type FOREIGN KEY (WorkstationTypeID) REFERENCES WorkstationType (WorkstationTypeID);
ALTER TABLE CanBeDoneAt ADD CONSTRAINT FK_CanBeDoneAt_OperationType FOREIGN KEY (OperationTypeID) REFERENCES OperationType (OperationTypeID);
ALTER TABLE CanBeDoneAt ADD CONSTRAINT FK_CanBeDoneAt_WorkstationType FOREIGN KEY (WorkstationTypeID) REFERENCES WorkstationType (WorkstationTypeID);
ALTER TABLE Component ADD CONSTRAINT FK_Component_Part FOREIGN KEY (ComponentID) REFERENCES Part (PartID);
ALTER TABLE RawMaterial ADD CONSTRAINT FK_RawMaterial_Part FOREIGN KEY (RawMaterialID) REFERENCES Part (PartID);
ALTER TABLE SupplyOffer ADD CONSTRAINT FK_SupplyOffer_Supplier FOREIGN KEY (SupplierID) REFERENCES Supplier (SupplierID);
ALTER TABLE SupplyOffer ADD CONSTRAINT FK_SupplyOffer_Address FOREIGN KEY (DeliveryAddressID) REFERENCES Address (AddressID);
ALTER TABLE RawMaterialSupply ADD CONSTRAINT FK_RawMaterialSupply_SupplyOffer FOREIGN KEY (SupplyOfferID) REFERENCES SupplyOffer (SupplyOfferID);
ALTER TABLE RawMaterialSupply ADD CONSTRAINT FK_RawMaterialSupply_RawMaterial FOREIGN KEY (RawMaterialID) REFERENCES RawMaterial (RawMaterialID);
ALTER TABLE RawMaterialSupplier ADD CONSTRAINT FK_RawMaterialSupplier_Supplier FOREIGN KEY (SupplierID) REFERENCES Supplier (SupplierID);
ALTER TABLE RawMaterialSupplier ADD CONSTRAINT FK_RawMaterialSupplier_RawMaterial FOREIGN KEY (RawMaterialID) REFERENCES RawMaterial (RawMaterialID);
ALTER TABLE Product ADD CONSTRAINT FK_Product_Part FOREIGN KEY (ProductID) REFERENCES Part (PartID);
ALTER TABLE ProductionTree ADD CONSTRAINT FK_ProductionTree_Part FOREIGN KEY (PartID) REFERENCES Part (PartID);
