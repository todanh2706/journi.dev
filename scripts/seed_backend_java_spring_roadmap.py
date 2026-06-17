#!/usr/bin/env python3

import argparse
import os
import pathlib
import socket
import subprocess
import sys


DEFAULT_DATASET = "classpath:seed-data/backend-java-spring-roadmap.json"
DEFAULT_POSTGRES_PORT = 5432
DOCKER_ONLY_HOST_ALIASES = {
    "backend",
    "cache",
    "database",
    "journi",
    "journi_backend",
    "journi_cache_db",
    "journi_frontend",
    "journi_prim_db",
}


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Run the backend roadmap seeder for the Backend Java Spring Boot Developer dataset."
    )
    parser.add_argument(
        "--dataset",
        default=DEFAULT_DATASET,
        help="Spring resource location for the roadmap dataset.",
    )
    return parser


def parse_env_file(path: pathlib.Path) -> dict[str, str]:
    values: dict[str, str] = {}

    if not path.exists():
        return values

    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue

        key, value = line.split("=", 1)
        values[key.strip()] = value.strip().strip('"').strip("'")

    return values


def normalize_host_for_local_run(host: str) -> str:
    if host in DOCKER_ONLY_HOST_ALIASES:
        return "localhost"

    return host


def normalize_postgres_url(url: str) -> str:
    prefix = "jdbc:postgresql://"
    if not url.startswith(prefix):
        return url

    remainder = url[len(prefix) :]
    host_port, slash, database = remainder.partition("/")
    host, colon, port = host_port.partition(":")

    normalized_host = normalize_host_for_local_run(host)
    normalized_port = port or str(DEFAULT_POSTGRES_PORT)

    rebuilt = f"{prefix}{normalized_host}:{normalized_port}"
    if slash:
        rebuilt = f"{rebuilt}/{database}"

    return rebuilt


def parse_postgres_host_port(url: str) -> tuple[str, int] | None:
    prefix = "jdbc:postgresql://"
    if not url.startswith(prefix):
        return None

    remainder = url[len(prefix) :]
    host_port = remainder.split("/", 1)[0]
    host, colon, port = host_port.partition(":")
    if not host:
        return None

    return host, int(port or DEFAULT_POSTGRES_PORT)


def prepare_environment(repo_root: pathlib.Path) -> dict[str, str]:
    root_env = parse_env_file(repo_root / "src" / ".env")
    backend_env = parse_env_file(repo_root / "src" / "backend" / ".env")
    env = os.environ.copy()

    merged_file_env: dict[str, str] = {}
    merged_file_env.update(root_env)
    merged_file_env.update(backend_env)

    if "JWT_SECRET_KEY" not in env and merged_file_env.get("JWT_SECRET_KEY"):
        env["JWT_SECRET_KEY"] = merged_file_env["JWT_SECRET_KEY"]

    if "SPRING_DATASOURCE_URL" not in env:
        candidate_url = (
            env.get("SPRING_DB_URL")
            or merged_file_env.get("SPRING_DATASOURCE_URL")
            or merged_file_env.get("SPRING_DB_URL")
        )
        if candidate_url:
            env["SPRING_DATASOURCE_URL"] = normalize_postgres_url(candidate_url)

    if "SPRING_DATASOURCE_USERNAME" not in env:
        candidate_username = (
            env.get("DB_USER")
            or merged_file_env.get("SPRING_DATASOURCE_USERNAME")
            or merged_file_env.get("DB_USER")
        )
        if candidate_username:
            env["SPRING_DATASOURCE_USERNAME"] = candidate_username

    if "SPRING_DATASOURCE_PASSWORD" not in env:
        candidate_password = (
            env.get("DB_PASSWORD")
            or merged_file_env.get("SPRING_DATASOURCE_PASSWORD")
            or merged_file_env.get("DB_PASSWORD")
        )
        if candidate_password:
            env["SPRING_DATASOURCE_PASSWORD"] = candidate_password

    if "SPRING_DATA_REDIS_HOST" not in env:
        candidate_redis_host = (
            env.get("REDIS_HOST")
            or merged_file_env.get("SPRING_DATA_REDIS_HOST")
            or merged_file_env.get("REDIS_HOST")
            or "localhost"
        )
        env["SPRING_DATA_REDIS_HOST"] = normalize_host_for_local_run(candidate_redis_host)

    if "SPRING_DATA_REDIS_PORT" not in env:
        candidate_redis_port = (
            env.get("REDIS_PORT")
            or merged_file_env.get("SPRING_DATA_REDIS_PORT")
            or merged_file_env.get("REDIS_PORT")
            or "6379"
        )
        env["SPRING_DATA_REDIS_PORT"] = candidate_redis_port

    env["SPRING_DEVTOOLS_RESTART_ENABLED"] = "false"
    return env


