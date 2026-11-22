# GravitLauncher Git convention

Simple, transparent and effective Git workflow rules. GravitLauncher follows [Git Flow](https://leanpub.com/git-flow/read).

## Branches

| Branch | Purpose | Branch from | Example |
| --- | --- | --- | --- |
| master | Production-ready code | release | |
| develop | New feature development | master | |
| release | Testing all new functionality | develop | |
| bugfix-* | Fix bugs in new functionality | release | bugfix-auth |
| feature-* | Add a feature | develop | feature-auth |
| hotfix-* | Urgent production fix | master | hotfix-auth |

![Git Flow](https://i.ytimg.com/vi/w2r0oLFtXAw/maxresdefault.jpg)

## Commits

1. Write commits in English.
2. Do not use past tense.
3. Include a prefix.
4. Do not end with unnecessary punctuation.
5. Keep every part within100 characters.

Format: `[PREFIX] <Message>`.

| Prefix | Purpose | Example |
| --- | --- | --- |
| [FIX] | Bug fixes | [FIX] Bug with failed authorization |
| [DOCS] | Documentation | [DOCS] Documenting Authorization API |
| [FEATURE] | New features | [FEATURE] 2FA on authorization |
| [STYLE] | Typos and formatting | [STYLE] Typos in the authorization module |
| [REFACTOR] | Refactoring | [REFACTOR] Switching to EDA in the authorization module |
| [TEST] | Tests | [TEST] Coverage of the authorization module with tests |
| [ANY] | Other changes | [ANY] Connecting Travis CI |
