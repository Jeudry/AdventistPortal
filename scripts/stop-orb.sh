#!/bin/bash

echo "🛑 Deteniendo servicios de Orb Stack para AdventistPortal..."

docker-compose -f compose.infra.yaml down

echo "✅ Servicios detenidos"
echo ""
echo "💡 Para eliminar también los datos, usa:"
echo "   docker-compose -f compose.infra.yaml down -v"