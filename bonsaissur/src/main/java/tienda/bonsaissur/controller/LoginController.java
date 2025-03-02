package tienda.bonsaissur.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import tienda.bonsaissur.dtos.Usuario;
import tienda.bonsaissur.services.Services;
import tienda.bonsaissur.util.util;

@Controller
/**
 * Clase encargada de los controladores de las vistas
 */
public class LoginController {

	private final Services service;

	public LoginController(Services loginService) {
		this.service = loginService;
	}

	/**
	 * Metodo controlador encargado de mostrar las vistas de cerrar sesion
	 * 
	 * @param session
	 * @return
	 */
	@PostMapping("/cerrarSesion")
	public ResponseEntity<Void> cerrarSesion(HttpSession session) {

		try {
			
			util.ficheroLog("Usuario entro en cerrar sesion");
			if (session != null) {
				session.invalidate(); // Cierra la sesión
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();
				// Redirige al index
			} else {
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/login.jsp")).build();
				// Redirige al login
			}
		} catch (Exception e) {
util.ficheroLog("Ocurrio un error en cerrarSesion :"+e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();

	}

	/**
	 * Metodo controlador encargado de mostrar las vistas del login
	 * 
	 * @param correo
	 * @param contrasena
	 * @param session
	 * @return
	 */
	@PostMapping("/login")
	public ResponseEntity<Void> login(@RequestParam String correo, @RequestParam String contrasena,
			HttpSession session) {
		try {
			System.out.println();
			util.ficheroLog("Usuario con correo:"+correo+" entro en login");
			Usuario usu = service.login(correo, util.encriptarContraseña(contrasena), session);
			System.out.println("Rol de Persona:" + usu.getRol());
			if (usu.getNombre() != null) {
				if (usu.getRol().equals("Administrador")) {
					session.setAttribute("Usuario", usu); // Guarda el usuario en la sesión
					return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/login.jsp"))
							.build();
				} else {
					session.setAttribute("Usuario", usu); // Guarda el usuario en la sesión
					return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp"))
							.build();
				}
			} else {
				// Redirige a /login
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/loginUsu.jsp")).build();

			}
		} catch (Exception e) {
			util.ficheroLog("Ocurrio un error en login:"+e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();
	}

	/**
	 * Metodo controlador encargado de controlar las vistas de registro
	 * 
	 * @param nombre
	 * @param apellidos
	 * @param correo
	 * @param contrasena
	 * @param direccion
	 * @param telefono
	 * @param rol
	 * @param session
	 * @return
	 */
	@PostMapping("/registro")
	public ResponseEntity<Void> registro(@RequestParam String nombre, @RequestParam String apellidos,
			@RequestParam String correo, @RequestParam String contrasena, @RequestParam String direccion,
			@RequestParam String telefono, @RequestParam String rol, HttpSession session) {
		try {
			util.ficheroLog("Usuario entro en registro usuario");
			String respuesta = service.Post(nombre, apellidos, correo, direccion, telefono,
					util.encriptarContraseña(contrasena), rol);
			Usuario usuario = (Usuario) session.getAttribute("Usuario");
			if (respuesta.equals("Registro exitoso")) {

				// Redirige a /index
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();

			} else {
				if (usuario.getRol().equals("Administrador")) {

					return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/login.jsp"))
							.build();
				} else {

					return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp"))
							.build();
				}
			}
		} catch (Exception e) {
			util.ficheroLog("Ocurrio un error en registro:"+e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();
	}

	/**
	 * Metodo controlador encargado de controlar las vistas de eliminar
	 * 
	 * @param correo
	 * @param session
	 * @return
	 */
	@PostMapping("/eliminar")
	public ResponseEntity<Void> eliminar(@RequestParam String correo, HttpSession session) {
		try {
			util.ficheroLog("Usuario entro en eliminar usuario");
			service.getAllUsu(session);
			String respuesta = service.Delete(correo);

			if (respuesta.equals("Usuario Eliminado")) {

				// Redirige a /index // Redirige a la vista principal
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();
			} else {
				// Redirige a /login con error
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/login.jsp")).build();

			}
		} catch (Exception e) {
			util.ficheroLog("Ocurrio un error en eliminar:"+e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();
	}

	/**
	 * Metodo controlador encargado de mostrar las vistas de de actualizar
	 * 
	 * @param nombre
	 * @param apellidos
	 * @param direccion
	 * @param telefono
	 * @param session
	 * @return
	 */
	@PostMapping("/actualizar")
	public ResponseEntity<Void> actualizar(@RequestParam String nombre, @RequestParam String apellidos,
			@RequestParam String direccion, @RequestParam String telefono, HttpSession session) {
		try {
			util.ficheroLog("Usuario entro en actualizar usuario");
			Usuario usuario = (Usuario) session.getAttribute("Usuario");
			String respuesta = service.Put(nombre, apellidos, usuario.getCorreo(), direccion, telefono);

			if (respuesta.equals("Usuario actualizado")) {

				// Redirige a /index // Redirige a la vista principal
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();
			} else {
				// Redirige a /login con error
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/login.jsp")).build();

			}
		} catch (Exception e) {
			util.ficheroLog("Ocurrio un error en actualizar un usuario:"+e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();
	}

	/**
	 * Metodo controlador encargado de controlar las vistas derecordatorio de correo
	 * 
	 * @param correoRecuperar
	 * @param session
	 * @return
	 */
	@PostMapping("/correoRecuperar")
	public ResponseEntity<Void> recuperarContrasena(@RequestParam String correoRecuperar, HttpSession session) {
		try {
			util.ficheroLog("Usuario entro en recuperar contraseña");
			String respuesta = service.recuperarContrasena(correoRecuperar);

			if (respuesta.equals("Correo existente")) {

				// Redirige a /index // Redirige a la vista principal
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/mirarCorreo.jsp"))
						.build();
			} else {
				// Redirige a /login con error
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/login.jsp")).build();

			}
		} catch (Exception e) {
			util.ficheroLog("Ocurrio un error en la recuperacion de contraseña:"+e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();
	}

	/**
	 * Metodo controlador encargado de mostrar las vistas de escrbir contraseña de
	 * recuperacion
	 * 
	 * @param nuevaContrasena
	 * @param token
	 * @param session
	 * @return
	 */
	@PostMapping("/escribirContrasena")
	public ResponseEntity<Void> escribirContrasena(@RequestParam String nuevaContrasena, @RequestParam String token,
			HttpSession session) {
		try {
			util.ficheroLog("Usuario entro en escribir Contraseña");
			String respuesta = service.actualizarContrasena(nuevaContrasena, token);

			if (respuesta.equals("Usuario actualizado")) {

				// Redirige a /index // Redirige a la vista principal
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/loginUsu.jsp")).build();
			} else {
				// Redirige a /login con error
				return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();

			}
		} catch (Exception e) {
			util.ficheroLog("Ocurrio un error en escribir contraseña nueva:"+e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/bonsaissur/index.jsp")).build();
	}

}
