# Contributing to SEMCode

Thanks for helping with the HR system.

## Workflow

1. Pick an issue and move its card to **In Progress** on Zube.
2. Pull the latest `develop` branch.
3. Create a feature branch from `develop`, for example `feature/my-feature`.
4. Make your changes and add unit or integration tests for them.
5. Check `mvn package` passes and GitHub Actions is green on your branch.
6. Merge the feature branch into `develop`.

## Reporting bugs

Open a new issue and choose the **Bug Report** template. Please include the version, where you saw the problem, steps to reproduce it and any log output.

## Bug labels

- `triage`: new bug that has not been looked at yet.
- `confirmed`: the bug has been reproduced.
- `in progress`: someone is fixing it.
- `fixed`: the fix is merged and waiting for release.
- `wontfix` and `duplicate`: closed without a fix.