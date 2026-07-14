# AdventistPortal Backend

Backend API for the AdventistPortal application built with Spring Boot and Kotlin.

## 🏗️ Architecture

The project is organized in a modular structure:

- **app**: Main application module with Spring Boot configuration
- **common**: Shared utilities and common code
- **user**: User management module
- **chat**: Chat functionality module
- **notification**: Notification services module

## 🚀 Quick Start

### Option A: IntelliJ IDEA (Recommended for Debugging) ⭐

1. **Start local services** (choose one):
   ```bash
   docker-compose up -d                              # Docker Compose tradicional
   docker-compose -f docker-compose.orb.yml up -d   # Orb Stack (mejor para macOS)
   ```

2. **Open project in IntelliJ IDEA**

3. **Select run configuration:**
   - **AdventistPortalApi [LOCAL]** 🏠 - Docker Compose tradicional
   - **AdventistPortalApi [ORB]** 🌟 - Orb Stack (mejor rendimiento en macOS)
   - **AdventistPortalApi [DEV]** 🔧 - Servicios remotos de desarrollo
   - **AdventistPortalApi [PROD]** 🚀 - Servicios de producción

4. **Click Debug 🐛 (Shift + F9)** o **Run ▶️ (Shift + F10)**

5. **Set breakpoints and debug!**

📚 **Documentation:**
- [Orb Stack Setup](docs/ORB_STACK_SETUP.md) - Complete local development setup
- [Quick Start Guide](docs/QUICK_START_ORB.md) - Get started quickly

### Option B: Command Line

1. **Start local infrastructure:**
   ```bash
   ./scripts/start-local.sh
   ```

2. **Run the application:**
   ```bash
   ./gradlew :app:bootRun --args='--spring.profiles.active=local'
   ```

3. **Stop local services:**
   ```bash
   ./scripts/stop-local.sh
   ```

For more details, see [docs/ORB_STACK_SETUP.md](docs/ORB_STACK_SETUP.md)

### Production/Remote Services

Run with default profile (uses Supabase, Redis Cloud, CloudAMQP):
```bash
./gradlew :app:bootRun
```

Make sure you have the required environment variables set:
- `POSTGRES_PASSWORD`
- `REDIS_PASSWORD`
- `RABBITMQ_PASSWORD`
- `JWT_SECRET_BASE64`
- `MAIL_FROM_EMAIL`
- `MAIL_PASSWORD`
- `SUPABASE_SERVICE_KEY`

## 🔧 Technology Stack

- **Language**: Kotlin
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL
- **Cache**: Redis
- **Message Queue**: RabbitMQ
- **Security**: Spring Security + JWT
- **Email**: Spring Mail (Gmail SMTP)
- **Cloud Storage**: Supabase
- **Push Notifications**: Firebase Cloud Messaging

## 📦 Local Development Stack

The `docker-compose.yml` provides:
- **PostgreSQL 16**: Local database
- **Redis 7**: Local cache
- **RabbitMQ 3**: Local message broker with management UI

## 🛠️ Build & Test

### Build the project:
```bash
./gradlew build
```

### Run tests:
```bash
./gradlew test
```

### Create a JAR:
```bash
./gradlew :app:bootJar
```

The JAR will be created in `app/build/libs/`

## 📝 Configuration Profiles

- **default**: Production configuration with remote services
- **local**: Development configuration with Docker services

Switch profiles using:
```bash
--spring.profiles.active=local
```

Or set environment variable:
```bash
export SPRING_PROFILES_ACTIVE=local
```

## 🔐 Environment Variables

Copy `.env.example` to `.env` and fill in your credentials:
```bash
cp .env.example .env
```

**Note**: Never commit the `.env` file to version control.

## 📚 API Documentation

The API will be available at:
- Local: http://localhost:8080
- Production: Your deployed URL

## 🐳 Docker Services

### Access RabbitMQ Management UI:
http://localhost:15672
- Username: `adventistportal_user`
- Password: `adventistportal_password`

### Connect to PostgreSQL:
```bash
docker exec -it adventistportal-postgres psql -U adventistportal_user -d adventistportal
```

### Connect to Redis:
```bash
docker exec -it adventistportal-redis redis-cli -a adventistportal_redis_password
```

## 📖 Additional Documentation

- [Local Development Guide](LOCAL_DEVELOPMENT.md)
- [Environment Variables](.env.example)

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Test locally with `./scripts/start-local.sh`
4. Submit a pull request

## 📄 License

[Your License Here]