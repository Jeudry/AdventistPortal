# AdventistPortal API - Orb Stack Setup (Local Development)

## 🎯 Overview

This setup allows you to run AdventistPortal API locally using **Orb Stack** with Docker services instead of cloud services (Supabase, Redis Cloud, CloudAMQP).

## 📦 Local Services

The following services run in Docker containers:

### PostgreSQL
- **Host**: `192.168.97.2`
- **Puerto**: `5432`
- **Base de datos**: `adventistportal`
- **Usuario**: `postgres`
- **Contraseña**: `postgres`
- **Schemas**: `public`, `chat_service`, `user_service`, `notification_service`

### Redis
- **Host**: `192.168.97.3`
- **Puerto**: `6379`
- **Contraseña**: `adventistportal_redis_password`

### RabbitMQ
- **Host**: `192.168.97.4`
- **Puerto**: `5672`
- **Management UI**: `http://localhost:15672`
- **Usuario**: `adventistportal_user`
- **Contraseña**: `adventistportal_password`
- **Virtual Host**: `adventistportal`

## 🚀 Quick Start

### 1. Start Docker Services

```bash
./reset-and-start-orb.sh
```

This script:
- Stops and removes old containers and volumes
- Starts fresh PostgreSQL, Redis and RabbitMQ services
- Waits until they are ready
- Verifies connectivity

### 2. Run the Application

**Option A - From terminal:**
```bash
./run-orb-app.sh
```

**Option B - From IntelliJ:**
1. Select configuration: `AdventistPortalApi [ORB]`
2. Click `Run` or `Debug`

## 📝 Important Files

### Docker Compose
- **`compose.yaml`**: Docker services configuration optimized for Orb Stack

### Spring Boot Configuration
- **`app/src/main/resources/application-orb.yml`**: Spring Boot profile for local development

### Utility Scripts
- **`reset-and-start-orb.sh`**: Restarts all services from scratch
- **`run-orb-app.sh`**: Runs the application with ORB profile
- **`stop-orb.sh`**: Stops services
- **`check-status.sh`**: Checks status of all services
- **`start-local.sh`**: Starts services (auto-detects Orb/Docker)

### Run Configurations (IntelliJ)
- **`.run/AdventistPortalApi [ORB].run.xml`**: Run configuration for IntelliJ
- **`.run/Docker_ Start Local Services (Orb Stack).run.xml`**: Starts Docker services from IntelliJ

## 🔧 Orb Stack Special Configuration

### Localhost Issue
Orb Stack has a special proxy for `localhost` that can cause authentication issues with PostgreSQL. That's why we use **direct IPs** instead of `localhost` or `.local` domains.

### Get Container IPs
If containers are recreated and IPs change, use:

```bash
docker inspect adventistportal-postgres -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'
docker inspect adventistportal-redis -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'
docker inspect adventistportal-rabbitmq -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'
```

Then update the IPs in `application-orb.yml`.

## 🐛 Troubleshooting

### Services won't start
```bash
# View logs
docker compose logs

# View logs for specific service
docker logs adventistportal-postgres
docker logs adventistportal-redis
docker logs adventistportal-rabbitmq
```

### Postgres authentication error
```bash
# Recreate from scratch
./reset-and-start-orb.sh
```

### Missing schemas error
```bash
docker exec adventistportal-postgres psql -U postgres -d adventistportal -c "CREATE SCHEMA IF NOT EXISTS chat_service; CREATE SCHEMA IF NOT EXISTS user_service; CREATE SCHEMA IF NOT EXISTS notification_service;"
```

### Verify connectivity
```bash
./check-status.sh
```

## 📊 Service Management

### Start services
```bash
docker compose up -d postgres redis rabbitmq zipkin
```

### Stop services (keep data)
```bash
docker compose down
```

### Stop and remove data
```bash
docker compose down -v
```

### View status
```bash
docker compose ps
```

### Access RabbitMQ Management UI
```bash
open http://localhost:15672
# User: adventistportal_user
# Password: adventistportal_password
```

## ⚙️ Required Environment Variables

```bash
export SPRING_PROFILES_ACTIVE=orb
export MAIL_FROM_EMAIL=tu-email@gmail.com
export MAIL_PASSWORD="tu-app-password"
```

## 🔐 Security

⚠️ **IMPORTANT**: This configuration is **ONLY for local development**. Do not use these credentials in production.

- PostgreSQL uses simple password (`postgres/postgres`)
- Rate limiting is disabled (`apply-limit: false`)
- Services are only accessible from localhost

## 📚 Profile Differences

| Feature | ORB (Local) | DEV | PROD |
|---------|-------------|-----|------|
| PostgreSQL | Local Docker | Supabase | Supabase |
| Redis | Local Docker | Redis Cloud | Redis Cloud |
| RabbitMQ | Local Docker | CloudAMQP | CloudAMQP |
| Rate Limiting | Disabled | Enabled | Enabled |
| SSL/TLS | Disabled | Enabled | Enabled |
| Logging | DEBUG | INFO | WARN |

## 💡 Tips

1. **Performance**: Local services are much faster than cloud services
2. **Debugging**: Use IntelliJ in debug mode with breakpoints
3. **Clean Data**: Use `reset-and-start-orb.sh` to start with a clean database
4. **Logs**: Enable DEBUG logging to see all SQL queries

## ✅ Startup Checklist

- [ ] Orb Stack installed and running
- [ ] Docker compose executed: `./reset-and-start-orb.sh`
- [ ] Services verified: `docker ps | grep adventistportal`
- [ ] Schemas created in PostgreSQL
- [ ] Environment variables configured
- [ ] Application running: `./run-orb-app.sh`

---

**Last updated**: December 3, 2025