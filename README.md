# MyContactsApp-BasicOOPS

# Use Case - 1 : New User Registration

# Use Case - 2 : User Authentication

## What this does

- Create a new user with name, email, password and account type (FREE/PREMIUM)
- Validates email via regex
- Validates password strength
- Hashes password using SHA-256 before saving
- Login with same credential inputs using:
  - BasicAuth (email + password check)
  - OAuth (email + password check + random token generation and storage)
- Uses `Optional` for login results and session lookups
- Supports session management:
  - show logged-in user details
  - logout
