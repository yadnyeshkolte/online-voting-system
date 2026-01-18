# Automated Code Review Setup

This document describes the automated code review process for the Online Voting System repository using CodeRabbit and Lynx.

## Overview

When a pull request (PR) is created targeting the `develop` branch, an automated code review workflow is triggered that performs comprehensive analysis including:

- **CodeRabbit AI Review**: AI-powered code analysis with contextual feedback
- **Lynx Analysis**: Security and code quality scanning
- **Frontend Quality Checks**: JavaScript/React linting and security audits
- **Backend Quality Checks**: Java/Maven verification and testing
- **Security Scanning**: Vulnerability detection with Trivy
- **Dependency Checks**: OWASP dependency analysis
- **Automated Labeling**: PR size and file-based labels

## How It Works

### 1. CodeRabbit AI Review

CodeRabbit provides intelligent, context-aware code reviews using AI:

**What it checks:**
- Security vulnerabilities (SQL injection, XSS, CSRF, etc.)
- Performance issues
- Best practices for JavaScript/React and Java/Spring Boot
- Code style and maintainability
- Potential bugs and edge cases
- Complexity and readability

**Configuration:** `.coderabbit.yaml` in the repository root

**Key features:**
- Line-by-line inline comments on issues
- Summary comments with overall assessment
- Suggestions for fixes
- Educational explanations for issues

### 2. Lynx Code Analysis

Lynx performs deep code analysis and security scanning:

**Important Note:** The Lynx GitHub Action (`lynx-security/lynx-action`) is not publicly available. Lynx may be:
- A commercial tool requiring separate licensing and setup
- Available through a different integration method (CLI, API)
- An alternative tool name

