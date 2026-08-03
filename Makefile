.PHONY: help local-up local-down local-logs local-restart orb-up orb-down orb-logs run-local run-orb run-prod build test clean docker-clean verify

help: ## Muestra esta ayuda
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

# Docker Compose Tradicional
local-up: ## Inicia los servicios locales con Docker Compose
	@./scripts/start-local.sh

local-down: ## Detiene los servicios locales
	@./scripts/stop-local.sh

local-logs: ## Muestra los logs de los servicios locales
	@docker-compose logs -f

local-restart: ## Reinicia los servicios locales
	@docker-compose restart

# Orb Stack (Recomendado para macOS)
orb-up: ## Inicia los servicios locales con Orb Stack
	@echo "🚀 Iniciando servicios con Orb Stack..."
	@docker-compose -f docker-compose.orb.yml up -d
	@echo "✅ Servicios iniciados con Orb Stack"

orb-down: ## Detiene los servicios de Orb Stack
	@docker-compose -f docker-compose.orb.yml down

orb-logs: ## Muestra los logs de Orb Stack
	@docker-compose -f docker-compose.orb.yml logs -f

orb-restart: ## Reinicia los servicios de Orb Stack
	@docker-compose -f docker-compose.orb.yml restart

# Ejecutar aplicación
run-local: ## Ejecuta la aplicación con perfil local (Docker Compose)
	@./gradlew :app:bootRun --args='--spring.profiles.active=local'

run-orb: ## Ejecuta la aplicación con perfil orb (Orb Stack)
	@./gradlew :app:bootRun --args='--spring.profiles.active=orb'

run-prod: ## Ejecuta la aplicación con perfil de producción
	@./gradlew :app:bootRun

build: ## Compila el proyecto
	@./gradlew build

test: ## Ejecuta los tests
	@./gradlew test

clean: ## Limpia los archivos de compilación
	@./gradlew clean

docker-clean: ## Detiene servicios y elimina volúmenes (BORRA DATOS)
	@docker-compose down -v

db-shell: ## Abre shell de PostgreSQL
	@docker exec -it adventistportal-postgres psql -U adventistportal_user -d adventistportal

redis-shell: ## Abre shell de Redis
	@docker exec -it adventistportal-redis redis-cli -a adventistportal_redis_password

docker-ps: ## Muestra el estado de los contenedores
	@docker-compose ps

status: ## Verifica el estado de todos los servicios
	@./scripts/check-status.sh

verify: ## Verifica la configuración del proyecto
	@./scripts/verify-setup.sh

# Atajos comunes
up: local-up ## Alias para local-up
down: local-down ## Alias para local-down
logs: local-logs ## Alias para local-logs
restart: local-restart ## Alias para local-restart
run: run-local ## Alias para run-local