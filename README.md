### Money Transfer API

Money Transfer API contains:
*   users - user which can have 0-infinity accounts
*   currency rates - rates from one currency to another
*   account - account contains userId, balance and currency of balance
*   transactions - transaction contains ids of sender and receiver account, amount and currency of transaction

### Available RESTFul methods
|METHOD|PATH|USAGE|
|-----|-----|-----|
|POST|/application/account/create/|register account and save|
|GET|/application/account/get/{id}|return account by id from database|
|POST|/application/currency/create/|register currency rate and save|
|POST|/application/transaction/create/|register transaction and trigger transaction processing|
|GET|/application/transaction/get/{id}|return transaction by id from database|
|POST|/application/user/create/|register user and save|
|GET|/application/user/get/{id}|return user by id from database|

### A few words about transactions

MoneyTransactions have free status(PROCESSING, DONE, FAILED):
*   PROCESSING - this status set when API create transaction and start process it
*   DONE - this status set when transaction successfully accept 
*   FAILED - this status ser when something go wrong during processing. For example if we can't find necessary accounts/currency rates, or one of account does not have enough money to accept the transaction.

### How to run

You need JDK8, maven >= 3.2.5, available port 8080, 9001 on your machine

Command:
```bash
mvn clean install
```
will build project and launch unit tests.
As a result, you will have jar to run: "money-transfer-1.0-SNAPSHOT.jar"
You can launch it with command using config file "config.yml":
```bash
java -jar <path-to-jar> server <path-to-config>  
```
Also API will create a log file "money-transfer.log"

### Examples

As example you can run these files:
*   example.sh - this script create example rates/accounts and requesting transaction processing. This example shows normal work of API.
*   exampleOfNotValidBalance.sh - this script do the same as example.sh but create one of account with not valid balance. This example shows work of API in exception case.


