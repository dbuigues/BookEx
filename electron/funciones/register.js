const API_BASE_URL = 'https://bookex-u97b.onrender.com/api/usuarios';

// Crear lista de favoritos para nuevo usuario
async function crearListaFavoritos(correoUsuario) {
	try {
		console.log('Creando lista de favoritos para:', correoUsuario);
		
		// Obtener el ID del usuario por su correo
		const idCall = await fetch(`https://bookex-u97b.onrender.com/api/usuarios/correo/${encodeURIComponent(correoUsuario)}`);
		const userData = await idCall.json();
		const id = userData.idUsuario;
		
		console.log("ID de usuario obtenido:", id);

		// Crear la lista de favoritos
		const lista = {
			nombreLista: "Favoritos",
			idUsuario: id
		};
		
		const response = await fetch('https://bookex-u97b.onrender.com/api/listas', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(lista)
		});
		
		if (response.ok) {
			console.log("Lista de favoritos creada exitosamente");
		} else {
			console.error("Error al crear lista de favoritos");
		}
	} catch (error) {
		console.error('Error al crear lista de favoritos:', error);
	}
}

async function register(userToAdd) {

	const errorDiv = document.getElementById('RegisterError');

	let valido = true;
	let mailregex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	if (document.getElementById('regName').value.length > 255 || document.getElementById('regPassword').value.length > 255 || !mailregex.test(document.getElementById("regEmail").value) || document.getElementById("regFotoPerfil").files.length == 0) {
		console.log(document.getElementById('regName').value.length);
		console.log(document.getElementById('regPassword').value.length);
		console.log(document.getElementById("regEmail").value);
		console.log(document.getElementById("regFotoPerfil").files.length);
		console.log(mailregex.test(document.getElementById("regEmail")));
		errorDiv.innerText = `Completa correctamente todos los campos.`;
		valido = false;
	}

	if (valido) {
		console.log("VÁLIDO.");
		console.log('Agregando usuario:', userToAdd);
		console.log('JSON enviado:', JSON.stringify(userToAdd));

		try {
			const response = await fetch(`${API_BASE_URL}`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(userToAdd),
			});
			console.log(userToAdd);

			const data = await response.json();

			if (response.ok) {
				errorDiv.innerText = `Usuario creado con éxito.`;
				console.log(data);
				
				// Crear lista de favoritos automáticamente
				await crearListaFavoritos(userToAdd.correo);
				
				document.getElementById('regName').value = '';
				document.getElementById('regEmail').value = '';
				document.getElementById('regPassword').value = '';
			} else {
				console.error('Error al crear usuario:', data);
				errorDiv.innerText = `Error: El correo ya está en uso.`;
				document.getElementById('regName').value = '';
				document.getElementById('regEmail').value = '';
				document.getElementById('regPassword').value = '';
			}

		} catch (error) {
			console.error('Error de conexión:', error.message);
			errorDiv.innerText = `Error: No se pudo conectar con el servidor.`;
		}
	} else {
		console.log("NO VÁLIDO");
	}
}

// Convertir imagen de input file a base64
function imagenABase64(inputFile) {
	return new Promise((resolve, reject) => {
		const file = inputFile.files[0];
		if (!file) {
			console.log("No se ha seleccionado ningún archivo.");
			resolve(null);
			return;
		}
		const reader = new FileReader();
		reader.onload = function (e) {
			// e.target.result es un string dataURL: "data:image/png;base64,..."
			// Extraemos solo la parte base64
			const base64 = e.target.result.split(',')[1];
			resolve(base64);
		};
		reader.onerror = function (error) {
			reject(error);
		};
		reader.readAsDataURL(file);
	});
}

const addUsuario = document.getElementById('regUserButton');
addUsuario.onclick = async (event) => {
	//event.preventDefault(); // Evitar que el formulario recargue la página

	const fotoInput = document.getElementById('regFotoPerfil');
	const fotoBase64 = await imagenABase64(fotoInput);

	const usuario = {
		nombre: document.getElementById('regName').value,
		correo: document.getElementById('regEmail').value,
		contrasena: document.getElementById('regPassword').value,
		fotoPerfil: fotoBase64
	};

	await register(usuario);

};

