package tienda.bonsaissur.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import tienda.bonsaissur.dtos.Usuario;
import tienda.bonsaissur.util.util;

@Service
/**
 * Clase servicio encargada de contener los metodos de servicio de la aplicacion
 */
public class Services {

	public static Usuario UsuarioLogeado = new Usuario();
	public static List<Usuario> listaUsu = new ArrayList();
	public String API_URL_LOGIN = "http://localhost:8081/api/usuarios/login";
	public static String tokenGlobal;
/**
 * Metodo encargado de actualizar la lista de usuarios 
 * @param session
 * @return
 */
	public Usuario getAllUsu(HttpSession session) {
		String res = "";
		Usuario usu = new Usuario();
		try {
			HttpClient client = HttpClient.newHttpClient();
			ObjectMapper objectMapper = new ObjectMapper();

			String API_URL_SELECT = "http://localhost:8081/api/usuarios/todos"; // Endpoint para obtener todos los
			// usuarios

// 1. Obtener la lista actualizada de usuarios desde la API
			HttpRequest requestUsu = HttpRequest.newBuilder().uri(URI.create(API_URL_SELECT))
					.header("Accept", "application/json").GET().build();

			HttpResponse<String> responseUsu = client.send(requestUsu, HttpResponse.BodyHandlers.ofString());

			if (responseUsu.statusCode() == 200) {
// Convertir la respuesta JSON a lista de usuarios
				List<Usuario> usuarios = objectMapper.readValue(responseUsu.body(), new TypeReference<List<Usuario>>() {
				});
				session.setAttribute("listaUsuarios", usuarios); // Guardar en sesión
			}
		} catch (Exception e) {
			System.out.println("Ocurrio un error en getAllUsu");
			util.ficheroLog("Ocurrio un error en el servicio getAllUsu:"+e.getMessage());
			res = "Ocurrio un error en Login";
		}
		return usu;
	}
/**
 * Metodo encargado de hacer las querys para el login
 * @param correo
 * @param contrasena
 * @param session
 * @return
 */
	public Usuario login(String correo, String contrasena, HttpSession session) {
		String res = "";
		Usuario usu = new Usuario();
		try {
			HttpClient client = HttpClient.newHttpClient();
			ObjectMapper objectMapper = new ObjectMapper();
			String API_URL_LOGIN = "http://localhost:8081/api/usuarios/login"; // Endpoint para el login
			String API_URL_SELECT = "http://localhost:8081/api/usuarios/todos"; // Endpoint para obtener todos los
			// usuarios

// 1. Obtener la lista actualizada de usuarios desde la API
			HttpRequest requestUsu = HttpRequest.newBuilder().uri(URI.create(API_URL_SELECT))
					.header("Accept", "application/json").GET().build();

			HttpResponse<String> responseUsu = client.send(requestUsu, HttpResponse.BodyHandlers.ofString());

			if (responseUsu.statusCode() == 200) {
// Convertir la respuesta JSON a lista de usuarios
				List<Usuario> usuarios = objectMapper.readValue(responseUsu.body(), new TypeReference<List<Usuario>>() {
				});
				session.setAttribute("listaUsuarios", usuarios); // Guardar en sesión
				listaUsu = usuarios; // Actualizar la lista local

				usu.setContrasena(contrasena);
				System.out.println(contrasena);
				usu.setCorreo(correo);
				boolean comprobacion = false;
				for (Usuario Aux : listaUsu) {

					if (Aux.getCorreo().equals(usu.getCorreo()) && Aux.getContrasena().equals(usu.getContrasena())) {
						usu = Aux;
						comprobacion = true;
						if (comprobacion == false) {
							System.out.println("No se encontro en la lista");
						}
					}
				}

				// Crear un objeto Map con los datos de login
				Map<String, String> loginData = new HashMap<>();
				loginData.put("correo", usu.getCorreo());
				loginData.put("contrasena", usu.getContrasena());

				// Convertir el objeto a JSON
				String json = objectMapper.writeValueAsString(loginData);

				// Construir la solicitud POST para el login
				HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL_LOGIN))
						.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json))
						// Enviar los datos de login en el cuerpo
						.build();

				// Enviar la solicitud
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

				// Imprimir la respuesta
				if (response.statusCode() == 200) {
					System.out.println("Respuesta Login : " + response.body());
				} else {
					System.out.println("Error en el login: " + response.body());
				}
				res = response.body();

			}
		} catch (Exception e) {
			System.out.println("Ocurrio un error en Login");
			util.ficheroLog("Ocurrio un error en el servicio login:"+e.getMessage());
			res = "Ocurrio un error en Login";
		}
		return usu;
	}
