package tienda.bonsaissur.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Clase encargada de los metodos utilizados en carias partes de la web
 */
public class util {
	/**
	 * Metodo encargado de encriptar las contraseñas
	 * 
	 * @param password
	 * @return
	 */
	public static String encriptarContraseña(String password) {
		try {
			// Creamos una instancia de MessageDigest con el algoritmo SHA-256
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			// Convertimos la contraseña a bytes y generamos el hash
			byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

			// Convertimos los bytes a una cadena hexadecimal
			StringBuilder hexString = new StringBuilder();
			for (byte b : encodedhash) {
				String hex = String.format("%02x", b);
				hexString.append(hex);
			}

			// Retornamos el hash en formato de String
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			ficheroLog("Ocurrio un error en encriptarcontraseña");
			return password;
		}
	}

	public static String creacionNombreFichero() {
		String fecha;
		/// Para poner un formato a una fecha con DateTime
		DateTimeFormatter formato = DateTimeFormatter.ofPattern("ddMMyy");
		LocalDate fechaActual = LocalDate.now();
		fecha = fechaActual.format(formato);
		return fecha;
	}

	/**
	 * Metodo encargado del fichero log
	 * 
	 * @param mensaje
	 * @param usu
	 */
	public static void ficheroLog(String mensaje) {
		try {
			String rutaCompletaLog = "C:\\Users\\ivan\\Desktop\\workspaceProyectoDWS\\bonsaissur\\logs"
					.concat("\\").concat("log-").concat(creacionNombreFichero()).concat(".txt");
			BufferedWriter escribe = new BufferedWriter(new FileWriter(rutaCompletaLog, true));

			escribe.write(mensaje.concat("\n"));

			escribe.close();
		} catch (IOException e1) {
e1.printStackTrace();
			System.out.println("Hubo un error en el fichero log  Error:001");
		}
	}

}
