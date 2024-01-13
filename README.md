Project modeled (structure) with the help of the course: https://www.getarrays.io/courses/full-stack-spring-boot-angular-professional 

Own features: 
  - Different database model (relations, tables)
  - Using a different approach
  - Integration with JPA
  - HATEOAS
  - Additional required logic for proprietary solutions
  - Verification feature structure
  - Cache feature
  - DB Migration with Liquibase
  - SMS MFA (unique phone number validation)
  - SOON...

Common features:

## Application User

1.  User new account (unique email address)
    -   Account verification (verify email address)
    -   User profile image
    -   User details (name, email, position, bio, **phone**, address,
        etc)
    -   Being able to update user detail information
2.  User reset password (without being logged in)
    -   Password reset link should expire within 24 hours
3.  User login (using email and a password)
    -   Token based authentication (JWT Token)
    -   Refresh Token seamlessly
4.  Brute force attack mitigation (account lock mechanism)
    -   Lock account on 6 failed login attempts
5.  Role and Permission based application access (ACL)
    -   Protect application resources using roles and permissions
6.  Two-factor authentication (using user phone number)
    -   Send verification code to user's phone
7.  Keep track of user activities (login, account change, etc)
    -   Ability to report suspicious activities
    -   Tracking information
        -   IP Address
        -   Device
        -   Browser
        -   Date
        -   Type

## Customers

1.  Customer Information
    -   Manage customer information(name, address, etc)
    -   Customer can be a person or an institution
    -   Customer should have a **status**
    -   Customer will have invoices
    -   Print customers in spreadsheet
2.  Search Customers
    -   Be able to search customers by name
    -   Pagination

## Invoices

1.  Manage Invoices
    -   Create new invoices
    -   Add invoices to customer
    -   Print invoices for mailing
    -   Print invoices in spreadsheet
    -   Download invoices as PDF