/**
 * Metodo encargado de crear usuario
 * @param nombre
 * @param apellidos
 * @param correo
 * @param direccion
 * @param telefono
 * @param contrasena
 * @param rol
 * @return
 */
	public String Post(String nombre, String apellidos, String correo, String direccion, String telefono,
			String contrasena, String rol) {

		try {
			HttpClient client = HttpClient.newHttpClient();
			ObjectMapper objectMapper = new ObjectMapper();
			String API_URL = "http://localhost:8081/api/usuarios/crearUsu";

			// Crear un usuario
			Usuario usuario = new Usuario();
			usuario.setNombre(nombre);
			usuario.setApellidos(apellidos);
			usuario.setCorreo(correo);
			usuario.setDireccion(direccion);
			usuario.setTelefono(telefono);
			usuario.setContrasena(util.encriptarContraseña(contrasena));
			Timestamp fechaRegistro = Timestamp.from(Instant.now());
			usuario.setFechaRegistro(fechaRegistro);
			usuario.setRol(rol);

			// Convertir el objeto a JSON
			String json = objectMapper.writeValueAsString(usuario);

			// Construir la solicitud POST
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL))
					.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();

			// Enviar la solicitud
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			// Imprimir la respuesta
			System.out.println("Respuesta del servidor: " + response.body());
			return "Registro exitoso";

		} catch (Exception e) {
			util.ficheroLog("Ocurrio un error en el servicio crear Usuario:"+e.getMessage());
			return "Registro erroneo";
		}
	}
/**
 * Metodo encargado de eliminar usuarios
 * @param correo
 * @return
 */
	public String Delete(String correo) {
		String respuesta = "";
		try {

			HttpClient client = HttpClient.newHttpClient();
			ObjectMapper objectMapper = new ObjectMapper();
			String API_URL_DELETE = "http://localhost:8081/api/usuarios/eliminar"; // Suponiendo que tu API tiene un
																					// endpoint para eliminar
			String API_URL_SELECT = "http://localhost:8081/api/usuarios/todos"; // Endpoint para obtener todos los
																				// usuarios

			// 1. Obtener la lista actualizada de usuarios desde la API
			HttpRequest requestUsu = HttpRequest.newBuilder().uri(URI.create(API_URL_SELECT))
					.header("Accept", "application/json").GET().build();

			HttpResponse<String> responseUsu = client.send(requestUsu, HttpResponse.BodyHandlers.ofString());

			if (responseUsu.statusCode() == 200) {
				// Convertir la respuesta JSON a lista de usuarios
				List<Usuario> usuarios = objectMapper.readValue(responseUsu.body(), new TypeReference<List<Usuario>>() {
				});

				// 2. Usar el método eliminarUsu para buscar el usuario
				Usuario usuario = new Usuario();

				usuario.setCorreo(correo);

				if (usuario != null && !usuario.getCorreo().isEmpty()) {
					// 3. Si el usuario existe, crear un objeto con correo y contraseña para
					// eliminarlo
					Map<String, String> userToDelete = new HashMap<>();
					userToDelete.put("correo", usuario.getCorreo());

					// Convertir el mapa a JSON
					String json = objectMapper.writeValueAsString(userToDelete);

					// 4. Enviar la solicitud DELETE con el JSON en el cuerpo
					// No necesitas pasar correo en la URL
					HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL_DELETE))
							.header("Content-Type", "application/json")
							.method("DELETE", HttpRequest.BodyPublishers.ofString(json)) // Usar DELETE con cuerpo
							.build();

					HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

					if (response.statusCode() == 200) {
						System.out.println("Usuario " + usuario.getCorreo() + " eliminado correctamente.");
						// Actualizar la lista local para reflejar el cambio
						listaUsu.remove(usuario);

						respuesta = "Usuario Eliminado";
					} else {
						System.out.println("Error al eliminar usuario: " + response.body());
						respuesta = "Error al eliminar";
					}
				} else {
					System.out.println("Usuario con correo " + usuario.getCorreo() + " no encontrado.");
					respuesta = "Usuario no encontrado";
				}
			} else {
				System.out.println("Error al obtener la lista de usuarios: " + responseUsu.statusCode());
			}
		} catch (Exception e) {
			System.out.println("Ocurrio un error en Eliminar:"+e.getMessage());
			util.ficheroLog("Ocurrio un error en Eliminar:"+e.getMessage());
		}
		System.out.println(respuesta);
		return respuesta;
	}
