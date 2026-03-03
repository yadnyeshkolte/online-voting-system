# Online Voting System: End-to-End Deployment Report

## 1. Executive Summary

This report documents the complete journey from an insecure, IP-based deployment:

- `http://140.245.10.206`

to a production-ready HTTPS domain deployment:

- `https://onlinevotingsystem.duckdns.org`

with stable authentication, profile image handling, and face verification for voting.

The work covered:

1. Public HTTPS setup with Nginx reverse proxy and Lets Encrypt.
2. Browser secure-context fixes for webcam support.
3. Backend/frontend reliability fixes for login/registration.
4. Profile image architecture upgrades (local + Cloudinary-ready).
5. Face verification service stabilization across multiple runtime failures.
6. Operational runbook and production troubleshooting methodology.

---

## 2. Objectives

## Primary goals

1. Enable camera-based voting verification for public users.
2. Move from HTTP IP access to HTTPS domain access.
3. Keep existing system behavior working while fixing broken image/verify path.
4. Make the stack debuggable and operationally stable.

## Non-goals

1. Full redesign of authentication model.
2. Replacing face-api implementation entirely.
3. Migrating to Kubernetes or cloud-managed API gateway.

---

## 3. Starting State (Before Work)

## Infrastructure

- Oracle Linux VM.
- Docker Compose stack:
  - `frontend` (Nginx container)
  - `backend` (Spring Boot)
  - `mysql`
  - `imageverify` (Node/Express + face-api + canvas)

## Access pattern

- Public access over HTTP only.
- No host-level TLS endpoint on `443`.
- Browser camera blocked in production due insecure context.

## App symptoms

1. Profile image not displaying consistently.
2. Vote capture not reliably enforced/visible.
3. Registration/login errors ambiguous.
4. Face verification unstable under deployment conditions.

---

## 4. Final Architecture (After Work)

```text
Browser (HTTPS)
   |
   v
Host Nginx (TLS termination, 80/443)
   |
   v
Frontend container (127.0.0.1:8080->80)
   |
   +--> /api proxy to Backend container:8080
             |
             +--> MySQL container:3306
             |
             +--> imageverify container:5001 (/verify, /health)
```

Key improvements:

1. HTTPS + domain + certificate renewal path.
2. Explicit startup sequencing and health checks.
3. Better error transparency in frontend.
4. More robust image storage and verification handling.

---

## 5. Chronological Incident Timeline and Fixes

## Phase 1: HTTPS enablement and camera secure context

## Symptom

- Camera error in production:
  - "This page is not secure. Camera works only on HTTPS or localhost."

## Root cause

- Browsers require secure context for `getUserMedia`.
- Public access was `http://<ip>` only.

## Actions

1. Created DuckDNS domain.
2. Mapped domain to VM IP.
3. Opened OCI ingress ports `80` and `443`.
4. Configured host Nginx reverse proxy.
5. Issued Lets Encrypt certificate with certbot.
6. Enabled HTTP -> HTTPS redirect.

## Result

- `https://onlinevotingsystem.duckdns.org` reachable.
- Browser secure-context requirement satisfied.

## Interview point

- Camera failures in production are often platform security, not application JavaScript logic.

---

## Phase 2: Nginx config failure due shell variable expansion

## Symptom

- Nginx startup failed with:
  - `invalid number of arguments in "server_name" directive`

## Root cause

- `${DOMAIN}` was empty when config file was generated.
- `$host`, `$remote_addr` were expanded by shell unexpectedly.

## Fix

- Rewrote Nginx config using literal heredoc (`<<'EOF'`) and explicit domain string.

## Result

- Host Nginx validated and started successfully.

---

## Phase 3: Auth looked broken after HTTPS

## Symptom

- Browser showed invalid credentials for admin/user even though API worked.
- `curl` login sometimes gave `502` then later `200`.

## Root causes

1. Backend not fully started yet (startup race) caused early 502.
2. Frontend showed generic credential error even for network/server failures.
3. CORS crash in backend:
   - `allowCredentials=true` with `allowedOrigins="*"` is invalid in Spring.

## Fixes

1. Waited for backend readiness before testing login.
2. Updated frontend login/register pages to surface real server/network error.
3. Updated backend CORS config handling for wildcard/origin patterns.
4. Moved deploy config toward explicit allowed origins.

## Result

- Login and registration became diagnosable and functional.

---

## Phase 4: Registration verification impossible on fresh DB

## Symptom

