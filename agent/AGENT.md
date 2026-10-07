# Agent Operating Rules

## 1. Purpose

This document defines how AI Agents must operate when working on the ClimbLog repository.

It defines:

- How Agents inspect the repository
- How Agents analyze tasks
- When approval is required
- How Agents modify the repository
- How Agents test and verify changes
- How Agents document work
- How Agents preserve architectural boundaries
- How Agents hand work over to another Agent

This document defines **Agent behavior and working rules**.

It does not describe the current implementation of the system.

Current architecture is documented under:

```text
agent/docs/architecture/
```

Current and planned project work is documented under:

```text
agent/docs/roadmap.md
```

Task-specific work state is documented under:

```text
agent/artifacts/
```

---

# 2. Documentation Structure

The `agent/` directory separates stable Agent rules from project knowledge and task state.

```text
agent/
├── AGENT.md
├── docs/
│   ├── architecture/
│   │   ├── overview.md
│   │   ├── modules.md
│   │   ├── video-pipeline.md
│   │   ├── authentication.md
│   │   ├── database.md
│   │   └── infrastructure.md
│   │
│   └── roadmap.md
│
└── artifacts/
    └── <task-name>/
```

### `AGENT.md`

Defines stable rules for how an Agent works.

It should not contain detailed implementation state.

### `docs/architecture/`

Describes the current intended system architecture.

Architecture documents provide context but do not override the repository implementation.

### `docs/roadmap.md`

Describes planned work, priorities, milestones, and deferred improvements.

The roadmap is not an implementation specification.

### `artifacts/`

Contains task-specific analysis, decisions, plans, verification results, and completion information.

Artifacts preserve work state across Agent sessions.

---

# 3. Source of Truth

The repository implementation is the final source of truth.

The documentation hierarchy provides context but must not be treated as more authoritative than the actual implementation.

When information conflicts, use the following order:

1. Current repository implementation
2. Current configuration and build files
3. Current tests and verification results
4. Architecture documentation
5. Roadmap
6. Historical task artifacts

Historical artifacts must never be used as proof that the current implementation still behaves the same way.

---

# 4. Documentation Reading Rules

Agents must read the documentation relevant to the requested task before making non-trivial changes.

Do not blindly read every document for every task.

Determine which documents are relevant based on the scope.

Examples:

| Task | Relevant Documentation |
|---|---|
| Module refactoring | `modules.md` |
| Video processing | `video-pipeline.md` |
| Authentication | `authentication.md` |
| Database / Redis | `database.md` |
| Deployment / infrastructure | `infrastructure.md` |
| Cross-cutting architecture | `overview.md` + relevant architecture documents |
| Roadmap planning | `roadmap.md` + relevant architecture documents |
| Existing task continuation | Relevant `artifacts/<task-name>/` |

After reading the relevant documentation, inspect the repository implementation before drawing conclusions.

---

# 5. Mandatory Agent Workflow

For non-trivial development tasks, follow this workflow:

```text
Inspect
   ↓
Analyze
   ↓
Report
   ↓
Approval
   ↓
Modify
   ↓
Test
   ↓
Verify
   ↓
Final Report
```

The phases must not be skipped simply because the requested change appears small.

If a task is clearly limited to a trivial, isolated change with no architectural or behavioral uncertainty, the Agent may use a shorter workflow when appropriate.

---

# 6. Step 1 — Inspect

Before modifying anything, inspect the relevant repository implementation.

Inspection may include:

- Source code
- Module structure
- Gradle configuration
- Build logic
- Configuration files
- Existing tests
- Related documentation
- Existing task artifacts
- Deployment configuration
- Database schema definitions
- API contracts

The goal is to understand the actual current state.

Do not modify files during the inspection phase.

---

# 7. Step 2 — Analyze

After inspection, determine:

- What the current implementation does
- What the requested behavior requires
- What is already implemented
- What is missing
- Which files are actually affected
- Whether the requested change fits the current architecture
- Whether dependencies or boundaries are affected
- What tests are required
- What risks or compatibility concerns exist

Avoid proposing changes simply because an alternative implementation appears cleaner.

The goal is to make the **minimum change required to satisfy the approved task**.

---

# 8. Step 3 — Pre-Change Report

For non-trivial work, provide a pre-change report before modifying the repository.

The report should contain:

### Current State

What currently exists.

### Requested Change

What the task requires.

### Gap

What is missing or incorrect.

### Proposed Change

The minimum implementation required.

### Affected Files

List files expected to be modified, created, deleted, or renamed.

### Dependencies

Identify dependency or module changes if applicable.

### Architectural Impact

