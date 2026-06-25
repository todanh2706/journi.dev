#!/usr/bin/env bash
set -u

challenge="${1:-}"
workspace="${WORKSPACE_ROOT:-/workspace}"
grader_root="${GRADER_ROOT:-/grader}"
result_dir="$workspace/.journi"
result_file="$result_dir/result.json"

rm -rf -- "$result_dir"
mkdir -p -- "$result_dir"

write_pass() {
  local summary="$1"
  cat > "$result_file" <<JSON
{"score":100,"summary":"$summary","criteria":[{"criterion":"Required artifacts","passed":true,"message":"All required artifacts were found."},{"criterion":"Contract behavior","passed":true,"message":"The server-owned deterministic checks passed."},{"criterion":"Safety constraints","passed":true,"message":"No forbidden contract change was detected."},{"criterion":"Reproducibility","passed":true,"message":"The checks completed from the submitted revision."}]}
JSON
}

write_failure() {
  local summary="$1"
  cat > "$result_file" <<JSON
{"score":0,"summary":"$summary","criteria":[{"criterion":"Required artifacts","passed":false,"message":"One or more required files or behaviors did not satisfy the challenge contract."},{"criterion":"Contract behavior","passed":false,"message":"Review the grader output and the learner-facing acceptance criteria."}]}
JSON
}

require_file() {
  [[ -f "$workspace/$1" ]]
}

require_text() {
  local file="$1"
  local pattern="$2"
  grep -Eq -- "$pattern" "$workspace/$file"
}

run_collections() {
  local source_root="$workspace/src/practice/collections-and-generics"
  local classes="$result_dir/classes"
  if [[ ! -f "$source_root/Book.java" || ! -f "$source_root/LibraryCatalog.java" ]]; then
    write_failure "Required Collections and Generics source files are missing."
    return 1
  fi
  mkdir -p -- "$classes"
  if javac -d "$classes" "$source_root/Book.java" "$source_root/LibraryCatalog.java" "$grader_root/collections-and-generics/TestMain.java" \
      && java -cp "$classes" dev.journi.practice.collections.TestMain; then
    write_pass "Collections and Generics behavior passed all deterministic checks."
    return 0
  fi
  write_failure "Collections and Generics behavior needs changes."
  return 1
}

run_contract_checks() {
  case "$challenge" in
    jdbc-basics)
      require_file "pom.xml" && require_file "src/main/java/dev/journi/practice/jdbc/JdbcBookRepository.java" \
        && require_text "src/main/java/dev/journi/practice/jdbc/JdbcBookRepository.java" "PreparedStatement" \
        && require_text "src/main/java/dev/journi/practice/jdbc/JdbcBookRepository.java" "try[[:space:]]*\\(" ;;
    rest-api-development)
      require_file "pom.xml" && require_file "src/main/java/dev/journi/catalog/controllers/BookController.java" \
        && require_text "src/main/java/dev/journi/catalog/controllers/BookController.java" "@RestController" \
        && require_text "src/main/java/dev/journi/catalog/controllers/BookController.java" "(@Valid|Pageable)" ;;
    spring-data-jpa)
      require_file "src/main/java/dev/journi/catalog/entities/Book.java" \
        && require_file "src/main/java/dev/journi/catalog/repositories/BookRepository.java" \
        && require_text "src/main/java/dev/journi/catalog/entities/Book.java" "@Entity" \
        && require_text "src/main/java/dev/journi/catalog/repositories/BookRepository.java" "JpaRepository" ;;
    spring-security-and-jwt)
      require_file "src/main/java/dev/journi/catalog/configs/SecurityConfig.java" \
        && require_file "src/main/java/dev/journi/catalog/services/AuthenticationService.java" \
        && require_text "src/main/java/dev/journi/catalog/configs/SecurityConfig.java" "SecurityFilterChain" \
        && require_text "src/main/java/dev/journi/catalog/services/AuthenticationService.java" "PasswordEncoder" ;;
    testing-basics)
      require_file "src/test/java/dev/journi/catalog/repositories/BookRepositoryTest.java" \
        && require_file "src/test/java/dev/journi/catalog/services/BookServiceTest.java" \
        && require_file "src/test/java/dev/journi/catalog/controllers/BookControllerTest.java" \
        && require_text "src/test/java/dev/journi/catalog/controllers/BookControllerTest.java" "@Test" ;;
    docker-basics)
      require_file "Dockerfile" && require_file "compose.yaml" && require_file ".dockerignore" \
        && require_text "Dockerfile" "^USER[[:space:]]+[^[:space:]]+" \
        && ! require_text "Dockerfile" "^USER[[:space:]]+root" ;;
    deployment-basics)
      require_file ".github/workflows/ci.yml" && require_file ".env.example" \
        && require_file "docs/release-checklist.md" \
        && require_text ".github/workflows/ci.yml" "(mvn|mvnw).*(test|verify)" \
        && require_text "docs/release-checklist.md" "(rollback|Rollback)" ;;
    *) return 1 ;;
  esac
}

if [[ "$challenge" == "collections-and-generics" ]]; then
  run_collections
  exit 0
fi

if run_contract_checks; then
  write_pass "The submitted revision satisfied the deterministic artifact contract."
else
  write_failure "The submitted revision does not yet satisfy the deterministic artifact contract."
fi
exit 0
