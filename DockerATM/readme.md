# ATM simulator

## About

This is a simple simulation of an ATM using Spring and PostgresSQL. Customers can use the endpoints to either check
their balance or withdraw money. Customers also have an overdraft that they can dip into in the case of having a 
negative balance.

## Setup

Requirements:
* IntelliJ IDEA
* PostgresSQL

Optional:
* Postman (to save on writing out CURL requests)

1. Clone this Github repo.
2. Open it in IntelliJ.
3. Navigate to your Postgres installation's /data/ folder and run the command: ```createdb docker_atm```.
4. Right-click **DockerAtmApplication**, and select either **Run** or **Debug**.