def ensure_database_is_reachable(env: dict[str, str]) -> bool:
    datasource_url = env.get("SPRING_DATASOURCE_URL")
    if not datasource_url:
        print(
            "Missing SPRING_DATASOURCE_URL. Set it explicitly or provide DB settings in src/.env.",
            file=sys.stderr,
        )
        return False

    host_port = parse_postgres_host_port(datasource_url)
    if host_port is None:
        return True

    host, port = host_port
    try:
        with socket.create_connection((host, port), timeout=3):
            return True
    except OSError as exc:
        print(
            f"Could not reach PostgreSQL at {host}:{port} ({exc}).",
            file=sys.stderr,
        )
        print(
            "Start the local database first, for example: `cd src && docker compose up -d database cache`.",
            file=sys.stderr,
        )
        print(
            "You can also override the connection by exporting SPRING_DATASOURCE_URL, "
            "SPRING_DATASOURCE_USERNAME, and SPRING_DATASOURCE_PASSWORD before rerunning the script.",
            file=sys.stderr,
        )
        return False


def find_packaged_jar(backend_dir: pathlib.Path) -> pathlib.Path | None:
    target_dir = backend_dir / "target"
    if not target_dir.exists():
        return None

    jar_candidates = sorted(
        path
        for path in target_dir.glob("*.jar")
        if not path.name.startswith("original-")
    )

    if not jar_candidates:
        return None

    return max(jar_candidates, key=lambda path: path.stat().st_mtime)


def main() -> int:
    parser = build_parser()
    args = parser.parse_args()

    repo_root = pathlib.Path(__file__).resolve().parents[1]
    backend_dir = repo_root / "src" / "backend"
    mvnw_path = backend_dir / "mvnw"

    if not mvnw_path.exists():
        print("Could not find src/backend/mvnw", file=sys.stderr)
        return 1

    env = prepare_environment(repo_root)
    env["JOURNI_SEED_ROADMAPS_ENABLED"] = "true"
    env["JOURNI_SEED_ROADMAPS_EXIT_AFTER_RUN"] = "true"
    env["JOURNI_SEED_ROADMAPS_DATASET_LOCATION"] = args.dataset

    if not ensure_database_is_reachable(env):
        return 1

    build_command = ["./mvnw", "-DskipTests", "package"]
    print("Building backend jar for roadmap seeding...", flush=True)
    build_result = subprocess.run(build_command, cwd=backend_dir, env=env, check=False)
    if build_result.returncode != 0:
        return build_result.returncode

    jar_path = find_packaged_jar(backend_dir)
    if jar_path is None:
        print("Could not find packaged backend jar under src/backend/target", file=sys.stderr)
        return 1

    run_command = ["java", "-jar", str(jar_path)]
    print(f"Running roadmap seeder with dataset: {args.dataset}", flush=True)
    print(f"Using datasource: {env.get('SPRING_DATASOURCE_URL', '<not set>')}", flush=True)
    completed = subprocess.run(run_command, cwd=backend_dir, env=env, check=False)
    return completed.returncode


if __name__ == "__main__":
    raise SystemExit(main())
