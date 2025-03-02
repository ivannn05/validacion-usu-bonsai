<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Restablecer Contraseña</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="icon" href="imagenes/LogoTienda.jpg" type="image/jpg">
</head>
<body class="d-flex align-items-center justify-content-center vh-100 bg-light">

<%
    // Obtener el token desde la URL
    String token = request.getParameter("token"); 
%>

<div class="card p-4 shadow-lg" style="max-width: 400px; width: 100%;">
    <h4 class="text-center mb-3">Restablecer Contraseña</h4>
    <p class="text-muted text-center">Ingresa tu nueva contraseña y confírmala.</p>

    <form action="<%= request.getContextPath() %>/escribirContrasena" method="POST" onsubmit="return validarContrasena()">
        <input type="hidden" name="token" value="<%= token %>"> 

        <div class="mb-3">
            <label for="nuevaContrasena" class="form-label">Nueva Contraseña</label>
            <input type="password" class="form-control" name="nuevaContrasena" id="nuevaContrasena" placeholder="Nueva contraseña" required>
        </div>

        <div class="mb-3">
            <label for="confirmarContrasena" class="form-label">Confirmar Contraseña</label>
            <input type="password" class="form-control" name="confirmarContrasena" id="confirmarContrasena" placeholder="Confirma tu contraseña" required>
            <div id="errorMensaje" class="text-danger mt-2" style="display: none;">❌ Las contraseñas no coinciden.</div>
        </div>

        <button type="submit" class="btn btn-primary w-100">Actualizar Contraseña</button>
    </form>

    <div class="text-center mt-3">
        <a href="<%= request.getContextPath() %>/index.jsp" class="text-decoration-none">Volver al inicio</a>
    </div>
</div>

<script>
    function validarContrasena() {
        let pass1 = document.getElementById("nuevaContrasena").value;
        let pass2 = document.getElementById("confirmarContrasena").value;
        let errorMensaje = document.getElementById("errorMensaje");

        if (pass1 !== pass2) {
            errorMensaje.style.display = "block"; // Muestra el mensaje de error
            return false; // Evita el envío del formulario
        } else {
            errorMensaje.style.display = "none"; // Oculta el mensaje de error
            return true; // Permite el envío del formulario
        }
    }
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
