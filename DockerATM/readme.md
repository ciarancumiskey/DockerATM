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

0. Clone this Github repo.
1. Navigate to the root folder of this project, and run:
   ```./gradlew build; java -jar build/libs/gs-spring-boot-docker-0.1.0.jar```
2. Run ```docker build -t docker_atm:latest .``` to create the Docker image.
3. Run ```docker-compose up```, which will start a Postgres container along with one for DockerATM.
4. Access the Postgres container using ```docker exec -it postgres bash```.
5. Log into Postgres using ``PGPASSWORD=changeMe! psql -U ciara``.
6. Copy the contents of `Patch-20221001-01-createCustomerTable.sql` to create a database for DockerATM with a 
`customer` table.
7. Copy the contents of `Patch-20221007-01-createTransactionTable.sql` to create a `transaction` table.


## Example requests

### Check balance
```curl -X POST -H 'Content-Type: application/json' localhost:8081/balance -d '{\"accountNumber\":\"<ACCOUNT_NUMBER>\", \"accountPIN\": \"<PIN>\"}'```

#### Powershell version
```curl.exe -X POST -H 'Content-Type: application/json' localhost:8081/balance -d '{\"accountNumber\":\"<ACCOUNT_NUMBER>\", \"accountPIN\": \"<PIN>\"}'```

### Withdraw money
```curl -X POST -H 'Content-Type: application/json' localhost:8081/withdraw -d '{\"withdrawalAmount\":\"<WITHDRAWAL_AMOUNT>\", \"accountNumber\":\"<ACCOUNT_NUMBER>\", \"accountPIN\": \"<PIN>\"}'```

#### Powershell version
```curl.exe -X POST -H "Content-Type: application/json" localhost:8081/withdraw -d '{\"withdrawalAmount\":\"290\", \"accountNumber\":\"123456789\", \"accountPIN\": \"1234\"}'```