State whether the existing architecture remains intact.

### Testing Plan

Describe how the change will be verified.

### Risks

Identify relevant compatibility, migration, deployment, or regression risks.

---

# 9. Approval Boundary

Do not modify the repository before approval when the proposed work is non-trivial.

Approval is required before:

- Creating files
- Deleting files
- Renaming files
- Moving files
- Modifying architecture
- Changing module boundaries
- Adding or removing dependencies
- Changing public APIs
- Changing database schema
- Changing persistence strategy
- Changing infrastructure architecture
- Changing authentication behavior
- Changing deployment configuration
- Performing broad refactoring

The Agent may perform read-only inspection and analysis before approval.

---

# 10. Architectural Change Escalation

If analysis reveals that the requested task cannot be completed safely without an architectural change, stop before implementation.

Report:

1. Problem
2. Evidence
3. Root Cause
4. Impact
5. Minimum Required Change
6. Alternatives
7. Recommendation
8. Affected Files
9. Migration / Compatibility Concerns
10. Verification Plan

Then wait for approval.

Do not silently expand the task scope.

---

# 11. Step 4 — Modify

After approval, implement only the approved changes.

The implementation should:

- Follow existing project conventions
- Preserve established architecture
- Avoid unrelated refactoring
- Minimize the number of changed files
- Preserve backward compatibility where required
- Reuse existing abstractions when appropriate
- Avoid introducing abstractions without a demonstrated need

If implementation reveals a requirement that was not covered by the approval, stop and report it rather than silently expanding the scope.

---

# 12. Minimal Change Principle

Prefer the smallest change that correctly solves the problem.

Do not:

- Rewrite working code without reason
- Rename unrelated symbols
- Reorganize unrelated packages
- Replace established libraries without requirement
- Introduce a new abstraction merely for theoretical flexibility
- Perform opportunistic cleanup
- Modify unrelated modules

A cleaner implementation is not automatically a better implementation if it expands the task unnecessarily.

---

# 13. No Unrelated Refactoring

Do not combine unrelated refactoring with the requested task.

For example, a feature implementation should not automatically include:

- Package restructuring
- Naming-system changes
- Dependency migration
- Architecture redesign
- Library replacement
- Broad formatting changes

If such work is beneficial but outside the requested scope, report it separately as a recommendation.

---

# 14. Dependency Changes

Before adding a dependency:

1. Search the repository for an existing solution.
2. Check whether an existing dependency already provides the capability.
3. Confirm the dependency belongs to the correct module.
4. Determine whether it should use `api` or `implementation`.
5. Check whether the dependency introduces architectural coupling.
6. Consider whether it is actually required.

Do not add dependencies speculatively.

When removing dependencies, verify that:

- No source code requires them.
- No build logic requires them.
- No transitive assumption is being relied upon unintentionally.
- The affected module still builds correctly.

---

# 15. Module Boundaries

Module boundaries must be preserved unless the task explicitly requires changing them.

General principles:

- Presentation depends on Domain.
- Domain remains independent from infrastructure.
- Data owns data-source coordination.
- Local owns local persistence concerns.
- Remote owns remote data-source concerns.
- Platform-specific capabilities remain isolated.
- Server implementation must not leak into client modules.
- Worker implementation must remain independently deployable.

The exact dependency graph is documented in:

`agent/docs/architecture/modules.md`

Always verify the actual Gradle configuration before making dependency decisions.

---

# 16. API and Implementation Dependencies

Use `api` only when a dependency's public types must be exposed to consumers.

Prefer `implementation` when a dependency is an internal implementation detail.

When reviewing dependencies:

- Identify unnecessary API exposure.
- Reduce accidental transitive dependencies.
- Remove unused dependencies.
- Keep module interfaces intentionally small.

Dependency cleanup must not break consumers.

---

# 17. Domain and Infrastructure Boundaries

Business logic should remain independent from infrastructure implementation details.

Avoid leaking:

- Database-specific models
- Exposed table objects
- HTTP client implementation types
- Redis client types
- Platform APIs
- Framework-specific infrastructure

into layers that should not depend on them.

When an abstraction is introduced, it must have a concrete architectural purpose.

Do not create interfaces solely because interfaces appear architecturally cleaner.

---

# 18. Model Separation

When multiple architectural representations of the same concept exist, preserve the project's established naming and layer boundaries.

For example:

```text
Domain
Video

Data
VideoData

Local
VideoEntity

Remote
VideoResponse
```

Related models should be organized consistently with the existing project structure.

Do not merge models across layers simply to reduce the number of classes if doing so introduces unwanted coupling.

---

# 19. Database Changes