/**
 * Metodo encargado de actualizar los datos de los usuarios
 * @param nombre
 * @param apellidos
 * @param correo
 * @param direccion
 * @param telefono
 * @return
 */
	public String Put(String nombre, String apellidos, String correo, String direccion, String telefono) {
		String resp = "";
		try {
			HttpClient client = HttpClient.newHttpClient();
			ObjectMapper objectMapper = new ObjectMapper();
			String API_URL = "http://localhost:8081/api/usuarios/actualizar";
			String API_URL_SELECT = "http://localhost:8081/api/usuarios/todos";

			// 1. Obtener la lista actualizada de usuarios desde la API
			HttpRequest requestUsu = HttpRequest.newBuilder().uri(URI.create(API_URL_SELECT))
					.header("Accept", "application/json").GET().build();

			HttpResponse<String> responseUsu = client.send(requestUsu, HttpResponse.BodyHandlers.ofString());

			if (responseUsu.statusCode() == 200) {
				// Convertir la respuesta JSON a lista de usuarios
				List<Usuario> usuarios = objectMapper.readValue(responseUsu.body(), new TypeReference<List<Usuario>>() {
				});
				listaUsu = usuarios; // Actualizar la lista local

				System.out.println("Total de usuarios cargados:" + listaUsu.size());
				// 2. Buscar el usuario con el correo proporcionado
				Usuario usuario = usuarios.stream().filter(u -> correo.equalsIgnoreCase(u.getCorreo())).findFirst()
						.orElse(null);

				if (usuario != null) {
					Usuario usu = usuario;
					// Actualizar los datos del usuario
					usu.setNombre(nombre);
					usu.setApellidos(apellidos);
					usu.setCorreo(correo);
					usu.setDireccion(direccion);
					usu.setTelefono(telefono);

					// 3. Enviar la solicitud PUT con el usuario actualizado
					String json = objectMapper.writeValueAsString(usu);

					HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL))
							.header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(json))
							.build();

					HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

					System.out.println("Respuesta del servidor: " + response.body());
					resp = "Usuario actualizado";
				} else {
					System.out.println("Usuario con correo " + correo + " no encontrado.");
					resp = "Usuario no encontrado";
				}
			} else {
				System.out.println("Error al obtener la lista de usuarios: " + responseUsu.statusCode());
				resp = "Ocurrio un error ";
			}
		} catch (Exception e) {
			util.ficheroLog("Ocurrio un error en el servicio actualizar:"+e.getMessage());
			
		}
		return resp;
	}

	/**
	 * Metodo encargado de enviar los correos de recuperar contrasena
	 * @param correoDestinatario
	 * @param asunto
	 * @param token
	 * @throws MessagingException
	 */
	public void enviarCorreo(String correoDestinatario, String asunto, String token) throws MessagingException {
		try {
		JavaMailSender mailSender = configurarServidorSMTP();
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		String mensaje = "http://localhost:8080/bonsaissur/nuevaContrasena.jsp?token=";

		helper.setTo(correoDestinatario);
		helper.setSubject(asunto);
		helper.setText(mensaje + token, false); // false = texto plano
		helper.setFrom("bonsaissur@gmail.com");

		mailSender.send(mimeMessage);
		System.out.println("[Correo enviado a " + correoDestinatario + "]");
		} catch (Exception e) {
			util.ficheroLog("Ocurrio un error en el servicio enciar correo:"+e.getMessage());
			
		}
	}

/**
 * Configuración del servidor SMTP
 * @return
 */
	private JavaMailSender configurarServidorSMTP() {
		try {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);
		mailSender.setUsername("bonsaissur@gmail.com");
		mailSender.setPassword("msprjeksnbhekmjc");

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");

		mailSender.setJavaMailProperties(props);
		return mailSender;
		} catch (Exception e) {
			util.ficheroLog("Ocurrio un error en el servicio configurarServidorSMTP:"+e.getMessage());
			
		}
		return null;
	}
