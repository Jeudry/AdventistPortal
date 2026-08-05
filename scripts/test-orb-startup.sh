#!/bin/bash

echo "🧪 Probando inicio de la aplicación AdventistPortal con perfil ORB"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

cd /Users/sargon/Documents/Coding/KMP/AdventistPortal

# Verificar servicios Docker
echo "1️⃣ Verificando servicios Docker..."
POSTGRES_STATUS=$(docker ps --filter "name=adventistportal-postgres" --format "{{.Status}}" | grep -c "Up")
REDIS_STATUS=$(docker ps --filter "name=adventistportal-redis" --format "{{.Status}}" | grep -c "Up")
RABBITMQ_STATUS=$(docker ps --filter "name=adventistportal-rabbitmq" --format "{{.Status}}" | grep -c "Up")

if [ "$POSTGRES_STATUS" -eq 0 ] || [ "$REDIS_STATUS" -eq 0 ] || [ "$RABBITMQ_STATUS" -eq 0 ]; then
  echo "❌ Servicios Docker no están corriendo"
  echo "   Ejecuta: docker-compose -f compose.infra.yaml up -d"
  exit 1
fi

echo "✅ Servicios Docker están corriendo"
echo ""

# Configurar variables
export SPRING_PROFILES_ACTIVE=orb

# Load environment variables from .env.local if it exists
if [ -f "scripts/.env.local" ]; then
  export $(grep -v '^#' scripts/.env.local | xargs)
fi

# Check if required environment variables are set
if [ -z "$MAIL_FROM_EMAIL" ] || [ -z "$MAIL_PASSWORD" ]; then
  echo "❌ Missing environment variables: MAIL_FROM_EMAIL and/or MAIL_PASSWORD"
  echo "   Set them before running this script."
  exit 1
fi

echo "2️⃣ Compilando proyecto..."
./gradlew :app:build -x test --quiet

if [ $? -ne 0 ]; then
  echo "❌ Error al compilar el proyecto"
  exit 1
fi

echo "✅ Proyecto compilado correctamente"
echo ""

echo "3️⃣ Iniciando aplicación (esto tomará ~30 segundos)..."
echo "   Puedes ver logs completos en: /tmp/adventistportal-orb.log"
echo ""

# Iniciar aplicación en background
./gradlew :app:bootRun > /tmp/adventistportal-orb.log 2>&1 &
APP_PID=$!

echo "   PID de la aplicación: $APP_PID"

# Esperar y monitorear el inicio
for i in {1..60}; do
  # Verificar si el proceso sigue corriendo
  if ! ps -p $APP_PID > /dev/null 2>&1; then
    echo ""
    echo "❌ La aplicación se detuvo inesperadamente"
    echo ""
    echo "Últimas líneas del log:"
    tail -30 /tmp/adventistportal-orb.log
    exit 1
  fi
  
  # Verificar si la app está lista
  if grep -q "Started AdventistPortalApiApplicationKt" /tmp/adventistportal-orb.log 2>/dev/null; then
    echo ""
    echo "✅ ¡Aplicación iniciada exitosamente!"
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "🎉 AdventistPortal API está corriendo"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "📍 URL: http://localhost:8080"
    echo "📊 PID: $APP_PID"
    echo "📝 Logs: tail -f /tmp/adventistportal-orb.log"
    echo ""
    echo "Para detener la aplicación:"
    echo "   kill $APP_PID"
    echo ""
    exit 0
  fi
  
  # Verificar si hay error de Firebase (que debería estar resuelto)
  if grep -q "Firebase service account file not found" /tmp/adventistportal-orb.log 2>/dev/null; then
    echo ""
    echo "❌ Error: Firebase todavía está intentando inicializarse"
    echo ""
    echo "Últimas líneas del log:"
    tail -30 /tmp/adventistportal-orb.log
    kill $APP_PID
    exit 1
  fi
  
  # Mostrar progreso
  if [ $((i % 5)) -eq 0 ]; then
    echo "   Esperando... (${i}s)"
  fi
  
  sleep 1
done

echo ""
echo "⚠️  La aplicación está tardando más de lo esperado"
echo ""
echo "Últimas líneas del log:"
tail -50 /tmp/adventistportal-orb.log
echo ""
echo "La aplicación sigue corriendo (PID: $APP_PID)"
echo "Monitorea el log con: tail -f /tmp/adventistportal-orb.log"