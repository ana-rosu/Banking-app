# Banking app

Command-line interface (CLI) app which provides a simplified version of a banking system with various actions available to both Bank Administrators and Customers.

## Overview
This banking application consists of multiple classes and services designed to facilitate banking operations. Here are the main components of the application:

-    Account: Abstract class representing a bank account.
        - CheckingAccount
        - SavingsAccount -> Implements callback functions to apply interest rates every 3 months and enforces withdrawal amount limits per month.
- Card: Represents a debit card associated with a bank account -> Includes an expiry date set to 3 years after initialization.
- Transaction
- User
- Address
- UserService -> Provides an interface for user-related operations such as registration, login, and account activation.
- AccountService -> Manages account-related operations such as generating statements
- UserUtils
- AccountUtils
- TransactionUtils
- Transactionable: Interface implemented by classes capable of handling deposits, withdrawals, transfers.
- TransactionType: Enum representing types of financial transactions (e.g., deposit, withdrawal, transfer).
- AccountStatus: Enum representing the status of a bank account (e.g., open, closed).
- AccountType: Enum representing the type of a bank account (e.g., checking, savings)
  
## User Actions
### Customer actions

1) Activate account by setting up a password. The user must be registered by a bank representative first.
2) Login
3) View all accounts associated with the logged-in user
4) Open a new account
5) Account Operations:
    - Check the current balance of an account
    - View Transaction History:
        - Filter transactions by deposit or withdrawal
        - Search transactions by date
        - Sort transactions by date or sum
    - Get Account Statement
    - Transfer funds to another account
    - Issue Debit Card associated with the account
    - View Card Details
    - Close Account

### Bank representative actions

1) Register a new user with the banking application.
2) Display a list of all registered users.