Database changes require additional caution.

Before modifying persistence:

- Inspect the current table definitions.
- Inspect initialization and migration logic.
- Identify existing constraints and indexes.
- Determine the database environment.
- Consider existing data.
- Determine transaction requirements.
- Determine compatibility with production.

Schema changes must be explicit.

Destructive changes require explicit justification and approval.

---

# 20. Infrastructure Changes

Infrastructure changes must be treated as potentially high-impact changes.

Examples:

- Cloud resources
- Object storage
- CDN configuration
- Worker deployment
- Server deployment
- Environment variables
- Secrets
- Network configuration
- Service management
- CI/CD
- Storage lifecycle policies

Inspect both repository configuration and deployment configuration before modifying infrastructure.

Never expose credentials in source code or logs.

---

# 21. Security

Agents must preserve security boundaries.

Never commit or expose:

- Passwords
- API keys
- Access keys
- Secret keys
- JWT signing secrets
- Refresh tokens
- Worker authentication tokens
- OAuth client secrets
- Production credentials

Do not place secrets in:

- Source code
- Documentation
- Test fixtures
- Logs
- Commit messages
- Agent artifacts

Use placeholders when documenting configuration examples.

---

# 22. Logging

Logs should be useful for diagnosis without exposing sensitive information.

Prefer logging:

- Operation type
- Resource identifiers when safe
- Job identifiers
- State transitions
- Timing information
- Error categories
- Retry information

Avoid logging:

- Credentials
- Tokens
- Personal secrets
- Full request bodies when they may contain sensitive information

---

# 23. Testing

After implementation, run the most relevant verification available.

Testing may include:

- Unit tests
- Integration tests
- Build verification
- Static analysis
- Module compilation
- API tests
- Database tests
- End-to-end tests
- Infrastructure smoke tests

Do not claim a test passed unless it was actually executed.

If the environment prevents a test from running, report:

- What could not be executed
- Why it could not be executed
- What alternative verification was performed
- What remains unverified

---

# 24. Step 5 — Test

Testing should focus on the behavior changed by the task.

At minimum, verify:

1. The modified code compiles.
2. Relevant tests pass.
3. Existing behavior remains intact where applicable.
4. Architectural boundaries remain valid.
5. Configuration remains valid.

For larger changes, expand verification proportionally.

---

# 25. Step 6 — Verify

Testing and verification are separate concerns.

Testing asks:

> Does the implementation behave as expected?

Verification asks:

> Did the implementation actually satisfy the requested task without unintended changes?

Verification should check:

- Requested behavior
- Affected files
- Dependency changes
- Architecture boundaries
- Build configuration
- Tests
- Documentation updates
- Unintended modifications

---

# 26. Documentation Updates

Update architecture documentation only when an approved change changes the documented architecture.

Do not update architecture documents for every implementation detail.

For example:

- Adding an internal helper does not normally require architecture documentation.
- Changing module dependency direction does.
- Changing the video processing architecture does.
- Changing database technology does.
- Changing infrastructure topology does.

Documentation should describe stable architectural decisions rather than temporary implementation details.

---

# 27. Roadmap Rules

The roadmap describes planned work, not current implementation truth.

Agents must not mark roadmap items complete solely because a related code change was attempted.

A roadmap item should be considered complete only when its defined acceptance criteria have been verified.

If implementation reveals that roadmap assumptions are outdated:

1. Report the discrepancy.
2. Verify the current repository state.
3. Update the roadmap only when appropriate and approved.

---

# 28. Task Artifacts

Task-specific artifacts belong under:

```text
agent/artifacts/<task-name>/
```

Artifacts exist to preserve work state across Agent sessions.

They should allow another Agent to continue the task without reconstructing the entire previous conversation.

---

# 29. Artifact Files

A task may use the following files:

```text
agent/artifacts/<task-name>/
├── README.md
├── analysis.md
├── plan.md
├── workthrough.md
├── verification.md
└── completion.md
```

Not every task requires every file.

Use only the files that provide meaningful persistent state.

### `README.md`

Defines:

- Task purpose
- Scope
- Current status
- Important references
- How to resume the task

### `analysis.md`

Contains:

- Repository findings
- Current-state analysis
- Constraints
- Risks
- Architectural considerations

### `plan.md`

Contains:

- Approved implementation plan
- Ordered work items
- Expected affected files
- Verification strategy

### `workthrough.md`

Contains meaningful implementation progress and decisions.

It should not become a raw terminal log.

### `verification.md`

Contains:

- Tests performed
- Verification results
- Known limitations
- Remaining concerns

### `completion.md`

Contains:

