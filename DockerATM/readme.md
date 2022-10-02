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
1. Navigate to your Postgres installation's /data/ folder and run the command: ```createdb docker_atm```.
2. Copy the contents of `Patch-20221001-01-createCustomerTable.sql`
3. 
4. Navigate to the root folder of this project, and run: 
   ```./gradlew build; java -jar build/libs/gs-spring-boot-docker-0.1.0.jar```


## Example requests

### Check balance
```curl -X POST -H 'Content-Type: application/json' localhost:8081/balance -d '{\"accountNumber\":\"<ACCOUNT_NUMBER>\", \"accountPIN\": \"<PIN>\"}'```

#### Powershell version
```curl.exe -X POST -H 'Content-Type: application/json' localhost:8081/balance -d '{\"accountNumber\":\"<ACCOUNT_NUMBER>\", \"accountPIN\": \"<PIN>\"}'```

### Withdraw money
```curl -X POST -H 'Content-Type: application/json' localhost:8081/withdraw -d '{\"withdrawalAmount\":\"<WITHDRAWAL_AMOUNT>\", \"accountNumber\":\"<ACCOUNT_NUMBER>\", \"accountPIN\": \"<PIN>\"}'```

#### Powershell version
```curl.exe -X POST -H "Content-Type: application/json" localhost:8081/withdraw -d '{\"withdrawalAmount\":\"290\", \"accountNumber\":\"123456789\", \"accountPIN\": \"1234\"}'```
