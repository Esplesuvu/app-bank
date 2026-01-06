# Build FAQ and Maven 403 Resolution Guide

This project builds with Maven. If you see HTTP 403 errors when resolving dependencies (for example the Spring Boot parent), follow the steps below to unblock the build.

## 1) Verify basic connectivity
- Ensure you can reach `https://repo.maven.apache.org/maven2` in your browser or with `curl -I https://repo.maven.apache.org/maven2`.
- If outbound HTTPS is blocked, you must use an enterprise proxy or an internal artifact mirror (Nexus/Artifactory) that has Maven Central cached.

## 2) Apply the provided Maven settings template
- Copy `settings-example.xml` to your local Maven home and customize the placeholders:
  ```bash
  ./scripts/apply-settings-example.sh
  # then edit ~/.m2/settings.xml to set proxy host/port, credentials or mirror URL
  ```
- The template contains sections for:
  - Proxy configuration (HTTP/HTTPS)
  - Mirrors (e.g., Nexus/Artifactory or a vendor-provided mirror of Maven Central)
  - Repository authentication (if your mirror requires credentials)

## 3) Point Maven to the correct repository
- If your organization exposes a mirror, put its URL in the `<mirrorOf>*</mirrorOf>` entry inside `~/.m2/settings.xml`.
- If you only need a proxy, leave `<mirrorOf>` as default and fill in the `<proxy>` section.
- You can keep a local cache inside the repo with:
  ```bash
  mvn -Dmaven.repo.local=.m2/repository -DskipTests verify
  ```
  This avoids polluting the global cache and makes CI runs reproducible.

## 4) Debug with verbose output
- Run Maven with `-X` to see the exact repository URL that returns 403:
  ```bash
  mvn -X -DskipTests package
  ```
- Confirm the failing host is reachable via the configured proxy or mirror.

## 5) Offline rebuild after a successful download
- Once dependencies are cached (either globally or in `.m2/repository`), you can rebuild offline:
  ```bash
  mvn -o -Dmaven.repo.local=.m2/repository -DskipTests package
  ```

## 6) When nothing else works
- Ask your network/DevOps team whether Maven Central is blocked and request access or an internal mirror URL.
- If a mirror is provided, add it to `settings.xml` as described above and retry `mvn -X -DskipTests package`.

By following these steps you should be able to resolve the 403 errors and complete the build locally or in CI.