- Registration repeatedly failed despite valid-looking input.

## Root cause

- Seeded dummy Aadhar and Voter ID records had mismatched name/DOB.
- Verification requires both records to match provided identity.

## Fix

- Data seeder now ensures a consistent demo pair exists/gets corrected:
  - Name: `John Doe`
  - DOB: `1990-01-01`
  - Aadhar: `123456789012`
  - Voter ID: `ABC1234567`

## Result

- Fresh deployments now support predictable registration testing.

---

## Phase 5: Profile image reliability and Cloudinary-ready architecture

## Symptom

- Profile image not appearing consistently.
- Need to move toward cloud media hosting model.

## Root causes

1. Fragile local-path assumptions.
2. Mixed URL/path forms in DB.
3. Missing/invalid default image fallback in some cases.

## Fixes

1. Added `ProfileImageStorageService` (local + cloud URL aware).
2. Updated profile upload flow to store canonical reference.
3. Improved profile image URL building on frontend with API base support.
4. Added fallback SVG when default image file unavailable.
5. Added Cloudinary environment hooks.

## Result

- Profile tab image behavior stabilized.
- Architecture ready for cloud-hosted profile photos.

---

## Phase 6: Vote flow UX hardening

## Symptom

- Vote modal sometimes attempted submission without reliable capture state.

## Fixes

1. Added explicit capture state:
   - `Capture Photo`
   - `Retake Photo`
2. Vote button disabled until capture exists.
3. Added camera error details:
   - permission denied
   - no device
   - insecure context
   - device busy
   - constraint mismatch

## Result

- Clear user path and fewer false failures.

---

## Phase 7: `imageverify` service instability

This was the most complex part and involved multiple distinct failures.

## Failure A: `invalid ELF header` on `canvas.node`

## Symptom

- Container crashed with:
  - `Error: .../canvas.node: invalid ELF header`

## Root cause

- Host `node_modules` leaked into Docker build context and overwrote Linux-compatible binary.

## Fix

1. Added `backend/imageverify/.dockerignore` including `node_modules`.
2. Switched Dockerfile install to `npm ci` for clean deterministic install.

## Result

- Native module binary mismatch resolved.

---

## Failure B: startup sequencing and health timing

## Symptom

- Compose reported unhealthy dependency and blocked startup.

## Root cause

- Service health timing did not align with heavy model-loading startup.

## Fix

1. Added `/health` endpoint in imageverify.
2. Added compose healthcheck.
3. Tuned dependencies and startup behavior.

## Result

- Predictable startup state and easier diagnostics.

---

## Failure C: TensorFlow native backend runtime crash

## Symptom

- Repeated crash on verify with stack traces in tfjs internals:
  - `TypeError: forwardFunc_1 is not a function`

## Root cause

- `@tensorflow/tfjs-node` runtime incompatibility in deployed environment with face-api stack.

## Fix

1. Removed tfjs-node usage path in imageverify.
2. Returned to stable pure JS backend for reliability.

## Result

- Process stopped crashing during verify operations.

---

## Failure D: backend EOF when calling `/verify`

## Symptom

- Backend error:
  - `Unexpected end of file from server`

## Root cause

- Upstream socket closed during transient service instability.

## Fix

1. Added retry logic in backend verify call for transient EOF.
2. Added `Connection: close` for that request.

## Result

- Reduced random request abort behavior.

---

## 6. Detailed "HTTP IP to HTTPS Domain" Implementation Steps

This is the complete sequence to reproduce the migration.

## Step 1: Create and map domain

```bash
export DUCK_DOMAIN="onlinevotingsystem"
export DUCK_TOKEN="<duckdns-token>"
export SERVER_IP="140.245.10.206"
curl "https://www.duckdns.org/update?domains=${DUCK_DOMAIN}&token=${DUCK_TOKEN}&ip=${SERVER_IP}"
```

Purpose:

- Update DNS record to VM public IP.

Validation:

```bash
dig +short onlinevotingsystem.duckdns.org
```

Expected:

- `140.245.10.206`

## Step 2: Open network ingress

In OCI:

1. Add inbound TCP `80` from `0.0.0.0/0`.
2. Add inbound TCP `443` from `0.0.0.0/0`.

On VM firewall:

