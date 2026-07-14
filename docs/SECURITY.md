# Security Configuration

## Sensitive Information Handling

This project uses environment variables to keep sensitive information (like passwords and API keys) out of version control.

## Local Development Setup

### 1. Scripts Configuration

Scripts now use environment variables instead of hardcoded credentials.

**Setup:**
```bash
# Copy the template
cp scripts/.env.local.example scripts/.env.local

# Edit with your credentials
nano scripts/.env.local
```

**Required variables:**
- `MAIL_FROM_EMAIL` - Your Gmail address
- `MAIL_PASSWORD` - Gmail App Password (NOT your regular password)

### 2. IntelliJ Run Configurations

For DEV/PROD profiles:

1. **Local only files** (gitignored):
   - `.run/AdventistPortalApi [DEV].run.xml` - Contains real DEV credentials
   - `.run/AdventistPortalApi [PROD].run.xml` - Contains real PROD credentials

2. **What you share** (in repo):
   - `.run/AdventistPortalApi [ORB].run.xml` - Local development, no secrets
   - Templates or documentation for DEV/PROD setup

## What's Safe to Commit

✅ **Safe (commit these):**
- `scripts/.env.local.example` - Template without real values
- `application-orb.yml` - Local Docker credentials (postgres/postgres, etc.)
- `.run/AdventistPortalApi [ORB].run.xml` - Only local development config

❌ **Never commit:**
- `scripts/.env.local` - Your personal credentials
- `.run/*[DEV].run.xml` - DEV environment credentials
- `.run/*[PROD].run.xml` - PROD environment credentials
- Any file with real passwords, tokens, or API keys

## GitIgnore Rules

The `.gitignore` includes:
```
# Environment files
.env
.env.*
scripts/.env.local

# Run configurations with secrets
.run/*[DEV]*.xml
.run/*[PROD]*.xml
```

## Getting App Password for Gmail

1. Go to: https://myaccount.google.com/apppasswords
2. Select "Mail" as the app
3. Generate password
4. Copy to your `scripts/.env.local`

## For New Developers

When setting up the project:

1. Clone the repo
2. Copy `scripts/.env.local.example` to `scripts/.env.local`
3. Get your own Gmail App Password
4. Fill in your credentials
5. Run `./scripts/run-orb-app.sh`

Your credentials stay local and are never committed to the repository.

---

**Last updated:** December 3, 2025