/**
 * Metodo encargado de hacer la validacion de existencia del correo
 * @param correoDestinatario
 * @return
 */
	public String recuperarContrasena(String correoDestinatario) {
		String resp = "";
		HttpClient client = HttpClient.newHttpClient();
		ObjectMapper objectMapper = new ObjectMapper();
		String API_URL = "http://localhost:8081/api/usuarios/actualizar";
		try {

			HttpRequest requestUsu = HttpRequest.newBuilder()
					.uri(URI.create("http://localhost:8081/api/usuarios/todos")).header("Accept", "application/json")
					.GET().build();

			HttpResponse<String> responseUsu = client.send(requestUsu, HttpResponse.BodyHandlers.ofString());

			if (responseUsu.statusCode() == 200) {
				List<Usuario> usuarios = objectMapper.readValue(responseUsu.body(), new TypeReference<List<Usuario>>() {
				});
				Services.listaUsu = usuarios;
				Usuario usuario = Services.listaUsu.stream()
						.filter(usu -> usu.getCorreo().equalsIgnoreCase(correoDestinatario)).findFirst().orElse(null);

				if (usuario == null) {
					System.out.println("Usuario no encontrado.");
				}
				// Generar Token único
				tokenGlobal = "";
				String token = UUID.randomUUID().toString();
				usuario.setToken(token);
				String json = objectMapper.writeValueAsString(usuario);
				HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL))
						.header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(json))
						.build();

				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

				System.out.println("Respuesta del servidor: " + response.body());
				resp = "Usuario actualizado";

				// Calcular la fecha de expiración del token (1 hora desde ahora)
				usuario.setFechaToken(new Timestamp(System.currentTimeMillis() + 3600000));
				System.out.println(usuario.toString());
				enviarCorreo(correoDestinatario, "Recuperacion de contraseña", token);
				resp = "Correo existente";

			}
		} catch (Exception e) {
		util.ficheroLog("Ocurrio un error en el servicio recuperar contraseña;"+e.getMessage());
		}
		return resp;
	}
/**
 * Metodo encargado de actualizar la contrasena 
 * @param contrasena
 * @param token
 * @return
 */
	public String actualizarContrasena(String contrasena, String token) {
		String resp = "";
		try {
			HttpClient client = HttpClient.newHttpClient();
			ObjectMapper objectMapper = new ObjectMapper();
			String API_URL = "http://localhost:8081/api/usuarios/actualizar";
			String API_URL_SELECT = "http://localhost:8081/api/usuarios/todos";

			// 1. Obtener la lista actualizada de usuarios desde la API
			HttpRequest requestUsu = HttpRequest.newBuilder().uri(URI.create(API_URL_SELECT))
					.header("Accept", "application/json").GET().build();

			HttpResponse<String> responseUsu = client.send(requestUsu, HttpResponse.BodyHandlers.ofString());

			if (responseUsu.statusCode() == 200) {
				// Convertir la respuesta JSON a lista de usuarios
				List<Usuario> usuarios = objectMapper.readValue(responseUsu.body(), new TypeReference<List<Usuario>>() {
				});
				listaUsu = usuarios; // Actualizar la lista local

				System.out.println("Total de usuarios cargados:" + listaUsu.size());
				// 2. Buscar el usuario con el correo proporcionado
				Usuario usuario = new Usuario();
				String aux = "";
				for (Usuario usu : usuarios) {
					if (tok   en.equalsIgnoreCase(usu.getToken())) {
						// Convertir Timestamp a LocalDate
						LocalDate fechaToken = usu.getFechaToken().toLocalDateTime().toLocalDate();

						// Comparar con la fecha actual
						if (fechaToken.isBefore(LocalDate.now())) {
							System.out.println("✅ El token es válido.");
							aux = "El token es válido";
							usuario = usu;
						} else {
							System.out.println("❌ El token ha expirado.");
							aux = "El token ha expirado";
						}
					}
				}

				if (usuario != null) {

					// Actualizar los datos del usuario
					usuario.setContrasena(util.encriptarContraseña(contrasena));

					// 3. Enviar la solicitud PUT con el usuario actualizado
					String json = objectMapper.writeValueAsString(usuario);

					HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL))
							.header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(json))
							.build();

					HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

					System.out.println("Respuesta del servidor: " + response.body());
					resp = "Usuario actualizado";
				} else {
					resp = "Usuario no encontrado";
				}
			} else {
				System.out.println("Error al obtener la lista de usuarios: " + responseUsu.statusCode());
				resp = "Ocurrio un error ";
			}
		} catch (Exception e) {
			util.ficheroLog("Ocurrio un error en el servicio actualizar contraseña;"+e.getMessage());
		}
		return resp;
	}

}
