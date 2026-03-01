# MyContactsApp-BasicOOPS

# Use Case - 1 : New User Registration
# Use Case - 2 : User Authentication
# Use Case - 3 : User Profile Management
# Use Case - 4 : Create Contact

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
- Logged-in user can:
  - update profile (name/email)
  - change password (with current password verification)
  - manage preferences (email notifications, language, contact view)
- Logged-in user can create contacts with:
  - contact hierarchy (`PersonContact`, `OrganizationContact`)
  - composition (`PhoneNumber`, `EmailAddress`)
  - multiple phones/emails via `List`
  - `UUID` contact id + `LocalDateTime` timestamps
