#!/bin/bash
set -e

echo "🔥 REINICIO COMPLETO - Eliminando TODO y empezando desde cero"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

cd /Users/sargon/Documents/Coding/KMP/AdventistPortal

# 1. Detener TODO
echo "🛑 Deteniendo contenedores..."
docker compose down -v 2>/dev/null || true

# 2. Eliminar volúmenes específicos por nombre
echo "🗑️  Eliminando volúmenes antiguos..."
docker volume rm adventistportal_postgres_data 2>/dev/null || true
docker volume rm adventistportal_redis_data 2>/dev/null || true
docker volume rm adventistportal_rabbitmq_data 2>/dev/null || true

# 3. Eliminar cualquier volumen huérfano
docker volume prune -f 2>/dev/null || true

# 4. Esperar un momento
sleep 2

# 5. Iniciar servicios FRESCOS
echo "🚀 Iniciando servicios desde cero..."
docker compose up -d postgres redis rabbitmq zipkin

# 6. Esperar a que Postgres se inicialice completamente
echo "⏳ Esperando 15 segundos para inicialización completa..."
sleep 15

# 7. Verificar estado
echo ""
echo "📊 Estado de los contenedores:"
docker compose ps

# 8. Ver logs de Postgres
echo ""
echo "📝 Últimas líneas de logs de Postgres:"
docker logs adventistportal-postgres 2>&1 | tail -15

# 9. Probar conexión CON contraseña
echo ""
echo "🧪 Probando conexión con contraseña 'postgres'..."
PGPASSWORD='postgres' psql -h postgres.adventistportal.orb.local -p 5432 -U postgres -d adventistportal -c "
SELECT 
  '✅ Conexión exitosa!' as status, 
  current_database() as database, 
  current_user as usuario,
  version() as version;
" 2>&1

if [ $? -eq 0 ]; then
  echo ""
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
  echo "✅ ¡ÉXITO! Todos los servicios están listos"
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
  echo ""
  echo "📋 Configuración:"
  echo "   PostgreSQL:"
  echo "     - Host: postgres.adventistportal.orb.local:5432"
  echo "     - Usuario: postgres"
  echo "     - Contraseña: postgres"
  echo "     - Base de datos: adventistportal"
  echo ""
  echo "   Redis:"
  echo "     - Host: redis.adventistportal.orb.local:6379"
  echo "     - Contraseña: adventistportal_redis_password"
  echo ""
  echo "   RabbitMQ:"
  echo "     - Host: rabbitmq.adventistportal.orb.local:5672"
  echo "     - Usuario: adventistportal_user"
  echo "     - Contraseña: adventistportal_password"
  echo "     - Management UI: http://localhost:15672"
  echo ""
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
  echo ""
echo "🎯 Puedes ejecutar tu aplicación con:"
echo ""
echo "   Opción 1 - Desde terminal:"
echo "   -------------------------"
echo "   export MAIL_FROM_EMAIL='your-email@gmail.com'"
echo "   export MAIL_PASSWORD='your-app-password'"
echo "   ./scripts/run-orb-app.sh"
  echo ""
  echo "   Opción 2 - Desde IntelliJ:"
  echo "   --------------------------"
  echo "   Run Configuration: 'AdventistPortalApi [ORB]'"
  echo ""
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
  
  exit 0
else
  echo ""
  echo "❌ La conexión falló"
  echo ""
  echo "Revisa los logs completos:"
  echo "  docker logs adventistportal-postgres"
  
  exit 1
fi