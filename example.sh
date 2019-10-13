#!/bin/bash

#Create currency rates
printf "\nCreate USD->RUB rate\n"
curl --header "Content-Type: application/json" --request POST --data '{"fromCode":"USD", "toCode":"RUB", "rate":"60"}' http://0.0.0.0:8080/application/currency/create/
printf "\nCreate RUB->EUR rate \n"
curl --header "Content-Type: application/json" --request POST --data '{"fromCode":"RUB", "toCode":"EUR", "rate":"0.016"}' http://0.0.0.0:8080/application/currency/create/

#Create accounts
printf "\nCreate first account\n"
curl --header "Content-Type: application/json" --request POST --data '{"balance":"1000000", "userId":"0", "currency":"USD"}' http://0.0.0.0:8080/application/account/create/
printf "\nCreate second account\n"
curl --header "Content-Type: application/json" --request POST --data '{"balance":"1000000", "userId":"0", "currency":"EUR"}' http://0.0.0.0:8080/application/account/create/

#Create transaction
printf "\nProcess transaction\n"
curl --header "Content-Type: application/json" --request POST --data '{"fromAccountId":"0", "toAccountId":"1", "amount":"100", "currency":"RUB"}' http://0.0.0.0:8080/application/transaction/create/

#Get accounts after processing transaction
printf "\nFirst account:\n"
curl --header "Content-Type: application/json" --request GET http://0.0.0.0:8080/application/account/get/0
printf "\nSecond account:\n"
curl --header "Content-Type: application/json" --request GET http://0.0.0.0:8080/application/account/get/1
printf "\n"