```bash
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

## Step 3: Bind frontend only to localhost on host

In compose:

- `127.0.0.1:8080:80`

Purpose:

- Prevent direct public bypass of host TLS proxy.

## Step 4: Host Nginx reverse proxy

Config pattern:

```nginx
server {
    listen 80;
    server_name onlinevotingsystem.duckdns.org;
    client_max_body_size 20m;
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Validate and reload:

```bash
sudo nginx -t
sudo systemctl reload nginx
```

## Step 5: Install certificate

```bash
sudo certbot --nginx -d onlinevotingsystem.duckdns.org -m <email> --agree-tos --no-eff-email --redirect
```

Validate:

```bash
curl -I https://onlinevotingsystem.duckdns.org
```

## Step 6: Renewal check

```bash
sudo certbot renew --dry-run
```

If `certbot.timer` is unavailable, use cron.

---

## 7. Command Reference with Failure Interpretation

## Service status

```bash
docker compose ps
```

Use:

- Confirm `Up` and `healthy` states.

If failing:

- inspect per-service logs.

## Logs

```bash
docker compose logs --tail=200 backend
docker compose logs --tail=200 imageverify
docker compose logs --tail=200 frontend
```

Use:

- Trace API and inter-service failures.

## Backend -> imageverify connectivity

```bash
docker compose exec backend sh -c "wget -qSO- http://imageverify:5001/health 2>&1 | head -n 20"
```

Interpretation:

- `200` + JSON: service reachable and ready.
- connection refused: service down/restarting/not listening.

## External auth API smoke test

```bash
curl -i -X POST https://onlinevotingsystem.duckdns.org/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identifier":"admin@voting.com","password":"admin123"}'
```

Interpretation:

- `200`: backend/auth path OK.
- `502/504`: proxy/upstream issue.
- `4xx` with message: app/auth/data issue.

---

## 8. Notable Code Changes and Why They Matter

## Frontend

- `frontend/src/pages/UserDashboard.jsx`
  - enforced explicit capture before voting
  - improved webcam error states

- `frontend/src/pages/ProfilePage.jsx`
  - robust image URL composition and fallback handling

- `frontend/src/pages/Login.jsx`
- `frontend/src/pages/AdminLogin.jsx`
- `frontend/src/pages/Register.jsx`
  - now expose server/network errors clearly

- `frontend/src/context/AuthContext.jsx`
  - safer auth state setup on login path

- `frontend/nginx.conf`
  - API proxy timeout tuning

## Backend (Spring)

- `backend/onlinevotingsystem/src/main/java/com/project/onlinevotingsystem/service/ProfileImageStorageService.java`
  - abstracted profile image storage/resolve logic

- `backend/onlinevotingsystem/src/main/java/com/project/onlinevotingsystem/service/VoteService.java`
  - verification request hardening and retry behavior

- `backend/onlinevotingsystem/src/main/java/com/project/onlinevotingsystem/config/SecurityConfig.java`
  - CORS pattern fix for credentials

- `backend/onlinevotingsystem/src/main/java/com/project/onlinevotingsystem/config/DataSeeder.java`
  - fixed deterministic demo identity records

## Image verify service

- `backend/imageverify/server.js`
  - health endpoint
  - startup/exception resilience
  - stable backend execution path

- `backend/imageverify/Dockerfile`
  - deterministic install with `npm ci`

- `backend/imageverify/.dockerignore`
  - prevents host module contamination

## Infra and repo hygiene

- `docker-compose.yml`
  - dependency conditions
  - health checks
  - restart policy
  - environment defaults

- `.gitignore`
  - ignores node_modules and build artifacts

---

## 9. Root Cause Matrix

| Incident | Observed Error | Root Cause | Fix | Prevention |
|---|---|---|---|---|
| Camera blocked | insecure context message | HTTP public origin | HTTPS domain + TLS | enforce HTTPS only |
| Nginx failed | invalid `server_name` args | shell expansion + empty var | literal config write + explicit domain | avoid variable-dependent heredocs in root configs |
| Login looked invalid | generic frontend errors | masked network/server failures | real error surfacing | keep actionable client errors |
| Registration always failed | generic register fail | seed identity mismatch | deterministic matched seed records | startup data integrity checks |
| Backend CORS crash | Spring IllegalArgumentException | wildcard origin + credentials | origin pattern/explicit origins | production explicit origins |
| Verify service crash #1 | invalid ELF | wrong node_modules binary copied | .dockerignore + npm ci | never copy host node_modules |
| Verify service crash #2 | tfjs runtime TypeError | incompatible tfjs-node path | remove tfjs-node path | prefer stable backend; benchmark before native acceleration |
| Vote verify EOF | unexpected EOF from server | upstream crash/socket abort | backend retry + connection close | retry transient errors and harden service |

---

## 10. Production Runbook

## Standard deployment

```bash
git pull
docker compose down
docker compose build --no-cache
docker compose up -d
docker compose ps
```

## Post-deploy verification checklist

1. `docker compose ps` all services Up/healthy.
2. `curl -I https://onlinevotingsystem.duckdns.org` returns 200/301.
3. Auth smoke test returns token for admin.
4. Public election API responds.
5. `imageverify` health returns 200 from backend container.
6. Browser test:
   - login
   - profile image visible
   - capture image
   - cast vote

## Fast rollback approach

1. Keep previous known-good commit SHA.
2. `git checkout <good-sha>`
3. `docker compose build --no-cache`
4. `docker compose up -d`

---

## 11. Security and Operations Notes

1. Rotate exposed secrets:
   - DuckDNS token
   - Cloudinary API secret
2. Move secrets to server-side `.env` instead of git defaults.
3. Restrict CORS to exact origins in production.
4. Consider adding rate limits and brute-force protection on auth endpoints.
5. Add monitoring/alerting:
   - service restarts
   - healthcheck failures
   - certificate expiration warnings

---

## 12. Interview Preparation Section

## Technical concepts demonstrated

1. Secure context requirements for browser media APIs.
2. TLS termination and reverse proxy design.
3. Docker Compose networking and service discovery.
4. CORS policy correctness with credentials.
5. Native dependency handling in containers.
6. Startup ordering with health checks.
7. Root-cause debugging via layered observability.
8. Resilience techniques (retry, timeout tuning, graceful startup).

## Example STAR narrative

Situation:

- A voting platform worked in local HTTP but failed in production for camera verification and authentication after migration.

Task:

- Ship stable HTTPS deployment and restore end-to-end voting verification.

Action:

- Configured domain, DNS, TLS, Nginx proxy; fixed CORS config; repaired seed data; improved frontend error handling; stabilized image verification runtime by fixing Docker build hygiene and removing unstable tfjs-node backend; added health checks and backend retry logic.

Result:

- Production domain became fully functional over HTTPS with working login/registration, stable profile images, and reliable face verification pipeline.

## Sample interview Q&A

Q1. Why did webcam fail only in production?

- Because production was HTTP on a public origin; browsers allow camera only on secure context (HTTPS/localhost).

Q2. Why did CORS crash backend?

- `allowCredentials=true` cannot be used with `allowedOrigins=*` in Spring; must use explicit origins or patterns.

Q3. What caused `invalid ELF header`?

- Architecture mismatch from host native module copied into container. Resolved via `.dockerignore` and in-container deterministic install.

Q4. How did you confirm whether issue was frontend or backend?

- Used direct `curl` API calls, container logs, and backend->service internal health checks to isolate layer-by-layer.

Q5. Why remove `tfjs-node`?

- In this runtime it introduced repeatable crashes; reliability was more important than potential performance gain.

---

## 13. Exact Current Production Endpoint

- `https://onlinevotingsystem.duckdns.org`

---

## 14. Appendix: Common Errors and Immediate Fix Commands

## Error: "This page is not secure"

Fix:

1. Ensure browser URL is HTTPS domain.
2. Confirm certificate valid.
3. Confirm no mixed-content proxy.

## Error: 502 Bad Gateway from public domain

Fix:

```bash
docker compose ps
docker compose logs --tail=200 backend frontend
```

Usually backend not ready or upstream connect refused.

## Error: 504 Gateway Timeout

Fix:

1. Increase host Nginx proxy timeouts.
2. Confirm backend and imageverify processing times.

## Error: `Unexpected end of file from server`

Fix:

1. Inspect `imageverify` logs for crashes.
2. Rebuild imageverify clean.
3. Ensure stable runtime path (no crashing backend).

## Error: `invalid ELF header`

Fix:

1. Add `.dockerignore` excluding node_modules.
2. `docker compose build --no-cache imageverify`.

## Error: tfjs runtime stack crash

Fix:

1. Remove/disable tfjs-node path.
2. Redeploy imageverify.

---

## 15. Final Summary Statement

This deployment was completed through iterative diagnosis across browser security, host reverse proxy, Docker networking, backend framework behavior, and native Node runtime compatibility. The final system is HTTPS-enabled, operationally stable, and documented with repeatable commands and troubleshooting playbooks.

