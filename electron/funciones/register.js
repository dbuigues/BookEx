const API_BASE_URL = 'https://bookex-u97b.onrender.com/api/usuarios';

// Pre-calentar el servidor de Render al cargar la página (evita cold start)
fetch('https://bookex-u97b.onrender.com/api/usuarios', { method: 'HEAD' }).catch(() => {});

// Crear listas por defecto para nuevo usuario (Favoritos y Reviews)
async function crearListasPorDefecto(idUsuario) {
	try {
		console.log('Creando listas por defecto para usuario ID:', idUsuario);

		// Crear ambas listas en paralelo
		const [resFav, resRev] = await Promise.all([
			fetch('https://bookex-u97b.onrender.com/api/listas', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ nombreLista: "Favoritos", idUsuario: idUsuario })
			}),
			fetch('https://bookex-u97b.onrender.com/api/listas', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ nombreLista: "Reviews", idUsuario: idUsuario })
			})
		]);

		console.log('Lista Favoritos:', resFav.ok ? 'creada' : 'error');
		console.log('Lista Reviews:', resRev.ok ? 'creada' : 'error');
	} catch (error) {
		console.error('Error al crear listas por defecto:', error);
	}
}

async function register(userToAdd) {

	const errorDiv = document.getElementById('RegisterError');

	let valido = true;
	let mailregex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	const pass = document.getElementById('regPassword').value;
	const pass2 = document.getElementById('regPassword2') ? document.getElementById('regPassword2').value : '';
	if (document.getElementById('regName').value.length > 255 || pass.length > 255 || !mailregex.test(document.getElementById("regEmail").value) || document.getElementById("regFotoPerfil").files.length == 0) {
		console.log(document.getElementById('regName').value.length);
		console.log(pass.length);
		console.log(document.getElementById("regEmail").value);
		console.log(document.getElementById("regFotoPerfil").files.length);
		console.log(mailregex.test(document.getElementById("regEmail")));
		errorDiv.innerText = `Completa correctamente todos los campos.`;
		valido = false;
	} else if (pass !== pass2) {
		errorDiv.innerText = `Las contraseñas no coinciden.`;
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
				
				// Usar el ID devuelto directamente, sin hacer GET extra
				crearListasPorDefecto(data.idUsuario);
				
				document.getElementById('regName').value = '';
				document.getElementById('regEmail').value = '';
				document.getElementById('regPassword').value = '';
				if (document.getElementById('regPassword2')) document.getElementById('regPassword2').value = '';
			} else {
				console.error('Error al crear usuario:', data);
				errorDiv.innerText = `Error: El correo ya está en uso.`;
				document.getElementById('regName').value = '';
				document.getElementById('regEmail').value = '';
				document.getElementById('regPassword').value = '';
				if (document.getElementById('regPassword2')) document.getElementById('regPassword2').value = '';
			}

		} catch (error) {
			console.error('Error de conexión:', error.message);
			errorDiv.innerText = `Error: No se pudo conectar con el servidor.`;
		}
	} else {
		console.log("NO VÁLIDO");
	}
}

// Convertir imagen de input file a base64 (comprimida y redimensionada)
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
			const img = new Image();
			img.onload = function () {
				const canvas = document.createElement('canvas');
				const MAX_SIZE = 200; // Máximo 200x200px para foto de perfil
				let width = img.width;
				let height = img.height;

				// Redimensionar manteniendo proporción
				if (width > height) {
					if (width > MAX_SIZE) {
						height = Math.round(height * MAX_SIZE / width);
						width = MAX_SIZE;
					}
				} else {
					if (height > MAX_SIZE) {
						width = Math.round(width * MAX_SIZE / height);
						height = MAX_SIZE;
					}
				}

				canvas.width = width;
				canvas.height = height;
				const ctx = canvas.getContext('2d');
				ctx.drawImage(img, 0, 0, width, height);

				// Comprimir como JPEG al 70% de calidad
				const base64 = canvas.toDataURL('image/jpeg', 0.7).split(',')[1];
				resolve(base64);
			};
			img.src = e.target.result;
		};
		reader.onerror = function (error) {
			reject(error);
		};
		reader.readAsDataURL(file);
	});
}

const addUsuario = document.getElementById('regUserButton');
addUsuario.onclick = async (event) => {
	addUsuario.disabled = true;
	addUsuario.textContent = 'Registrando...';

	try {
		const fotoInput = document.getElementById('regFotoPerfil');
		const fotoBase64 = await imagenABase64(fotoInput);

		const usuario = {
			nombre: document.getElementById('regName').value,
			correo: document.getElementById('regEmail').value,
			contrasena: document.getElementById('regPassword').value,
			fotoPerfil: fotoBase64
		};

		await register(usuario);
	} finally {
		addUsuario.disabled = false;
		addUsuario.textContent = 'Registrarse';
	}
};

