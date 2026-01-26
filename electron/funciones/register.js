// ============================================
// CONFIGURACIÓN DE LA API (INVENTADA)
// ============================================
const API_BASE_URL = 'http://localhost:8080/api/usuarios';

async function register(userToAdd) {

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
            const errorDiv = document.getElementById('RegisterError');
            errorDiv.innerText = `Usuario creado con éxito.`;
            console.log(data);
            document.getElementById('regName').value = '';
            document.getElementById('regEmail').value = '';
            document.getElementById('regPassword').value = '';
        } else {
            console.error('Error al crear usuario:', data);
            const errorDiv = document.getElementById('RegisterError');
            errorDiv.innerText = `Error: El correo ya está en uso.`;
            document.getElementById('regName').value = '';
            document.getElementById('regEmail').value = '';
            document.getElementById('regPassword').value = '';
        }

    } catch (error) {
        console.error('Error de conexión:', error.message);
        const errorDiv = document.getElementById('RegisterError');
        errorDiv.innerText = `Error: No se pudo conectar con el servidor.`;
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
    reader.onload = function(e) {
      // e.target.result es un string dataURL: "data:image/png;base64,..."
      // Extraemos solo la parte base64
      const base64 = e.target.result.split(',')[1];
      resolve(base64);
    };
    reader.onerror = function(error) {
      reject(error);
    };
    reader.readAsDataURL(file);
  });
}

const addUsuario = document.getElementById('regUserButton');
addUsuario.onclick = async (event) => {
  event.preventDefault(); // Evitar que el formulario recargue la página

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
