#!/bin/bash

echo "🛑 Deteniendo servicios de Orb Stack para AdventistPortal..."

docker compose down

echo "✅ Servicios detenidos"
echo ""
echo "💡 Para eliminar también los datos, usa:"
echo "   docker compose down -v"