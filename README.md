# MyContactsApp-BasicOOPS

# Use Case - 1 : New User Registration

# Use Case - 2 : User Authentication

# Use Case - 3 : User Profile Management

# Use Case - 4 : Create Contact

# Use Case - 5 : View Contact Details

# Use Case - 6 : Edit Contact

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
  - simple reference id per user from owner name (example `ALI1`, `ALI2`) + `UUID` + `LocalDateTime` timestamps
- Logged-in user can view full contact details by simple contact reference ID with:
  - immutable view object (`ContactDetailsView`)
  - Optional handling for nullable fields (address/notes/extra fields)
  - formatted display output
- Logged-in user can edit existing contacts with:
  - setter methods with validation
  - copy constructors (`PersonContact`, `OrganizationContact`) for safe draft editing
  - deep copy + defensive copying for phone/email lists before saving