- Final implementation summary
- Changed files
- Verification results
- Documentation updates
- Remaining follow-up work

---

# 30. Artifact Status

Task artifacts should make the current state obvious.

A task should clearly identify whether it is:

- `ANALYSIS`
- `WAITING_FOR_APPROVAL`
- `IMPLEMENTATION`
- `VERIFICATION`
- `COMPLETED`
- `BLOCKED`

Do not describe incomplete work as completed.

---

# 31. Artifact Update Rules

Update artifacts when meaningful state changes occur.

Examples:

- Analysis completed
- Approval received
- Implementation begins
- Important implementation decision made
- Verification completed
- Blocker discovered
- Task completed

Do not record every command or minor edit.

The goal is to preserve **decision state and work state**, not terminal history.

---

# 32. Agent Handoff

A task should be resumable by another Agent.

Before ending an incomplete task, record:

- What was inspected
- What was determined
- What was approved
- What was changed
- What remains
- What failed
- What must happen next
- Relevant files
- Verification status

The next Agent should be able to continue from the artifacts without relying on the previous conversation.

---

# 33. Resuming Existing Work

When a task already has artifacts:

1. Read `README.md`.
2. Read the latest relevant analysis or plan.
3. Check the recorded status.
4. Inspect the current repository state.
5. Compare the repository against the artifact assumptions.
6. Continue only after confirming that the recorded state is still valid.

Never assume that an artifact's recorded implementation state is still current.

---

# 34. Architecture Preservation

The Agent must preserve established architectural boundaries unless an approved task explicitly changes them.

Before introducing an architectural change, determine whether the same requirement can be satisfied within the existing architecture.

Prefer:

```text
Existing Architecture
        +
Minimum Required Change
```

over:

```text
Existing Architecture
        ↓
Unnecessary Redesign
```

---

# 35. Code Quality

Code should be:

- Readable
- Explicit
- Consistent with existing conventions
- Testable
- Appropriately abstracted
- Easy to maintain

Avoid both extremes:

- Excessively clever implementations
- Excessive abstraction for simple behavior

The best implementation is the simplest one that correctly satisfies the requirement within the existing architecture.

---

# 36. Naming

Follow existing repository naming conventions.

Before introducing a new name:

1. Search for similar concepts.
2. Identify existing terminology.
3. Reuse established terminology where appropriate.
4. Avoid introducing synonyms for the same concept.

Consistency is more important than personal naming preference.

---

# 37. Communication Rules

Agent reports should be:

- Precise
- Evidence-based
- Structured
- Explicit about uncertainty

Distinguish clearly between:

- Observed facts
- Inferences
- Recommendations
- Approved decisions
- Unverified assumptions

Do not present assumptions as repository facts.

---

# 38. Handling Uncertainty

When information is incomplete:

1. Inspect the repository further if possible.
2. Identify the exact uncertainty.
3. State what is known.
4. State what is unknown.
5. Explain why the uncertainty matters.
6. Ask for approval or clarification when necessary.

Do not silently fill architectural gaps with assumptions.

---

# 39. Completion Criteria

A task is complete only when:

- The approved scope has been implemented.
- Relevant tests have been executed.
- Verification has been completed.
- No known blocking issue remains within scope.
- Required architecture documentation has been updated.
- Relevant task artifacts have been updated.
- The final report accurately describes the result.

If any of these cannot be completed, report the task as incomplete or blocked.

---

# 40. Final Report

Every completed non-trivial task should provide a final report containing:

### Summary

What was implemented.

### Changed Files

What files were created, modified, deleted, or renamed.

### Key Decisions

Important implementation or architectural decisions.

### Verification

Tests and checks that were actually executed.

### Results

Whether verification passed, failed, or was partially completed.

### Documentation

Which architecture or task documents were updated.

### Remaining Work

Any known follow-up work or limitations.

Do not claim successful verification for checks that were not executed.

---

# 41. Core Principles

The following principles apply to all Agent work:

1. **Inspect before changing.**
2. **Repository implementation is the final source of truth.**
3. **Analyze before proposing implementation.**
4. **Obtain approval before non-trivial changes.**
5. **Make the minimum change necessary.**
6. **Do not expand scope silently.**
7. **Preserve architectural boundaries.**
8. **Avoid unnecessary abstractions and refactoring.**
9. **Keep secrets out of source code, logs, and documentation.**
10. **Test what was changed.**
11. **Verify the result independently of implementation activity.**
12. **Document meaningful architectural changes.**
13. **Keep task artifacts resumable.**
14. **Never claim work or verification that did not actually occur.**
15. **When uncertain, inspect rather than guess.**