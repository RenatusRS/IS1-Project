# Banking System

> Project for **Information Systems 1** class at **UoB ETF** in the **2021/2022** school year

Banking transaction system simulation implemented in **Java**.

Supports actions such as creating a new account, transfering funds from one account to another, getting user information, and more.

The system is composed of several applications.
___
### Client Application

**Swing** application used by bank's users to send communicate with the banking system.

The application sends **REST** request to the *Central Server* by using **Retrofit**.
___
### Central Server

Acts as a middleman between the *Client Application* and the *Subsystems*.

Processes the requests by passing them to the appropriate subsystem using **JMS**.
___
### Subsystems

The subsystems process **JMS** messages received from the *Central Server*. Data on them is stored in a **MySQL** database.

#### User Subsystem

Handles creation and storing of user accounts and bank locations. 

#### Transaction Subsystem

Handles creation of bank accounts and processes transactions.

#### Backup Subsystem

Periodically sends requests to other subsystems to gather all the data they have and then stores that data.
