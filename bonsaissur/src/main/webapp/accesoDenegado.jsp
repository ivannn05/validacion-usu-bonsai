<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Acceso Denegado</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #F3F0E5; /* Color de fondo personalizado */
            color: white;
        }
        .card {
            background-color: white;
            color: #5B3E34;
        }
        .btn-primary {
            background-color: #5B3E34;
            border-color: #5B3E34;
        }
        .btn-primary:hover {
            background-color: #4A322A;
            border-color: #4A322A;
        }
    </style>
</head>
<body class="d-flex align-items-center justify-content-center vh-100">

    <div class="card p-4 shadow-lg text-center" style="max-width: 400px; width: 100%;">
        <h4 class="text-danger mb-3">Acceso Denegado</h4>
        <p class="text-muted">No tienes permiso para acceder a esta página.</p>
        
        <div class="mt-3">
            <a href="<%=request.getContextPath()%>/index.jsp" class="btn btn-primary w-100">Volver al inicio</a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