**If you want to use Lynx:**
1. Contact Lynx (if it's a commercial product) for setup instructions
2. Configure the `LYNX_API_KEY` secret in your repository
3. Update the workflow with the correct Lynx integration method

**Alternative tools for similar functionality:**
- **SonarQube/SonarCloud**: Code quality and security analysis
- **Snyk**: Security vulnerability scanning
- **CodeQL**: GitHub's native security analysis (already included in workflow via Trivy)
- **Semgrep**: Static analysis for security patterns

**What Lynx would check (if configured):**
- Security vulnerabilities across OWASP Top 10
- Code complexity metrics
- Duplication detection
- Architecture violations
- Performance bottlenecks
- Testing coverage

**Configuration:** `.lynx.yml` in the repository root (ready for when Lynx is available)

**Current Status:** The Lynx job is configured to skip if the `LYNX_API_KEY` secret is not set, so it won't cause workflow failures.

### 3. Automated Quality Checks

The workflow runs additional automated checks:

#### Frontend Checks
- ESLint for JavaScript code quality
- npm audit for security vulnerabilities
- React best practices validation

#### Backend Checks
- Maven verify for build validation
- Checkstyle (if configured)
- JUnit test execution

#### Security Scans
- **Trivy**: Container and filesystem vulnerability scanning
- **OWASP Dependency Check**: Third-party library vulnerabilities
- **Secret Detection**: Prevents committing sensitive data

### 4. PR Labeling

Automatic labels are added based on:
- **Size**: `size/xs`, `size/s`, `size/m`, `size/l`, `size/xl`
- **Files Changed**: Based on `.github/labeler.yml` configuration
- **Review Status**: After automated review completion

## What Developers Should Expect

### When Creating a PR

1. **Create your PR** targeting the `develop` branch
2. **Automated checks start** within seconds
3. **Review comments appear** on specific lines of code
4. **Summary comment** is posted with overall assessment
5. **Status checks** show pass/fail for each analysis

### Review Comment Types

**🔴 Critical Issues**
- Security vulnerabilities
- Data integrity problems
- Must be addressed before merging

**🟡 Warnings**
- Code quality issues
- Performance concerns
- Should be addressed if possible

**ℹ️ Suggestions**
- Style improvements
- Best practice recommendations
- Optional enhancements

### Timeline

- **CodeRabbit Review**: 1-3 minutes
- **Lynx Analysis**: 2-5 minutes
- **Quality Checks**: 3-7 minutes
- **Security Scans**: 5-10 minutes

Total: 10-25 minutes depending on PR size

## How to Respond to Automated Reviews

### Addressing Comments

1. **Read the comment carefully**: Understand the issue and suggested fix
2. **Evaluate the suggestion**: Automated tools can sometimes be wrong
3. **Make changes if valid**: Commit fixes to your PR branch
4. **Reply to comments**: Explain your reasoning if you disagree
5. **Request re-review**: Tag a human reviewer after addressing issues

### Valid Reasons to Dismiss Comments

- **False Positives**: The tool misunderstood the code
- **Existing Pattern**: Following established repository patterns
- **Acceptable Tradeoff**: Performance/readability balance decision
- **Out of Scope**: Issue exists but not related to your changes

Always explain your reasoning when dismissing automated feedback.

### Example Workflow

```bash
# 1. Make your changes
git checkout -b feature/add-voter-validation

# 2. Commit and push
git add .
git commit -m "Add voter validation logic"
git push origin feature/add-voter-validation

# 3. Create PR on GitHub targeting 'develop'

# 4. Wait for automated reviews (10-25 minutes)

# 5. Address feedback
git add .
git commit -m "Fix SQL injection vulnerability"
git push origin feature/add-voter-validation

# 6. Request human review
```

## Setup Instructions for Repository Maintainers

### Prerequisites

1. **GitHub Repository Admin Access**
2. **CodeRabbit Account** (for AI reviews)
3. **Lynx Account** (for code analysis)

### Step 1: Install CodeRabbit GitHub App

1. Go to [CodeRabbit on GitHub Marketplace](https://github.com/apps/coderabbitai) or search for "CodeRabbit" in GitHub Marketplace
   - **Note**: The exact URL may change. Search for "CodeRabbit" if the link is outdated.
2. Click "Install" or "Set up a plan"
3. Select the `yadnyeshkolte/online-voting-system` repository
4. Grant required permissions:
   - Read access to code
   - Write access to pull requests
   - Write access to checks

### Step 2: Configure CodeRabbit

1. CodeRabbit reads configuration from `.coderabbit.yaml`
2. The configuration is already set up for this repository
3. Review settings at https://coderabbit.ai/dashboard

**Key Configuration:**
- Auto-review enabled for PRs to `develop`
- Detailed review level
- Focus on security, performance, and best practices
- JavaScript and Java language support

### Step 3: Configure Lynx or Alternative Tools (Optional)

**Important:** The Lynx GitHub Action is not publicly available. The workflow is currently configured to skip the Lynx job unless you set up the integration manually.

**Option 1: If Lynx is Available to You**
1. Contact your Lynx provider for integration instructions
2. Obtain API key or access credentials
3. Add secrets to repository:
   - Go to repository Settings → Secrets → Actions
   - Add `LYNX_API_KEY` with your Lynx API key
4. Update the workflow `.github/workflows/pr-review.yml` with the correct Lynx integration method (CLI, API, or Action)

**Option 2: Use Alternative Security/Quality Tools (Recommended)**

Since Lynx may not be accessible, consider these alternatives:

**SonarQube/SonarCloud** (Recommended Alternative):
```yaml
- name: SonarCloud Scan
  uses: SonarSource/sonarcloud-github-action@master
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

**Snyk** (Security Vulnerability Scanning):
```yaml
- name: Run Snyk Security Scan
  uses: snyk/actions/node@master
  env:
    SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
```

**Semgrep** (Static Analysis):
```yaml
- name: Semgrep Scan
  uses: returntocorp/semgrep-action@v1
```

**Current Status:** The Lynx job will be skipped automatically since the `LYNX_API_KEY` is not configured, and the workflow will continue without errors.

### Step 4: Configure GitHub Actions

The workflow file is already created at `.github/workflows/pr-review.yml`.

**Required Repository Settings:**

1. **Enable Actions**:
   - Go to Settings → Actions → General
   - Allow all actions and reusable workflows

2. **Configure Branch Protection** (Optional but recommended):
   - Go to Settings → Branches
   - Add rule for `develop` branch
   - Enable "Require status checks to pass"
   - Select checks: `frontend-quality`, `backend-quality`, `security-scan`

3. **Permissions**:
   - The workflow uses `GITHUB_TOKEN` (automatically available)
   - No additional secrets needed for basic functionality

### Step 5: Set Up Labeler (Optional)

Create `.github/labeler.yml` to auto-label PRs:

```yaml
frontend:
  - frontend/**/*

backend:
  - backend/**/*

documentation:
  - docs/**/*
  - '*.md'

configuration:
  - '*.yml'
  - '*.yaml'
  - '*.json'
  - pom.xml
```

### Step 6: Test the Setup

1. Create a test branch
2. Make a small change
3. Open PR targeting `develop`
4. Verify all checks run successfully
5. Review automated comments

## Required Secrets and Configurations

### GitHub Secrets

| Secret | Required | Description |
|--------|----------|-------------|
| `GITHUB_TOKEN` | ✅ Yes | Automatically provided by GitHub Actions |
| `LYNX_API_KEY` | ⚠️ Optional | Required only if using Lynx with API key authentication |

### GitHub App Installations

| App | Required | Purpose |
|-----|----------|---------|
| CodeRabbit | ✅ Recommended | AI-powered code review |
| Lynx | ⚠️ Optional | Advanced code analysis (if available) |

### Repository Settings

- ✅ **Actions enabled**: Settings → Actions → Allow all actions
- ✅ **PR permissions**: Write permissions for workflows
- ⚠️ **Branch protection**: Optional but recommended for `develop`

## Customization

### Adjusting CodeRabbit Settings

Edit `.coderabbit.yaml`:

```yaml
# Change review level
reviews:
  level: "basic"  # or "standard" or "detailed"

# Adjust complexity threshold
complexity:
  max_complexity: 20  # Increase tolerance

# Disable specific checks
javascript:
  checks:
    - no_console_log: false  # Allow console.log
```

### Adjusting Lynx Settings

Edit `.lynx.yml`:

```yaml
# Change security threshold
security:
  vulnerabilities:
    severity_threshold: "high"  # Only report high/critical

# Adjust quality thresholds
thresholds:
  quality_score: 60  # Lower threshold
```

### Modifying Workflow

Edit `.github/workflows/pr-review.yml`:

```yaml
# Run only on specific labels
on:
  pull_request:
    types: [opened, synchronize, labeled]
    branches: [develop]

# Add additional jobs
jobs:
  custom-check:
    runs-on: ubuntu-latest
    steps:
      - name: Custom validation
        run: echo "Add your custom checks here"
```

## Voting System Security Focus

The automated reviews are configured with special attention to voting system security:

### Critical Security Checks

1. **SQL Injection Prevention**
   - Parameterized queries
   - Input sanitization
   - ORM best practices

2. **XSS Protection**
   - Output encoding
   - Content Security Policy
   - Sanitized user input

3. **Authentication & Authorization**
   - JWT token security
   - Role-based access control
   - Session management

4. **Vote Integrity**
   - Vote counting logic
   - Tamper detection
   - Audit trails

5. **Data Protection**
   - Encryption at rest
   - Secure communication (HTTPS)
   - Personal data handling

### Custom Rules

Special patterns are detected for voting system code:
- Vote counting and tallying logic
- Election management endpoints
- Voter authentication flows
- Ballot submission handlers

## Troubleshooting

### CodeRabbit Not Commenting

**Possible causes:**
- GitHub App not installed
- App permissions insufficient
- PR not targeting `develop` branch

**Solution:**
1. Verify app installation in GitHub settings
2. Check `.coderabbit.yaml` branch configuration
3. Re-trigger workflow by pushing a new commit

### Lynx Step Failing

**Possible causes:**
- Lynx not configured
- API key missing
- Service unavailable

**Solution:**
- Check `continue-on-error: true` is set (non-blocking)
- Verify Lynx configuration and API key
- Contact Lynx support if persistent

### Quality Checks Failing

**Common issues:**
- Linting errors
- Missing dependencies
- Build failures

**Solution:**
1. Run checks locally first: `npm run lint`, `mvn verify`
2. Fix issues before pushing
3. Check logs in GitHub Actions for details

### Workflow Not Triggering

**Checklist:**
- ✅ PR targets `develop` branch
- ✅ GitHub Actions enabled in repository
- ✅ Workflow file in correct location
- ✅ YAML syntax valid

## Support and Feedback

### Getting Help

1. **Review workflow logs**: Actions tab → Failed workflow → Check logs
2. **Check documentation**: This file and tool-specific docs
3. **Ask maintainers**: Create an issue or discussion
4. **Tool-specific support**:
   - CodeRabbit: https://docs.coderabbit.ai
   - Lynx: Check Lynx documentation

### Providing Feedback

If automated reviews are:
- **Too strict**: We can adjust thresholds in config files
- **Missing issues**: Report patterns we should add
- **Creating noise**: We can refine rules and exclusions

Create an issue with:
- PR link where problem occurred
- Specific comment or check that needs adjustment
- Your suggested improvement

## Best Practices

### For Developers

1. **Run checks locally first**: `npm run lint`, `npm test`, `mvn verify`
2. **Review your own code**: Self-review before creating PR
3. **Write descriptive PR descriptions**: Help reviewers understand context
4. **Keep PRs focused**: Smaller PRs get better reviews
5. **Respond to feedback**: Even if you disagree, explain why

### For Reviewers

1. **Don't rely solely on automation**: Human review still essential
2. **Verify critical security issues**: Automated tools can miss context
3. **Provide context in comments**: Help developers understand "why"
4. **Balance automation and pragmatism**: Not every suggestion must be followed
5. **Update configurations**: Improve rules based on learnings

## Conclusion

Automated code review helps maintain code quality and security but doesn't replace human judgment. Use these tools as assistants to catch common issues and learn best practices, while reviewers focus on architecture, design, and business logic.

The goal is to make code review faster, more consistent, and more educational for everyone involved.

---

**Last Updated**: January 2026  
**Maintained By**: Repository Maintainers  
**Questions**: Open an issue or discussion
