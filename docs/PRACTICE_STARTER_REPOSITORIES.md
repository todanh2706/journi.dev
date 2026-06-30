# Practice Starter Repositories

This catalog is the source of truth for public starter repositories linked from seeded
Journi.dev assessment challenges.

## Curation Policy

- Owner: `todanh2706`
- Naming convention: `journi-practice-<node-slug>`
- Default branch: `main`
- Visibility: public
- Scope: one starter repository per seeded `PRACTICE` or `PROJECT` node
- Seed rule: no assessment challenge may point to the main `journi.dev` source repository

## Current Mapping

| Node slug | Type | Starter repository | Branch | Readiness notes |
| --- | --- | --- | --- | --- |
| `collections-and-generics` | `PRACTICE` | `https://github.com/todanh2706/journi-practice-collections-and-generics` | `main` | Ready for learner use; current grading pilot. |
| `jdbc-basics` | `PRACTICE` | `https://github.com/todanh2706/journi-practice-jdbc-basics` | `main` | Ready as a learner-visible brief; automated evaluation remains disabled. |
| `rest-api-development` | `PROJECT` | `https://github.com/todanh2706/journi-practice-rest-api-development` | `main` | Ready as a learner-visible brief; automated evaluation remains disabled. |
| `spring-data-jpa` | `PRACTICE` | `https://github.com/todanh2706/journi-practice-spring-data-jpa` | `main` | Ready as a learner-visible brief; automated evaluation remains disabled. |
| `spring-security-and-jwt` | `PRACTICE` | `https://github.com/todanh2706/journi-practice-spring-security-and-jwt` | `main` | Ready as a learner-visible brief; automated evaluation remains disabled. |
| `testing-basics` | `PRACTICE` | `https://github.com/todanh2706/journi-practice-testing-basics` | `main` | Ready as a learner-visible brief; automated evaluation remains disabled. |
| `docker-basics` | `PRACTICE` | `https://github.com/todanh2706/journi-practice-docker-basics` | `main` | Ready as a learner-visible brief; automated evaluation remains disabled. |
| `deployment-basics` | `PROJECT` | `https://github.com/todanh2706/journi-practice-deployment-basics` | `main` | Ready as a learner-visible brief; automated evaluation remains disabled. |

## Maintenance Workflow

When adding or changing a seeded assessment challenge:

1. Create or update the matching public repository under the curation policy above.
2. Keep the starter files aligned with the challenge `expectedArtifacts`.
3. Update this catalog and the seed dataset in `src/backend/src/main/resources/seed-data/`.
4. Rerun the local seeder so existing development databases refresh the roadmap node metadata and challenge responses that power **Open starter repository** links.
5. Run the backend seed tests so malformed, duplicate, or placeholder starter repository URLs are caught locally.
6. If a challenge brief changes, review the external repository README and starter files in the same change.

Useful live check:

```bash
gh repo view todanh2706/journi-practice-collections-and-generics --json visibility,defaultBranchRef,url
```
