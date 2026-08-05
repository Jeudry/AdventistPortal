# 🎯 Quick Start - Local Development with Orb Stack

## ⚡ Essential Commands

### Start Everything from Scratch
```bash
./reset-and-start-orb.sh
```

### Run the Application
```bash
./run-orb-app.sh
```

### Stop Services
```bash
docker compose down
```

## 🏗️ Local Architecture

```
┌─────────────────────────────────────────────┐
│         Your Mac (Host)                     │
│                                             │
│  ┌─────────────────────────────────────┐   │
│  │  Spring Boot Application            │   │
│  │  (Port 8080)                        │   │
│  │  Profile: orb                       │   │
│  └────────┬────────────────────────────┘   │
│           │                                 │
│           │ Connects via direct IPs         │
│           │                                 │
│  ┌────────▼────────────────────────────┐   │
│  │   Orb Stack (Docker)                │   │
│  │                                      │   │
│  │  ┌────────────────────────────┐     │   │
│  │  │ PostgreSQL                 │     │   │
│  │  │ 192.168.97.2:5432         │     │   │
│  │  │ DB: adventistportal            │     │   │
│  │  └────────────────────────────┘     │   │
│  │                                      │   │
│  │  ┌────────────────────────────┐     │   │
│  │  │ Redis                      │     │   │
│  │  │ 192.168.97.3:6379         │     │   │
│  │  └────────────────────────────┘     │   │
│  │                                      │   │
│  │  ┌────────────────────────────┐     │   │
│  │  │ RabbitMQ                   │     │   │
│  │  │ 192.168.97.4:5672         │     │   │
│  │  │ Management: :15672         │     │   │
│  │  └────────────────────────────┘     │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

## 📋 Startup Checklist

1. ✅ **Orb Stack installed and running**
2. ✅ **Docker services started**
   ```bash
   docker ps | grep adventistportal
   ```
3. ✅ **Environment variables configured**
   ```bash
   export MAIL_FROM_EMAIL=your-email@gmail.com
   export MAIL_PASSWORD="your-app-password"
   ```
4. ✅ **Application running at http://localhost:8080**

## ⚠️ Disabled Services in ORB

- **Firebase**: Push notifications are disabled
- **Supabase Storage**: Use local storage or mock

These services are not necessary for basic local development.

## 🔑 Credentials

### PostgreSQL
```
Host: 192.168.97.2
Port: 5432
Database: adventistportal
User: postgres
Password: postgres
```

### Redis
```
Host: 192.168.97.3
Port: 6379
Password: adventistportal_redis_password
```

### RabbitMQ
```
Host: 192.168.97.4
Port: 5672
User: adventistportal_user
Password: adventistportal_password
Virtual Host: adventistportal

Management UI: http://localhost:15672
```

## 🐛 Common Troubleshooting

### Error: "Could not resolve placeholder"
**Solution**: Verify all environment variables are configured
```bash
export MAIL_FROM_EMAIL=your-email@gmail.com
export MAIL_PASSWORD="your-app-password"
```

### Error: "password authentication failed"
**Solution**: Recreate containers from scratch
```bash
./reset-and-start-orb.sh
```

### Error: "schema does not exist"
**Solution**: Create schemas manually
```bash
docker exec adventistportal-postgres psql -U postgres -d adventistportal -c \
  "CREATE SCHEMA IF NOT EXISTS chat_service; \
   CREATE SCHEMA IF NOT EXISTS user_service; \
   CREATE SCHEMA IF NOT EXISTS notification_service;"
```

### Container IPs changed
**Solution**: Get new IPs and update `application-orb.yml`
```bash
docker inspect adventistportal-postgres -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'
docker inspect adventistportal-redis -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'
docker inspect adventistportal-rabbitmq -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'
```

## 📊 Monitoring Commands

### View all services logs
```bash
docker compose logs -f
```

### View specific service logs
```bash
docker logs -f adventistportal-postgres
docker logs -f adventistportal-redis
docker logs -f adventistportal-rabbitmq
```

### Check health status
```bash
docker compose ps
```

### Connect to PostgreSQL
```bash
docker exec -it adventistportal-postgres psql -U postgres -d adventistportal
```

### Connect to Redis
```bash
docker exec -it adventistportal-redis redis-cli -a adventistportal_redis_password
```

## 🎯 IntelliJ IDEA

### Available Configurations
- **AdventistPortalApi [ORB]**: Runs the application with ORB profile
- **Docker: Start Local Services (Orb Stack)**: Starts Docker services

### Run with Debug
1. Select `AdventistPortalApi [ORB]`
2. Click the debug icon (🐛)
3. Place breakpoints where needed

## 💡 Useful Tips

1. **Clean Data**: Use `./reset-and-start-orb.sh` when you want to start with empty database
2. **Performance**: Local services are much faster than remote ones
3. **Debugging**: You can see all SQL queries with DEBUG logging enabled
4. **RabbitMQ UI**: Monitor queues and messages at http://localhost:15672

## 📚 Complete Documentation

See `ORB_STACK_SETUP.md` for detailed documentation.

---

**Ready to start?** Run:
```bash
./reset-and-start-orb.sh && ./run-orb-app.sh
```